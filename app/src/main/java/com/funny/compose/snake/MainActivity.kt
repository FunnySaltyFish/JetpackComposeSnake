package com.funny.compose.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.funny.compose.snake.ui.theme.JetpackComposeSnakeTheme
import com.funny.compose.snake.ui.SnakeGame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeSnakeTheme {
                SnakeGame()
            }
        }
    }
}
