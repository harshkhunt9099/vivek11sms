package com.zone.messanger.text.PANDA_ads

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class WallpaperItem(
    val imageUrl: String = "",
    val thumbUrl: String = "",
) : Parcelable