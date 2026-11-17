package com.zone.messanger.text.PANDA_ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.zone.messanger.text.PANDA_ads.AdsManager.fetchRemoteConfigData
import com.zone.messanger.text.Data.Smartly_Extension.config

object AdsConfigManager {
    private const val REMOTE_CONFIG_FETCH_INTERVAL =
        0L

    fun tryFetch(
        context: Context,
        onComplete: () -> Unit,
    ) {
        if (!AdsManager.isNetworkAvailable(context)) {
            onComplete()
            return
        }

        initMobileAds(context) { }

        if (!context.config.canFetchRemoteConfig(REMOTE_CONFIG_FETCH_INTERVAL)) {
            Log.d("AdsConfig", "Using cached RemoteConfig")
            onComplete()
            return
        }

        fetchRemoteConfigData(context, object : GAds.OnAdAction {
            override fun onCallBack() {
                Log.e("AdsConfigxxfxf11111111",
                    context.config.canFetchRemoteConfig(REMOTE_CONFIG_FETCH_INTERVAL).toString()
                );
                context.config.setLastRemoteConfigFetchTime(0)
                onComplete()
            }
        })
    }

    fun initMobileAds(context: Context, onInitialize: () -> Unit) {
        try {
            val mobileAds = MobileAds.getInitializationStatus()
            if (mobileAds != null && mobileAds.adapterStatusMap.isNotEmpty()) {
                onInitialize()
                return
            }
            MobileAds.initialize(context) {
                onInitialize()
            }
        } catch (e: Exception) {
            MobileAds.initialize(context) {
                onInitialize()
            }
            Log.e("TAG", "initMobileAds: ${e.message}")
        }

    }
}