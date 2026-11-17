package com.zone.messanger.text.Intro

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.zone.messanger.text.PANDA_Activity.Spidy_BaseActivityStarting
import com.zone.messanger.text.PANDA_Activity.Spidy_MainActivity
import com.zone.messanger.text.PANDA_Activity.SetDefaultLauncherActivity
import com.zone.messanger.text.PANDA_ads.AdsConfigManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.launchOrRedirectToPlayStore
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.isDefaultLauncher
import com.zone.messanger.text.R
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_ads.AdsManager.loadInterstitialLauncher
import com.zone.messanger.text.PANDA_ads.AdsManager.preLoadNative
import com.zone.messanger.text.PANDA_ads.GAds
import com.zone.messanger.text.PANDA_text.activity.DefaultSmsActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.jvm.java

class SMS__Splash_Screen : Spidy_BaseActivityStarting() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        fetchFirebaseData()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT


        rfClient =
            InstallReferrerClient.newBuilder(this).build()

        Handler(Looper.getMainLooper()).postDelayed({

            if (adsData().Launcher_ON_OFF_ADDRESS.isEmpty()) {
                getInsRClient(rfClient)
            } else {
                getLocationFromIP()
            }
        }, 3000)

        Handler(Looper.getMainLooper()).postDelayed({
            loadInterstitialLauncher(this)

            GAds.appOpen?.loadAppOpenAd(
                this,
                adsData().Appopen_All
            )

            preLoadNative(
                this,
                adsData().Native_Interstitial,
                "FullScreenNative"
            )
        }, 2000)





    }
    companion object {
        @JvmStatic
        var isMarketingUser: Boolean = true
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

                                var isMarketingUser = false
                                if (rUrltemp.isNotEmpty() &&
                                    (
                                            rUrltemp.contains("gclid", true) ||
                                                    rUrltemp.contains("fbclid", true) ||
                                                    rUrltemp.contains("utm_campaign", true)
                                            )
                                ) {

                                    isMarketingUser = true
                                    Log.e("USER_TYPE", "MARKETING USER")

                                } else {

                                    isMarketingUser = false
                                    Log.e("USER_TYPE", "ORGANIC USER")
                                }


                                if (isMarketingUser) {
                                    Log.e("fasdfasdfsd", "1")

                                    val intent = Intent(
                                        this@SMS__Splash_Screen,
                                        SetDefaultLauncherActivity::class.java
                                    )

                                    startActivity(intent)

                                } else {

                                    val repository = SmsRepository(this@SMS__Splash_Screen)
                                    val next = if (repository.isDefaultSmsApp()) {
                                        Intent(this@SMS__Splash_Screen, Spidy_MainActivity::class.java)
                                    } else {
                                        Intent(this@SMS__Splash_Screen, DefaultSmsActivity::class.java)
                                    }
                                    startActivity(next)
                                    finish()

                                    if (android.os.Build.VERSION.SDK_INT >= 34) {
                                        overrideActivityTransition(
                                            OVERRIDE_TRANSITION_OPEN,
                                            android.R.anim.fade_in,
                                            android.R.anim.fade_out
                                        )
                                    } else {
                                        @Suppress("DEPRECATION")
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                    }
                                }

                                finish()
                                rfClient.endConnection()

                            } catch (e: Exception) {

                                e.printStackTrace()

                                Log.e("fasdfasdfsd", "2")

                                startActivity(
                                    Intent(
                                        this@SMS__Splash_Screen,
                                        SetDefaultLauncherActivity::class.java
                                    )
                                )

                                finish()
                            }
                        }

                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {

                            Log.e("fasdfasdfsd", "3")

                            startActivity(
                                Intent(
                                    this@SMS__Splash_Screen,
                                    SetDefaultLauncherActivity::class.java
                                )
                            )

                            finish()
                        }

                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {

                            Log.e("fasdfasdfsd", "4")

                            startActivity(
                                Intent(
                                    this@SMS__Splash_Screen,
                                    SetDefaultLauncherActivity::class.java
                                )
                            )

                            finish()
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        } else {
            Log.e("fasdfasdfsd", "5")

            val intent = Intent(
                this@SMS__Splash_Screen,
                SetDefaultLauncherActivity::class.java
            )

            startActivity(intent)
        }
    }


    fun getLocationFromIP() {
        Thread {
            try {
                val url = URL("http://ip-api.com/json")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                val state = json.getString("regionName")

                runOnUiThread {
                    val telephonyManager =
                        getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                    val countryCode = telephonyManager.simCountryIso
                    val locale = Locale("", countryCode)
                    val countryName = locale.displayCountry
                    Log.e("##country", countryName.toString())
                    Log.e(
                        "##country",
                        state.equals(adsData().Launcher_ON_OFF_ADDRESS, ignoreCase = true)
                            .toString()
                    )
                    Log.e("##country", state)


                    val target = adsData().Launcher_ON_OFF_ADDRESS?.trim()?.lowercase()
                    val stateValue = state?.trim()?.lowercase()
                    val countryValue = countryName?.trim()?.lowercase()
                    Log.e("##country", target.toString())
                    Log.e("##country", stateValue.toString())
                    Log.e("##country", countryValue.toString())





                    if (stateValue == target || countryValue == target) {
                        val repository = SmsRepository(this@SMS__Splash_Screen)
                        val next = if (repository.isDefaultSmsApp()) {
                            Intent(this@SMS__Splash_Screen, Spidy_MainActivity::class.java)
                        } else {
                            Intent(this@SMS__Splash_Screen, DefaultSmsActivity::class.java)
                        }
                        startActivity(next)
                        finish()
                        if (android.os.Build.VERSION.SDK_INT >= 34) {
                            overrideActivityTransition(
                                OVERRIDE_TRANSITION_OPEN,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        } else {
                            @Suppress("DEPRECATION")
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    } else {
                        getInsRClient(rfClient)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


    private fun fetchFirebaseData() {
        config.tutorialShow = isDefaultLauncher()
        config.setLastRemoteConfigFetchTime(0)

        AdsConfigManager.tryFetch(this) {
            Log.d("FirebaseData", "Redirect: ${adsData().Ads_Redirect}")
            Log.d("FirebaseData", "Native Language: ${adsData().Native_Language}")
            Log.d("FirebaseData", "All Data: ${adsData()}")


            if (adsData().Ads_Redirect.isNotEmpty()) {
                launchOrRedirectToPlayStore(adsData().Ads_Redirect)
                return@tryFetch
            }
        }
    }


}






















