package com.funny.compose.snake.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedList

object PackageUtils {
    suspend fun getInstalledApps(pm: PackageManager): List<String> = withContext(Dispatchers.Default) {
        val apps = mutableListOf<String>()
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        } else {
            pm.queryIntentActivities(intent, 0)
        }
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            apps.add(packageName)
        }
        return@withContext apps
    }

    suspend fun loadAppIcons(context: Context, packageNames: List<String>): List<ImageBitmap> =
        withContext(Dispatchers.Default) {
            val icons = LinkedList<ImageBitmap>()
            Log.d("PackageUtils", "loadAppIcons: packageNames(${packageNames.size}): $packageNames")
            val myPackage = context.packageName
            for (packageName in packageNames) {
                try {
                    val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
                    val iconDrawable = context.packageManager.getApplicationIcon(appInfo)
                    val bitmap = iconDrawable.toBitmap()
                    val imageBitmap = bitmap.asImageBitmap()
                    if (packageName != myPackage) {
                        icons.add(imageBitmap)
                    } else {
                        icons.add(0, imageBitmap)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    // Handle error
                }
            }
            return@withContext icons
        }
}