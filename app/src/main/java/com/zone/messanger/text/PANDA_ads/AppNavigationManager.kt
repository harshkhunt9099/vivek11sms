package com.zone.messanger.text.PANDA_ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.WindowManager
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.zone.messanger.text.Data.Smartly_Extension.config

object AppNavigationManager {

    private lateinit var sharedPreferences: SharedPreferences

    fun handleAppNavigation(
        activity: Activity,
        appNavigationCount: Int,
        isAppNavigationShow: Boolean,
    ) {
        activity.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val shouldShow =
                if (activity.config.navigationUserOverride) {
                    activity.config.showNavigationBar
                } else {
                    shouldShowAppNavigation(
                        activity,
                        appNavigationCount,
                        isAppNavigationShow
                    )
                }

            val insetsController = WindowCompat.getInsetsController(this, decorView)


            if (shouldShow) {
                insetsController.show(WindowInsetsCompat.Type.navigationBars())
            } else {
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        }
    }


    private fun shouldShowAppNavigation(
        activity: Activity,
        appNavigationCount: Int,
        isAppNavigationShow: Boolean,
    ): Boolean {

        if (!::sharedPreferences.isInitialized) {
            sharedPreferences =
                activity.getSharedPreferences("AppNavigationManagerPref", Context.MODE_PRIVATE)
        }

        return if (isAppNavigationShow) {
            !shouldShowNavigationBar(appNavigationCount)
        } else {
            shouldShowNavigationBar(appNavigationCount)
        }
    }

    private fun shouldShowNavigationBar(count: Int): Boolean {
        Log.e("adds", "count: $count")

        if (count == 0) return false
        if (count == 1) return toggleShowHide()

        val currentStep = getAndIncreaseStep()

        Log.e("adds", "currentStep: $currentStep")

        return (currentStep + 1) % (count + 1) == 0
    }

    private fun toggleShowHide(): Boolean {
        val step = getAndIncreaseStep()
        return step % 2 != 0
    }

    private fun getAndIncreaseStep(): Int {
        val step = sharedPreferences.getInt("nav_step", 0)
        sharedPreferences.edit { putInt("nav_step", step + 1) }
        return step
    }


}
