package com.funny.compose.snake.beans

import androidx.compose.runtime.MutableState
import com.funny.compose.snake.App.Companion.DataSaverUtils
import com.funny.data_saver.core.mutableDataSaverStateOf


object ThemeConfig {
    val themeList = listOf(
        SnakeAssets.SnakeAssets1, SnakeAssets.SnakeAssets2
    )
    val savedSnakeAssets: MutableState<SnakeAssets> = mutableDataSaverStateOf(DataSaverUtils ,key = "saved_snake_assets", initialValue = SnakeAssets.SnakeAssets1)
}