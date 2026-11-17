package com.zone.messanger.text.PANDA_ads

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.appopen.AppOpenAd
import com.zone.messanger.text.PANDA_ads.AdsManager.isFromPermission
import com.zone.messanger.text.PANDA_ads.AdsManager.isFromPromotionalContent
import com.zone.messanger.text.PANDA_ads.AdsManager.isInterShowing
import com.google.android.gms.ads.AdValue
import com.google.firebase.analytics.FirebaseAnalytics
import com.launcher.launcherpackage.Codebizz_activity.Codebizz_PackageMonitorDisableConfirmationActivity
import java.util.Date

class GAppOpen(adsApp: AdsApp) : ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    companion object {
        private const val TAG = "GAppOpen2"
    }

    interface AppOpenResult {
        fun isAppOpenAdShown(shown: Boolean)
    }

    var appOpenAd: AppOpenAd? = null

    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null
    var appOpenResult: AppOpenResult? = null

    init {
        Log.e(TAG, ": init")
        adsApp.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        Log.e(TAG, "onActivityStarted: ${activity.javaClass.simpleName}")
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.e(TAG, "onStart: ")

        // Check if activity should skip app open ad
        val shouldSkip = currentActivity == null ||
                currentActivity is Codebizz_PackageMonitorDisableConfirmationActivity ||
                isFromPromotionalContent ||
                isFromPermission ||
                isInterShowing

        if (shouldSkip) {

            isFromPermission = false
            isFromPromotionalContent = false

            Log.e(TAG, "onStart: return")
            return
        }

        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            Log.e(TAG, "onStart: ${e.message}")
        }

//        showAppOpenAd(object : AppOpenResult {
//            override fun isAppOpenAdShown(shown: Boolean) {
//                Log.e(TAG, "isAppOpenAdShown: $shown")
//            }
//        })
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.e(TAG, "onStop: ")

        // Check if activity should skip app open ad loading
        if (currentActivity is Codebizz_PackageMonitorDisableConfirmationActivity
        ) {
            return
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            loadAppOpenAd(currentActivity!!, adsData().Appopen_Resume)
//        }, 2000)
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference = (Date()).time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return (dateDifference < (numMilliSecondsPerHour * 4L))
    }

    fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    fun logAppOpenAdImpression(
        context: Context?,
        adValue: AdValue?,
        appOpenAd: AppOpenAd?
    ) {
        try {
            if (!adsData().logadevent) return
            if (context == null || adValue == null || appOpenAd == null) return

            val params = Bundle().apply {
                putString(FirebaseAnalytics.Param.AD_UNIT_NAME, appOpenAd.adUnitId)
                putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
                putDouble(
                    FirebaseAnalytics.Param.VALUE,
                    adValue.valueMicros / 1_000_000.0
                )
                putString(FirebaseAnalytics.Param.AD_FORMAT, "App Open")
                putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager")
                putString(
                    FirebaseAnalytics.Param.AD_SOURCE,
                    appOpenAd.responseInfo.mediationAdapterClassName
                )
            }

            FirebaseAnalytics.getInstance(context)
                .logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun loadAppOpenAd(context: Context, adId: String) {
        Log.e(TAG, "loadAppOpenAd: ")

//        if (isLoadingAd || isAdAvailable()) {
//            Log.e(TAG, "loadAppOpenAd isLoadingAd: $isLoadingAd isAdAvailable: ${isAdAvailable()}")
//            return
//        }
        Log.e(TAG, "loadAppOpenAd: $adId")

        if (adId.isEmpty()) {
            return
        }

        isLoadingAd = true
        Log.e(TAG, "loadAppOpenAd isLoadingAd: true")


        val request = AdManagerAdRequest.Builder().build()
        AppOpenAd.load(context, adId, request, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                Log.e(TAG, "onAdLoaded: ")
                appOpenAd = ad
                isLoadingAd = false
                loadTime = (Date()).time


                ad.setOnPaidEventListener { adValue ->
                    logAppOpenAdImpression(context,adValue,ad)
                }

                appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        Log.e(TAG, "onAdDismissedFullScreenContent: ")

                        this@GAppOpen.appOpenResult?.isAppOpenAdShown(true)
                        appOpenAd = null
                        isShowingAd = false
//                        Handler(Looper.getMainLooper()).postDelayed(
//                            { loadAppOpenAd(context, adsResponse.Appopen_Resume) },
//                            AdsManager.APP_OPEN_INTERVAL_AFTER_SHOW.toLong()
//                        )
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        Log.e(TAG, "onAdFailedToShowFullScreenContent: ")
                        this@GAppOpen.appOpenResult?.isAppOpenAdShown(false)
                        appOpenAd = null
                        isShowingAd = false
//                        Handler(Looper.getMainLooper()).postDelayed(
//                            { loadAppOpenAd(context, adsResponse.Appopen_Resume) },
//                            AdsManager.APP_OPEN_INTERVAL_AFTER_SHOW.toLong()
//                        )
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        Log.e(TAG, "onAdShowedFullScreenContent: ")
                    }
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                Log.e(TAG, "onAdFailedToLoad: ${adError.message}")
                isLoadingAd = false
//                Handler(Looper.getMainLooper()).postDelayed(
//                    { loadAppOpenAd(context, adsResponse.Appopen_Resume) },
//                    AdsManager.APP_OPEN_INTERVAL_AFTER_FAIL.toLong()
//                )
            }
        })
    }

    fun showAppOpenAd(appOpenResult: AppOpenResult) {
        Log.e(TAG, "showAppOpenAd: ")
        this@GAppOpen.appOpenResult = appOpenResult

        if (isShowingAd || !isAdAvailable()) {
            Log.e(TAG, "The app open ad is already showing.")
            this@GAppOpen.appOpenResult?.isAppOpenAdShown(false)
            return
        }

        isShowingAd = true
        currentActivity?.let {
            Log.e(TAG, "showAppOpenAd: ${it.javaClass.simpleName}")
            appOpenAd?.show(it)
        }
    }
}