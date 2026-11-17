package com.zone.messanger.text.PANDA_ads

import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
data class OfflineLauncherApp(
    val label: String,
    val packageName: String,
    val icon: Drawable
)