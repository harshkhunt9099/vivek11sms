package com.zone.messanger.text.InterFaces

import com.zone.messanger.text.Models.AppLauncher

interface AllAppsListener {
    fun onAppLauncherLongPressed(x: Float, y: Float, appLauncher: AppLauncher)
}
