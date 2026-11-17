package com.zone.messanger.text.PANDA_ads

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorPrefManager
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorLogger
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_Activity.SetDefaultLauncherActivity
import com.zone.messanger.text.Intro.SMS__Splash_Screen
import com.zone.messanger.text.R


class MyApplication : AdsApp() {

    companion object {
        lateinit var logger: AppEventsLogger
        var firebaseLogAnalytics: FirebaseAnalytics? = null
        var context: MyApplication? = null
        @Volatile
        private var instanceRef: MyApplication? = null

        fun getInstance(): MyApplication? = instanceRef
        const val CHANNEL_ID_MESSAGES = "messages_channel"
    }

    override fun onCreate() {
        super.onCreate()
        instanceRef = this

        context = this
        rfClient =
            InstallReferrerClient.newBuilder(this).build()
        getInsRClient(rfClient)
        Log.e("isMarketingUser111111", SMS__Splash_Screen.isMarketingUser.toString())


        createNotificationChannel()






        //TODO Meta Logger
        logger = AppEventsLogger.newLogger(this)

        firebaseLogAnalytics = FirebaseAnalytics.getInstance(this)

        val modeValue = if (config.isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(modeValue)

        try {
            MobileAds.initialize(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        firebaseLogAnalytics?.let {
            Codebizz_PackageMonitorLogger.init(
                analytics = it,
                logger = logger
            )
        }

        //TODO For Package Monitor
        Codebizz_PackageMonitorPrefManager.init(context = this)

        //TODO Clarity
        val config = ClarityConfig(
            projectId = "example",
            logLevel = LogLevel.Verbose // default is None
        )
        Clarity.initialize(applicationContext, config)



        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->

            try {
                // Crash thay tyare Default Launcher Activity open karo
                val intent = Intent(
                    this,
                    SetDefaultLauncherActivity::class.java
                ).apply {
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                }

                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()

                // Jo recovery pan fail thay to original crash handler
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }


    private lateinit var rfClient: InstallReferrerClient

    private fun getInsRClient(rfClient: InstallReferrerClient) {
        if (adsData().Organic_Marketing_Both) {
            rfClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {

                    when (responseCode) {

                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            try {
                                val response: ReferrerDetails =
                                    rfClient.installReferrer
                                val rUrltemp =
                                    response.installReferrer

                                Log.e("ReferrerData2xxxxxasdff", rUrltemp)

                                // =========================
                                // ORGANIC / MARKETING CHECK
                                // =========================


                                if (

                                    rUrltemp.isNotEmpty() &&
                                    (
                                            rUrltemp.contains("gclid", true) ||
                                                    rUrltemp.contains("fbclid", true) ||
                                                    rUrltemp.contains("utm_campaign", true)
                                            )
                                ) {

                                    SMS__Splash_Screen.isMarketingUser = true
                                    Log.e("ReferrerData2xxxxxasdff",
                                        SMS__Splash_Screen.isMarketingUser.toString()
                                    )

                                } else {

                                    SMS__Splash_Screen.isMarketingUser = false
                                }





                                rfClient.endConnection()

                            } catch (e: Exception) {


                            }
                        }

                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            SMS__Splash_Screen.isMarketingUser = true

                        }

                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                            SMS__Splash_Screen.isMarketingUser = true

                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        } else {
            SMS__Splash_Screen.isMarketingUser = true
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_MESSAGES,
                getString(R.string.messages_title),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.settings_notifications)
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

}