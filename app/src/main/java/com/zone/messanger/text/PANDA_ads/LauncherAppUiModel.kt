package com.zone.messanger.text.PANDA_ads

import android.graphics.drawable.Drawable
import androidx.annotation.Keep

@Keep
data class LauncherAppUiModel(
    val title: String,
    val iconUrl: String? = null,
    val iconDrawable: Drawable? = null,
    val packageName: String? = null
)