package com.launcher.launcherpackage.Codebizz_helper

import android.annotation.SuppressLint
import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics

object Codebizz_PackageMonitorLogger {

    private var analytics: FirebaseAnalytics? = null
    var logger: AppEventsLogger? = null

    @SuppressLint("MissingPermission")
    fun init(analytics: FirebaseAnalytics,logger: AppEventsLogger?=null) {
        this.analytics = analytics
        this.logger = logger
    }

    // 1️⃣ Log event with a simple string
    fun logEvent(eventName: String, eventValue: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.VALUE, eventValue)
            }
            analytics?.logEvent(eventName, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 2️⃣ Log event with a Bundle (for revenue or custom parameters)
    fun logEvent(eventName: String, params: Bundle) {
        try {
            analytics?.logEvent(eventName, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}