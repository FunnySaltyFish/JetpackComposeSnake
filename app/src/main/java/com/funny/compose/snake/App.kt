package com.funny.compose.snake

import android.app.Application
import com.funny.compose.snake.beans.SnakeAssets
import com.funny.data_saver.core.DataSaverConverter
import com.funny.data_saver.core.DataSaverInterface
import com.funny.data_saver.core.DataSaverPreferences

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ctx = this
        DataSaverUtils = DataSaverPreferences(this)

        // SnakeAssets 使我们自定义的类型，因此先注册一下转换器，能让它保存时自动转化为 String，读取时自动恢复
        DataSaverConverter.registerTypeConverters(save = SnakeAssets.Saver, restore = SnakeAssets.Restorer)
    }

    companion object {
        lateinit var ctx: Application
        lateinit var DataSaverUtils: DataSaverInterface
    }
}