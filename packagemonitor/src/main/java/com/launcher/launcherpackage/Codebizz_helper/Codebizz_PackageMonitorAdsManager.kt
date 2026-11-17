package com.launcher.launcherpackage.Codebizz_helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.appevents.AppEventsConstants
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.launcher.launcherpackage.R
import com.launcher.launcherpackage.databinding.PmNativeBinding
import com.launcher.launcherpackage.databinding.PmWebBannerBinding

object Codebizz_PackageMonitorAdsManager {

    private const val TAG = "PMAdsManager"

    fun loadAndShowNative(
        activity: Activity,
        container: FrameLayout,
    ) {
        if (!isNetworkAvailable(activity)) return

        if (Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native.isEmpty()) {
            showWebNative(context = activity, container = container)
            return
        }

        val loaderLay = R.layout.pm_native_loader
        val loader =
            LayoutInflater.from(activity).inflate(loaderLay, container, false) as LinearLayout
        container.removeAllViews()
        container.addView(loader)

        val builder = AdLoader.Builder(
            activity,
            Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native
        )
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adLoader = builder.forNativeAd {
            it.setOnPaidEventListener { adValue ->
                sendAdImpressionRevenueToFirebase(
                    adValue = adValue,
                    adUnitId = Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native
                )
            }

            inflateNative(activity, container, it)
        }.withAdListener(object : AdListener() {

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                Log.e(TAG, "Native onAdFailedToLoad: " + error.message)

                container.removeAllViews()

                if (Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Banner_Inline.isEmpty()) {
                    showWebNative(context = activity, container = container)
                    return
                }

                loadAndShowInlineBanner(activity = activity, container = container)
            }

            override fun onAdClicked() {
                super.onAdClicked()

                if (!Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native_All_Click2Change) {
                    return
                }

                clickToChangeNative(activity = activity, container = container)

            }

        }).withNativeAdOptions(NativeAdOptions.Builder().setVideoOptions(videoOptions).build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun clickToChangeNative(
        activity: Activity,
        container: FrameLayout,
    ) {
        val builder = AdLoader.Builder(
            activity,
            Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native
        )
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adLoader = builder.forNativeAd {
            it.setOnPaidEventListener { adValue ->
                sendAdImpressionRevenueToFirebase(
                    adValue = adValue,
                    adUnitId = Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native
                )
            }

            inflateNative(activity, container, it)
        }.withAdListener(object : AdListener() {

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                Log.e(TAG, "Native onAdFailedToLoad: " + error.message)
                container.removeAllViews()

                if (Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Banner_Inline.isEmpty()) {
                    showWebNative(context = activity, container = container)
                    return
                }

                loadAndShowInlineBanner(activity = activity, container = container)
            }

            override fun onAdClicked() {
                super.onAdClicked()

                clickToChangeNative(activity = activity, container = container)
            }

        }).withNativeAdOptions(NativeAdOptions.Builder().setVideoOptions(videoOptions).build())
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    fun loadAndShowInlineBanner(
        activity: Activity,
        container: FrameLayout,
    ) {

        if (!isNetworkAvailable(activity)) return

        if (Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Banner_Inline.isEmpty()) {
            showWebNative(context = activity, container = container)
            return
        }

        val adView = AdView(activity)

        val loaderLayout = R.layout.pm_inline_banner_loader

        val loader =
            LayoutInflater.from(activity)
                .inflate(loaderLayout, container, false) as ConstraintLayout
        container.removeAllViews()

        container.addView(loader)

        adView.adUnitId =
            Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Banner_Inline
        val adRequest = AdRequest.Builder().build()

        adView.setOnPaidEventListener { adValue ->
            sendAdImpressionRevenueToFirebase(
                adValue = adValue, adUnitId = adView.adUnitId
            )
        }

        val adSize = getInlineAdaptiveBannerAdSize(activity)

        adView.setAdSize(adSize)
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Log.e(TAG, "Banner onAdFailedToLoad: " + loadAdError.message)
                container.removeAllViews()

                showWebNative(context = activity, container = container)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                showHomeBanner(container, adView)
                Log.e(TAG, "Banner onAdLoaded: ")
            }

            override fun onAdClicked() {
                super.onAdClicked()

                if (!Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native_All_Click2Change) {
                    return
                }

                loadAndShowNative(activity = activity, container = container)
            }
        }
    }

    private fun showHomeBanner(container: FrameLayout, homeBannerView: AdView?) {
        if (homeBannerView != null) {
            // Remove from old parent if exists
            if (homeBannerView.parent != null) {
                (homeBannerView.parent as ViewGroup).removeView(homeBannerView)
            }
            container.removeAllViews()

            // Set layout params to center the AdView
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.CENTER

            container.addView(homeBannerView, params)
        }
    }

    private fun getInlineAdaptiveBannerAdSize(activity: Context): AdSize {
        val displayMetrics = activity.resources.displayMetrics
        val adWidthPixels = displayMetrics.widthPixels - 24.dpToPx(
            activity
        )

        val width = displayMetrics.widthPixels
        Log.e(TAG, "getInlineAdaptiveBannerAdSize: $adWidthPixels $width")
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(activity, adWidth)
    }

    @SuppressLint("DefaultLocale")
    fun sendAdImpressionRevenueToFirebase(
        adValue: AdValue,
        adUnitId: String,
    ) {
        sendAdRevenueToMeta(adValue, adUnitId)

        val revenue = adValue.valueMicros / 1_000_000.0
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
            putDouble(FirebaseAnalytics.Param.VALUE, revenue)
            putString("formatted_revenue", String.format("%.6f", revenue))
            putInt("precision", adValue.precisionType)
            putString("ad_unit_id", adUnitId)
        }

        if (!adUnitId.contains("ca-app-pub")) {
            Codebizz_PackageMonitorLogger.logEvent("pm_ad_impression", params)
        }

        Log.e(TAG, "sendAdImpressionRevenueToFirebase: $params")
    }

    fun sendAdRevenueToMeta(adValue: AdValue, adUnitId: String) {

        val revenue = adValue.valueMicros / 1_000_000.0
        val params = Bundle().apply {
            putString(AppEventsConstants.EVENT_PARAM_CURRENCY, adValue.currencyCode)
            putString("formatted_revenue", String.format("%.6f", revenue))
            putInt("precision", adValue.precisionType)
            putString("ad_unit_id", adUnitId)
        }


        Codebizz_PackageMonitorLogger.logger?.logEvent(
            AppEventsConstants.EVENT_NAME_AD_IMPRESSION,
            revenue,
            params
        )

        Log.e(
            TAG,
            "sendAdRevenueToMeta: $params $revenue"
        )
    }

    private fun inflateNative(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = PmNativeBinding.inflate(LayoutInflater.from(context))

        val nativeAdView = binding.root
        nativeAdView.headlineView = binding.headlineView
        nativeAdView.bodyView = binding.bodyView
        nativeAdView.callToActionView = binding.callToActionView
        nativeAdView.iconView = binding.iconView
        nativeAdView.mediaView = binding.mediaView

        binding.headlineView.text = nativeAd.headline
        nativeAd.mediaContent?.let { binding.mediaView.mediaContent = it }

        if (nativeAd.icon == null) {
            binding.iconView.visibility = View.GONE
        } else {
            binding.iconView.setImageDrawable(nativeAd.icon?.drawable)
            binding.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.body == null) {
            binding.bodyView.visibility = View.INVISIBLE
        } else {
            binding.bodyView.visibility = View.VISIBLE
            binding.bodyView.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            binding.callToActionView.visibility = View.INVISIBLE
        } else {
            binding.callToActionView.visibility = View.VISIBLE
            binding.callToActionView.text = nativeAd.callToAction
        }

        nativeAdView.setNativeAd(nativeAd)
        container.removeAllViews()
        container.addView(binding.root)
    }

    fun showWebNative(
        context: Context,
        container: FrameLayout,
    ) {

        val webList =
            Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Native_All_Fail_Array

        if (webList.isEmpty()) {
            container.removeAllViews()
            container.background = null
            return
        }

        val randomAd = webList.random()

        val binding = PmWebBannerBinding.inflate(
            LayoutInflater.from(context),
            container,
            false
        )

        try {
            Glide.with(context)
                .load(randomAd.image_url).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        container.removeAllViews()
                        container.background = null

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        binding.ivImage.setImageDrawable(resource)

                        container.removeAllViews()
                        container.background = null

                        container.addView(binding.root)
                        return false
                    }
                })
                .preload()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.root.setPerfectClickListener(onPerfectClick = {
            openCustomTabIntent(context, randomAd.click_url)
        })
    }


    fun openCustomTabIntent(context: Context, url: String) {

        if (url.isBlank()) return

        val windowInsetsController =
            WindowCompat.getInsetsController((context as Activity).window, context.window.decorView)

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.show(WindowInsetsCompat.Type.navigationBars())

        val uri = url.toUri()

        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)

            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()

        customTabsIntent.intent.setPackage("com.android.chrome")

        try {
            customTabsIntent.launchUrl(context, uri)
        } catch (e: ActivityNotFoundException) {
            Log.e("axxx", "openCustomTabIntent: " + e.message)

            e.printStackTrace()
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("axxx", "openCustomTabIntent: " + ex.message)

            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.activeNetwork?.let { it ->
            connectivityManager.getNetworkCapabilities(it)?.let {
                return it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }

        return false
    }


}