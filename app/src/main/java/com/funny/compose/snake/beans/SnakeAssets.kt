package com.funny.compose.snake.beans

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.funny.cmaterialcolors.MaterialColors
import com.funny.compose.snake.R
import com.funny.compose.snake.utils.PackageUtils
import com.funny.compose.snake.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class SnakeAssets(
    val lineColor: Color = Color.LightGray.copy(alpha = 0.8f),
) {
    sealed class Colored(
        lineColor: Color = Color.LightGray.copy(alpha = 0.8f),
        val foodColor: Color = MaterialColors.Orange700,
        val headColor: Color = MaterialColors.Red700,
        val bodyColor: Color = MaterialColors.Blue200
    ) : SnakeAssets(lineColor) {
        object Style1 : Colored()

        object Style2 : Colored(
            foodColor = MaterialColors.Purple700,
            lineColor = MaterialColors.Brown200.copy(alpha = 0.8f),
            headColor = MaterialColors.Blue700,
            bodyColor = MaterialColors.Pink300
        )
    }

    object Icon: SnakeAssets() {
        private val iconList = arrayListOf<ImageBitmap>()

        var initialized by mutableStateOf(false)
            private set

        suspend fun init(context: Context) {
            if (initialized) return

            iconList.clear()
            withContext(Dispatchers.Main) {
                context.toast(R.string.loading_app_icons)
            }
            iconList.addAll(PackageUtils.loadAppIcons(context, PackageUtils.getInstalledApps(context.packageManager)))
            initialized = true
            withContext(Dispatchers.Main) {
                context.toast(R.string.load_app_icons_success)
            }
        }

        fun getIcon(index: Int) = iconList[index % iconList.size]
    }

    override fun toString(): String {
        return this.javaClass.simpleName
    }

    companion object {
        val Saver = { assets: SnakeAssets ->
            assets.javaClass.simpleName
        }
        val Restorer = { str: String ->
            when (str) {
                "SnakeAssets1" -> Colored.Style1
                "SnakeAssets2" -> Colored.Style2
                "Icon" -> Icon
                else -> Colored.Style1
            }
        }
    }
}