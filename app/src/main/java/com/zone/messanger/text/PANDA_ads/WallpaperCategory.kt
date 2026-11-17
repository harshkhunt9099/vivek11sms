package com.zone.messanger.text.PANDA_ads

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class WallpaperCategory(
    val categoryName: String = "",
    val wallpapers: List<WallpaperItem> = emptyList()
) : Parcelable