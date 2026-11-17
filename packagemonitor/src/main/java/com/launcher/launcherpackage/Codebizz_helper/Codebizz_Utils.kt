package com.launcher.launcherpackage.Codebizz_helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import java.io.File
import kotlin.math.abs

fun formatSize(bytes: Long): String {
    val kb = 1024
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        bytes >= gb -> String.format("%.2f GB", bytes.toFloat() / gb)
        bytes >= mb -> String.format("%.2f MB", bytes.toFloat() / mb)
        bytes >= kb -> String.format("%.2f KB", bytes.toFloat() / kb)
        else -> "$bytes B"
    }
}

fun getAppInfo(context: Context, packageName: String): Triple<String, Drawable?, String> {
    val pm = context.packageManager

    return try {
        val appInfo = pm.getApplicationInfo(packageName, 0)

        // App Name
        val appName = pm.getApplicationLabel(appInfo).toString()

        // App Logo (Icon)
        val appIcon = pm.getApplicationIcon(appInfo)

        val appSize = formatSize(File(appInfo.sourceDir).length())

        Triple(appName, appIcon, appSize)

    } catch (e: PackageManager.NameNotFoundException) {
        Triple("", null, "")
    }
}



@SuppressLint("ClickableViewAccessibility")
fun View.setPerfectClickListener(
    maxClickDuration: Long = 500L,
    maxMoveDistance: Int = 20,     // prevent drag/slight swipe
    onPerfectClick: () -> Unit,
) {
    var downTime = 0L
    var downX = 0f
    var downY = 0f
    var isMoved = false

    this.setOnTouchListener { _, event ->
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                downTime = System.currentTimeMillis()
                downX = event.rawX
                downY = event.rawY
                isMoved = false
                true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs(event.rawX - downX)
                val dy = abs(event.rawY - downY)

                // If user drags more than allowed, mark as moved
                if (dx > maxMoveDistance || dy > maxMoveDistance) {
                    isMoved = true
                }
                true
            }

            MotionEvent.ACTION_UP -> {
                val duration = System.currentTimeMillis() - downTime

                if (!isMoved && duration <= maxClickDuration) {
                    onPerfectClick()
                }
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                isMoved = true
                true
            }

            else -> false
        }
    }
}


