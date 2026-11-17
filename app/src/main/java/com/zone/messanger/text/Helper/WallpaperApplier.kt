package com.zone.messanger.text.Helper

import android.app.WallpaperManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.zone.messanger.text.Models.WallpaperType

object WallpaperApplier {

    fun apply(
        context: Context,
        imageUrl: String,
        type: WallpaperType,
        onResult: (Boolean) -> Unit
    ) {
        Thread {
            try {
                val file = Glide.with(context)
                    .downloadOnly()
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .submit()
                    .get()

                val wallpaperManager = WallpaperManager.getInstance(context)

                when (type) {
                    WallpaperType.HOME -> {
                        file.inputStream().use { stream ->
                            wallpaperManager.setStream(stream, null, true, WallpaperManager.FLAG_SYSTEM)
                        }
                    }
                    WallpaperType.LOCK -> {
                        file.inputStream().use { stream ->
                            wallpaperManager.setStream(stream, null, true, WallpaperManager.FLAG_LOCK)
                        }
                    }
                    WallpaperType.BOTH -> {
                        // Open a fresh stream for each call — never reuse the same stream
                        file.inputStream().use { stream ->
                            wallpaperManager.setStream(stream, null, true, WallpaperManager.FLAG_SYSTEM)
                        }
                        file.inputStream().use { stream ->
                            wallpaperManager.setStream(stream, null, true, WallpaperManager.FLAG_LOCK)
                        }
                    }
                }

                Handler(Looper.getMainLooper()).post { onResult(true) }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post { onResult(false) }
            }
        }.start()
    }
}