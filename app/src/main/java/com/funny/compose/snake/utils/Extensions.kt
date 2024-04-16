package com.funny.compose.snake.utils

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

fun Offset.toIntOffset() = IntOffset(this.x.toInt(), this.y.toInt())

fun Size.toIntSize() = IntSize(this.width.toInt(), this.height.toInt())

fun Context.toast(msg: String, duration: Int = android.widget.Toast.LENGTH_SHORT) {
    android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
}

fun Context.toast(resId: Int, duration: Int = android.widget.Toast.LENGTH_SHORT) {
    android.widget.Toast.makeText(this, resId, android.widget.Toast.LENGTH_SHORT).show()
}