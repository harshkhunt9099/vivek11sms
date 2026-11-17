package com.zone.messanger.text.PANDA_Activity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AppNavigationManager

open class Messanger__BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        val config: Configuration = newBase.resources.configuration
        config.fontScale = 1.0f
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onResume() {
        super.onResume()
        AppNavigationManager.handleAppNavigation(
            this, AdsManager.adsData().Ads_App_Navigation_Count,
            AdsManager.adsData().Ads_App_Navigation
        )
    }
}