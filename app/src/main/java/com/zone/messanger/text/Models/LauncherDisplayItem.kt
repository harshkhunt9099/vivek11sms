package com.zone.messanger.text.Models

import androidx.annotation.Keep

@Keep
sealed class LauncherDisplayItem {
    data class AppItem(val appLauncher: AppLauncher) : LauncherDisplayItem()
    data class Separator(val label: String = "") : LauncherDisplayItem()

    data class HeaderItem(val type: HeaderType) : LauncherDisplayItem()
}

@Keep
enum class HeaderType {
    SET_DEFAULT_LAUNCHER
}