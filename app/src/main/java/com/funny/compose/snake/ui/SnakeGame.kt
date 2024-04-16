package com.funny.compose.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.funny.compose.snake.R
import com.funny.compose.snake.beans.GameAction
import com.funny.compose.snake.beans.GameState
import com.funny.compose.snake.beans.Snake
import com.funny.compose.snake.beans.SnakeAssets
import com.funny.compose.snake.beans.SnakeState
import com.funny.compose.snake.beans.ThemeConfig
import com.funny.compose.snake.utils.toIntOffset
import com.funny.compose.snake.utils.toIntSize
import kotlinx.coroutines.delay

internal val LocalSnakeAssets: ProvidableCompositionLocal<SnakeAssets> = staticCompositionLocalOf { SnakeAssets.Colored.Style1 }
private const val TAG = "SnakeGame"

private sealed class DrawType {
    object Head : DrawType()
    class Body(val index: Int = 0) : DrawType()
    object Food : DrawType()
}

@Composable
fun SnakeGame(
    modifier: Modifier = Modifier
) {
    val vm: SnakeGameViewModel = viewModel()
    val snakeState by vm.snakeState

    LaunchedEffect(key1 = snakeState.gameState) {
        if (snakeState.gameState != GameState.PLAYING) return@LaunchedEffect
        while (true) {
            vm.dispatch(GameAction.GameTick)
            delay(snakeState.getSleepTime())
        }
    }

    val snakeAssets by ThemeConfig.savedSnakeAssets
    CompositionLocalProvider(LocalSnakeAssets provides snakeAssets) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (snakeState.gameState) {
                GameState.PLAYING -> Playing(snakeState, snakeAssets, vm::dispatch)
                GameState.LOST -> Lost(snakeState.getScore(), vm.historyBestScore.value, vm::dispatch)
                GameState.WAITING -> Waiting(vm::dispatch)
            }
        }
    }
}

@Composable
fun ColumnScope.Waiting(dispatchAction: (GameAction) -> Unit) {
    val snakeAssets by ThemeConfig.savedSnakeAssets
    val context = LocalContext.current
    LaunchedEffect(key1 = snakeAssets) {
        if (snakeAssets is SnakeAssets.Icon) {
            // 对于 Icon 类型的资源，需要在这里初始化
            (snakeAssets as SnakeAssets.Icon).init(context = context)
        }
    }

    val enabledButton by remember {
        derivedStateOf {
            (snakeAssets is SnakeAssets.Icon && (snakeAssets as SnakeAssets.Icon).initialized)
                    || snakeAssets is SnakeAssets.Colored
        }
    }

    OutlinedButton(
        onClick = { dispatchAction(GameAction.StartGame) },
        enabled = enabledButton
    ) {
        Text(text = stringResource(R.string.start_game))
    }
    Spacer(modifier = Modifier.height(16.dp))

    var expanded by remember { mutableStateOf(false)  }
    OutlinedButton(onClick = { expanded = true }) {
        Text(text = stringResource(id = R.string.selected_assets, snakeAssets))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ThemeConfig.themeList.forEach { theme ->
                DropdownMenuItem(onClick = {
                    ThemeConfig.savedSnakeAssets.value = theme
                    expanded = false
                }, text = {
                    Text(text = theme.toString())
                })
            }
        }
    }
}

@Composable
fun ColumnScope.Playing(
    snakeState: SnakeState,
    snakeAssets: SnakeAssets,
    dispatchAction: (GameAction) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .square()
            .onSizeChanged {
                val size = it
                dispatchAction(GameAction.ChangeSize(size.width to size.height))
            }
            .detectDirectionalMove {
                dispatchAction(GameAction.MoveSnake(it))
            }
    ) {
        drawBackgroundGrid(snakeState, snakeAssets)
        drawSnake(snakeState, snakeAssets)
        drawFood(snakeState, snakeAssets)
    }
}

@Composable
fun ColumnScope.Lost(
    score: Int,
    bestHistoryScore: Int,
    dispatchAction: (GameAction) -> Unit
) {
    Text(text = stringResource(id = R.string.lost_tip, score, bestHistoryScore), textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedButton(onClick = { dispatchAction(GameAction.RestartGame) }) {
        Text(text = stringResource(R.string.retry), textAlign = TextAlign.Center)
    }
}


private fun DrawScope.drawSnake(snakeState: SnakeState, snakeAssets: SnakeAssets) {
    val size = snakeState.blockSize
    snakeState.snake.body.forEachIndexed { index, point ->
        val offset = point.asOffset(snakeState.blockSize)
        if (point == snakeState.snake.head) {
            drawOneBlock(offset, size, DrawType.Head, snakeAssets, snakeState.snake)
        } else {
            drawOneBlock(offset, size, DrawType.Body(index), snakeAssets, snakeState.snake)
        }
    }
}

private fun DrawScope.drawFood(snakeState: SnakeState, snakeAssets: SnakeAssets) {
    val size = snakeState.blockSize
    val offset = snakeState.food.asOffset(snakeState.blockSize)
    drawOneBlock(offset, size, DrawType.Food, snakeAssets, snakeState.snake)
}

private fun DrawScope.drawBackgroundGrid(snakeState: SnakeState, snakeAssets: SnakeAssets) {
    val (width, height) = snakeState.size
    for (x in 0..width step snakeState.blockSize.width.toInt()) {
        drawLine(
            snakeAssets.lineColor,
            start = Offset(x.toFloat(), 0f),
            end = Offset(x.toFloat(), height.toFloat()),
            strokeWidth = 1f
        )
    }
    for (y in 0..height step snakeState.blockSize.height.toInt()) {
        drawLine(
            snakeAssets.lineColor,
            start = Offset(0f, y.toFloat()),
            end = Offset(width.toFloat(), y.toFloat()),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawOneBlock(offset: Offset, size: Size, drawType: DrawType, snakeAssets: SnakeAssets, snake: Snake) {
    when (snakeAssets) {
        is SnakeAssets.Colored ->
            when (drawType) {
                is DrawType.Head -> drawRect(snakeAssets.headColor, offset, size)
                is DrawType.Body -> drawRect(snakeAssets.bodyColor, offset, size)
                is DrawType.Food -> drawRect(snakeAssets.foodColor, offset, size)
            }

        is SnakeAssets.Icon -> {
            val index = when (drawType) {
                is DrawType.Head -> 0
                is DrawType.Body -> drawType.index
                is DrawType.Food -> snake.body.size
            }
            drawImage(
                image = snakeAssets.getIcon(index),
                dstOffset = offset.toIntOffset(),
                dstSize = size.toIntSize(),
                filterQuality = FilterQuality.Low
            )
        }
    }
}