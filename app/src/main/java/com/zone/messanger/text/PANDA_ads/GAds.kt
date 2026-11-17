package com.zone.messanger.text.PANDA_ads

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.facebook.appevents.AppEventsConstants
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_Activity.WallpaperListActivity
import com.zone.messanger.text.PANDA_ads.AdsManager.isFromPromotionalContent
import com.zone.messanger.text.PANDA_ads.AdsManager.isInterShowing
import com.zone.messanger.text.databinding.GInterLoadingDialogBinding
import com.zone.messanger.text.databinding.GNativeBannerLargeBinding
import com.zone.messanger.text.databinding.GNativeBannerSmallBinding
import com.zone.messanger.text.databinding.GNativeFullScreenBinding
import com.zone.messanger.text.databinding.GNativeLargeBinding
import com.zone.messanger.text.databinding.GNativeLargeLauncherSidePageBinding
import com.zone.messanger.text.databinding.GNativeLargeTopMediaviewBinding
import com.zone.messanger.text.databinding.GNativeSmallBinding
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import org.greenrobot.eventbus.EventBus


@SuppressLint("StaticFieldLeak")
object GAds {
    private const val TAG = "GAds2"

    private var interstitialAdLauncher: InterstitialAd? = null
    private var isInterstitialLoadingLauncher: Boolean = false

    private var onAdAction: OnAdAction? = null
    private var onAdAction1: OnAdAction1? = null

    private val nativeAdMap = mutableMapOf<String, NativeAd?>()
    private val nativeAdLoadingMap = mutableMapOf<String, Boolean>()
    private val bannerAdLoadingMap = mutableMapOf<String, Boolean>()

    var currentActivity: Activity? = null

    val appOpen: GAppOpen?
        get() = AdsApp.instance.gAppOpen

    fun interface OnAdAction {
        fun onCallBack()
    }

    interface OnAdAction1 {
        fun onResult(success: Boolean)
    }
    fun logInterAdImpression(
        context: Context?,
        adValue: AdValue?,
        interstitialAd: InterstitialAd?
    ) {
        try {
            if (!adsData().logadevent) return
            if (context == null || adValue == null || interstitialAd == null) return

            val params = Bundle().apply {
                putString(FirebaseAnalytics.Param.AD_UNIT_NAME, interstitialAd.adUnitId)
                putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
                putDouble(
                    FirebaseAnalytics.Param.VALUE,
                    adValue.valueMicros / 1_000_000.0
                )
                putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager")
                putString(
                    FirebaseAnalytics.Param.AD_SOURCE,
                    interstitialAd.responseInfo.mediationAdapterClassName
                )
            }

            FirebaseAnalytics.getInstance(context)
                .logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadInterstitialLauncher(
        activity: Context,
        adsResponse: AdsResponse,
    ) {
        if (interstitialAdLauncher != null || isInterstitialLoadingLauncher) return

        isInterstitialLoadingLauncher = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            adsResponse.Interstitial_All,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)

                    interstitialAd.setOnPaidEventListener { adValue ->

                        logInterAdImpression(activity,adValue,interstitialAd)

                        Log.e("#sadadc12","asdad");

                    }


                    isInterstitialLoadingLauncher = false
                    interstitialAdLauncher = interstitialAd
                    interstitialAdLauncher?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                Log.e(TAG, "Interstitial onAdDismissedFullScreenContent: ")
                                onAdAction?.onCallBack()
                                interstitialAdLauncher = null
                                isInterShowing = false
                                onAdAction1?.onResult(true)
                                currentActivity?.window?.let {
                                    WindowCompat.setDecorFitsSystemWindows(
                                        it,
                                        false,
                                    )
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                super.onAdFailedToShowFullScreenContent(adError)
                                Log.e(TAG, "Interstitial onAdFailedToShowFullScreenContent: ")
                                onAdAction?.onCallBack()
                                interstitialAdLauncher = null
                                isInterShowing = false
                                onAdAction1?.onResult(false)
                                currentActivity?.window?.let {
                                    WindowCompat.setDecorFitsSystemWindows(
                                        it,
                                        false,
                                    )
                                }
                            }

                            override fun onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent()
                                isInterShowing = true

                                currentActivity?.window?.let {
                                    WindowCompat.setDecorFitsSystemWindows(
                                        it,
                                        true,
                                    )
                                }
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                isFromPromotionalContent = true
                            }
                        }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.e(TAG, "interstitial onAdFailedToLoad: ${error.message}")
                    isInterstitialLoadingLauncher = false
                    interstitialAdLauncher = null
                }
            },
        )
    }


    fun showInterstitialLauncher(
        activity: Activity,
        adsResponse: AdsResponse,
        onAdAction: OnAdAction1,
        showLoader: Boolean = false,
    ) {
        onAdAction1 = onAdAction

        currentActivity = activity

        Log.e("#asdads", interstitialAdLauncher.toString());

        if (interstitialAdLauncher != null) {
            if (showLoader) {
                Dialog(activity).apply {
                    val binding = GInterLoadingDialogBinding.inflate(activity.layoutInflater)
                    setContentView(binding.root)
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)

                    Handler(Looper.getMainLooper()).postDelayed({
                        Handler(Looper.getMainLooper()).postDelayed({
                            try {
                                dismiss()
                            } catch (e: Exception) {
                                Log.e(TAG, "showInterstitial: ${e.message}")
                            }
                        }, 1000)

                        interstitialAdLauncher?.show(activity)
                        Log.e(TAG, "showInterstitial show: ")
                    }, 1500)

                    window?.apply {
                        setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.MATCH_PARENT,
                        )
                        setBackgroundDrawableResource(android.R.color.transparent)
                        setGravity(Gravity.CENTER)
                    }
                    show()
                }
            } else {
                interstitialAdLauncher?.show(activity)
                Log.e(TAG, "showInterstitial show: ")
            }
        } else {
            onAdAction1?.onResult(false)
//            loadInterstitialLauncher(activity, adsResponse)
            Log.e(TAG, "retry loading go back: ")
//            GAds.onAdAction?.onCallBack()
        }
    }

    private fun getAdSize(context: Context): AdSize {
        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels
        val density = displayMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    fun loadAndShowBanner(
        context: Context,
        uniqueKey: String,
        container: FrameLayout,
        bannerType: BannerType,
        adId: String,
        onAdResult: (Boolean) -> Unit,
    ) {
        val bannerAdLoading: Boolean = bannerAdLoadingMap[uniqueKey] == true
        Log.e(TAG, "showBanner uniqueKey: $uniqueKey  bannerAdLoadin: $bannerAdLoadingMap")
        if (bannerAdLoading) {
            return
        }

        bannerAdLoadingMap[uniqueKey] = true


        val adView = AdView(context)
        Log.e("TAG_Gads", "New AdView created: $adView")

        val loaderLayout = when (uniqueKey) {
            "LauncherSidePage" -> {
                R.layout.g_banner_loader_launcher_side_page
            }

            "LauncherSidePageNative" -> {
                R.layout.g_inline_banner_loader_launcher_side_page
            }

            else -> {
                when (bannerType) {
                    BannerType.COLLAPSIBLE_BANNER -> R.layout.g_banner_loader

                    BannerType.NORMAL_BANNER -> R.layout.g_banner_loader

                    BannerType.LARGE_BANNER -> R.layout.g_large_banner_loader

                    BannerType.INLINE_BANNER -> R.layout.g_inline_banner_loader
                }
            }
        }

        val loader = LayoutInflater.from(context)
            .inflate(loaderLayout, container, false) as LinearLayout
        container.removeAllViews()
        container.addView(loader)

        adView.adUnitId = adId
        Log.e("TAG_Gads", "AdUnitID applied: $adId")

        val adRequest = if (bannerType == BannerType.COLLAPSIBLE_BANNER) {

            val extras = Bundle()
            extras.putString("collapsible", "bottom")

            AdManagerAdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()

        } else {
            AdManagerAdRequest.Builder().build()
        }

        val adSize = when (bannerType) {
            BannerType.INLINE_BANNER -> getInlineAdaptiveBannerAdSize(
                context
            )

            BannerType.LARGE_BANNER -> AdSize.LARGE_BANNER
            else -> getAdSize(context)
        }

        adView.setAdSize(adSize)
        Log.e("TAG_Gads", "AdSize set → LARGE_BANNER"+adSize)

        adView.setOnPaidEventListener { adValue ->

            logBannerAdImpression(context,adValue,adView)
        }

        adView.loadAd(adRequest)
        Log.e("TAG_Gads", "Ad loading started...")
        Log.e("TAG_Gads", "AdView stored in map for key=$uniqueKey")

        adView.adListener = object : AdListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)

                Log.e("TAG_Gads", "Home Banner Failed → ${loadAdError.message}")

                bannerAdLoadingMap[uniqueKey] = false

                container.removeAllViews()
                onAdResult(false)
                Log.e("TAG_Gads", "Container cleared due to failure")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e(TAG, "Home Banner Loaded Successfully for key=$uniqueKey")

                bannerAdLoadingMap[uniqueKey] = false

                showHomeBanner(
                    container = container,
                    homeBannerView = adView,
                    uniqueKey = uniqueKey
                )
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.e(TAG, "Home Banner Clicked!")
                isFromPromotionalContent = true
            }
        }
    }
    fun logBannerAdImpression(
        context: Context?,
        adValue: AdValue?,
        adView: AdView?
    ) {
        try {
            if (!adsData().logadevent) return
            if (context == null || adValue == null || adView == null) return

            val params = Bundle().apply {
                putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adsData().Banner_All)
                putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
                putDouble(
                    FirebaseAnalytics.Param.VALUE,
                    adValue.valueMicros / 1_000_000.0
                )
                putString(FirebaseAnalytics.Param.AD_FORMAT, "Banner")
                putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager")
                putString(
                    FirebaseAnalytics.Param.AD_SOURCE,
                    adView.responseInfo?.mediationAdapterClassName
                )
            }

            FirebaseAnalytics.getInstance(context)
                .logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun nativeFailLoadAndShowBanner(
        context: Context,
        uniqueKey: String,
        container: FrameLayout,
        bannerType: BannerType,
        nativeType: NativeType,
        adId: String,
        onAdResult: (Boolean) -> Unit,
    ) {
        val bannerAdLoading: Boolean = bannerAdLoadingMap[uniqueKey] == true
        Log.e(TAG, "showBanner uniqueKey: $uniqueKey  bannerAdLoadin: $bannerAdLoadingMap")
        if (bannerAdLoading) {
            return
        }

        bannerAdLoadingMap[uniqueKey] = true


        val adView = AdView(context)
        Log.e("TAG_Gads", "New AdView created: $adView")

        val loaderLayout = when (uniqueKey) {
            "LauncherSidePage" -> {
                R.layout.g_banner_loader_launcher_side_page
            }

            "LauncherSidePageNative" -> {
                R.layout.g_inline_banner_loader_launcher_side_page
            }

            else -> {
                when (bannerType) {
                    BannerType.COLLAPSIBLE_BANNER -> R.layout.g_banner_loader

                    BannerType.NORMAL_BANNER -> R.layout.g_banner_loader

                    BannerType.LARGE_BANNER -> R.layout.g_large_banner_loader

                    BannerType.INLINE_BANNER -> R.layout.g_inline_banner_loader
                }
            }
        }

        val loader = LayoutInflater.from(context)
            .inflate(loaderLayout, container, false) as LinearLayout
        container.removeAllViews()
        container.addView(loader)

        adView.adUnitId = adId
        Log.e("TAG_Gads", "AdUnitID applied: $adId")

        val adRequest = if (bannerType == BannerType.COLLAPSIBLE_BANNER) {

            val extras = Bundle()
            extras.putString("collapsible", "bottom")

            AdManagerAdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()

        } else {
            AdManagerAdRequest.Builder().build()
        }

        val adSize = when (bannerType) {
            BannerType.INLINE_BANNER -> getInlineAdaptiveBannerAdSize(context)

            BannerType.LARGE_BANNER -> AdSize.LARGE_BANNER
            else -> getAdSize(context)
        }

        adView.setAdSize(adSize)
        Log.e("TAG_Gads", "AdSize set → LARGE_BANNER")

        adView.setOnPaidEventListener { adValue ->
            logBannerAdImpression(context,adValue,adView)

        }

        adView.loadAd(adRequest)
        Log.e("TAG_Gads", "Ad loading started...")
        Log.e("TAG_Gads", "AdView stored in map for key=$uniqueKey")

        adView.adListener = object : AdListener() {

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)

                Log.e("TAG_Gads", "Home Banner Failed → ${loadAdError.message}")

                bannerAdLoadingMap[uniqueKey] = false

                container.removeAllViews()
                onAdResult(false)
                Log.e("TAG_Gads", "Container cleared due to failure")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.e(TAG, "Home Banner Loaded Successfully for key=$uniqueKey")

                bannerAdLoadingMap[uniqueKey] = false

                showHomeBanner(
                    container = container,
                    homeBannerView = adView,
                    uniqueKey = uniqueKey
                )
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.e(TAG, "Home Banner Clicked!")
                isFromPromotionalContent = true

                if (!AdsManager.adsData().Native_All_Click2Change) return

                clickToChangeNative(
                    context = context,
                    container = container,
                    nativeType = if (uniqueKey == "LauncherSidePageNative") NativeType.LARGE_NATIVE_LAUNCHER_SIDE_PAGE else nativeType,
                    adId = AdsManager.adsData().Native_All,
                    uniqueKey = uniqueKey,
                    onAdAction = onAdResult
                )
            }
        }
    }

    private fun showHomeBanner(
        container: FrameLayout,
        homeBannerView: AdView?,
        uniqueKey: String,
    ) {
        if (homeBannerView == null) return

        if (homeBannerView.parent != null) {
            (homeBannerView.parent as ViewGroup).removeView(homeBannerView)
        }

        container.removeAllViews()

        val context = container.context

        val wrapperLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        val density = context.resources.displayMetrics.density
        val twoDp = (2 * density).toInt()

        val dividerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            twoDp
        )

        val adParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }

        // Top divider
        val topDivider = View(context).apply {
            setBackgroundColor(
                ContextCompat.getColor(context, R.color.nativeAdBorder)
            )
        }

        // Bottom divider
        val bottomDivider = View(context).apply {
            setBackgroundColor(
                ContextCompat.getColor(context, R.color.nativeAdBorder)
            )
        }

        if (uniqueKey == "LauncherSidePage" || uniqueKey == "LauncherSidePageNative") {
            wrapperLayout.addView(homeBannerView, adParams)
        } else {
            wrapperLayout.addView(topDivider, dividerParams)
            wrapperLayout.addView(homeBannerView, adParams)
            wrapperLayout.addView(bottomDivider, dividerParams)
        }

        container.addView(wrapperLayout)
    }


    fun clickToChangeBanner(
        context: Context,
        container: FrameLayout,
        bannerType: BannerType,
        adId: String,
        uniqueKey: String,
    ) {
        val bannerAdLoading: Boolean = bannerAdLoadingMap[uniqueKey] == true
        Log.e(TAG, "showBanner uniqueKey: $uniqueKey  bannerAdLoadin: ${bannerAdLoadingMap}")
        if (bannerAdLoading) {
            return
        }

        bannerAdLoadingMap[uniqueKey] = true

        val adView = AdView(context)
        adView.adUnitId = adId
        val adRequest = AdRequest.Builder().build()

        val adSize = when (bannerType) {
            BannerType.INLINE_BANNER -> getInlineAdaptiveBannerAdSize(context)

            BannerType.LARGE_BANNER -> AdSize.LARGE_BANNER
            else -> getAdSize(context)
        }

        adView.setAdSize(adSize)
        adView.setOnPaidEventListener { adValue ->
            logBannerAdImpression(context,adValue,adView)

        }
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                bannerAdLoadingMap[uniqueKey] = false

                Log.e(TAG, "clickToChangeBanner onAdFailedToLoad: ")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                bannerAdLoadingMap[uniqueKey] = false

                Log.e(TAG, "clickToChangeBanner onAdLoaded: ")
                showHomeBanner(
                    container = container,
                    homeBannerView = adView,
                    uniqueKey = uniqueKey
                )
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.e(TAG, "clickToChangeBanner onAdClicked: ")

                isFromPromotionalContent = true

                clickToChangeBanner(
                    context = context,
                    container = container,
                    bannerType = bannerType,
                    adId = adId,
                    uniqueKey = uniqueKey
                )
            }
        }
    }


    fun showNative(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        adId: String,
        uniqueKey: String,
        onAdAction: (Boolean) -> Unit = {},
    ) {
        Log.e(TAG, "showNative nativeType: $nativeType")

        val existingNativeAd: NativeAd? = nativeAdMap[uniqueKey]
        if (existingNativeAd != null) {
            onAdAction(true)
            inflateNative(context, nativeType, container, existingNativeAd)
            nativeAdMap.remove(uniqueKey)
            return
        }

        val loaderLay =
            when (nativeType) {
                NativeType.FULL_SCREEN_NATIVE -> R.layout.g_native_full_screen_loader
                NativeType.LARGE_NATIVE -> R.layout.g_native_large_loader
                NativeType.LARGE_NATIVE_TOP_MEDIAVIEW -> R.layout.g_native_large_loader_top_mediaview
                NativeType.LARGE_NATIVE_LAUNCHER_SIDE_PAGE -> R.layout.g_native_large_loader_launcher_side_page
                NativeType.SMALL_NATIVE -> R.layout.g_native_small_loader
                NativeType.SMALL_NATIVE_BANNER -> R.layout.g_native_banner_small_loader
                NativeType.LARGE_NATIVE_BANNER -> R.layout.g_native_banner_large_loader
            }

        val loader =
            LayoutInflater.from(context).inflate(loaderLay, container, false) as LinearLayout
        container.removeAllViews()
        container.addView(loader)

        val nativeAdLoading: Boolean = nativeAdLoadingMap[uniqueKey] == true
        if (nativeAdLoading) {
            return
        }

        Log.e(TAG, "showNative load: $uniqueKey")

        val builder = AdLoader.Builder(context, adId)
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adLoader =
            builder
                .forNativeAd {
                    it.setOnPaidEventListener { adValue ->

                        logNativeAdImpression(context, adValue, it)

                    }

                    Log.e(TAG, "showNative onAdLoaded uniqueKey: $uniqueKey")
                    onAdAction(true)
                    inflateNative(context, nativeType, container, it)
                }.withAdListener(
                    object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            super.onAdFailedToLoad(error)
                            Log.e(
                                TAG,
                                "showNative onAdFailedToLoad uniqueKey: $uniqueKey ${error.message}"
                            )
                            container.removeAllViews()

                            if (nativeType == NativeType.FULL_SCREEN_NATIVE) {
                                onAdAction(false)
                                return
                            }

                            if (AdsManager.adsData().Banner_Inline_Native_Fail.isEmpty()) {
                                AdsManager.showWebNative(
                                    context = context,
                                    container = container,
                                    nativeType = nativeType
                                )
                                return
                            }

                            val bannerType = when {

                                uniqueKey == WallpaperListActivity::class.java.simpleName -> BannerType.COLLAPSIBLE_BANNER

                                nativeType == NativeType.LARGE_NATIVE_BANNER -> BannerType.LARGE_BANNER

                                nativeType == NativeType.SMALL_NATIVE_BANNER -> BannerType.NORMAL_BANNER

                                else -> BannerType.INLINE_BANNER
                            }

                            val myNativeType = when {
                                uniqueKey == WallpaperListActivity::class.java.simpleName -> NativeType.SMALL_NATIVE

                                else -> nativeType
                            }

                            nativeFailLoadAndShowBanner(
                                context = context,
                                uniqueKey = uniqueKey,
                                container = container,
                                nativeType = myNativeType,
                                bannerType = bannerType,
                                adId = AdsManager.adsData().Banner_Inline_Native_Fail,
                                onAdResult = onAdAction
                            )
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            isFromPromotionalContent = true

                            if (nativeType == NativeType.FULL_SCREEN_NATIVE || !AdsManager.adsData().Native_All_Click2Change) return

                            clickToChangeNative(
                                context = context,
                                container = container,
                                nativeType = nativeType,
                                adId = adId,
                                uniqueKey = uniqueKey,
                                onAdAction = onAdAction
                            )

                        }
                    },
                ).withNativeAdOptions(
                    NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
                )
                .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun clickToChangeNative(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        adId: String,
        uniqueKey: String,
        onAdAction: (Boolean) -> Unit = {},
    ) {
        Log.e(TAG, "clickToChangeNative nativeType: $nativeType")

        val nativeAdLoading: Boolean = nativeAdLoadingMap[uniqueKey] == true
        if (nativeAdLoading) return

        nativeAdLoadingMap[uniqueKey] = true

        val builder = AdLoader.Builder(context, adId)
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adLoader = builder
            .forNativeAd { newNativeAd ->

                newNativeAd.setOnPaidEventListener { adValue ->
                    logNativeAdImpression(context, adValue, newNativeAd)

                }

                Log.e(TAG, "clickToChangeNative onAdLoaded: $uniqueKey")

                inflateNative(
                    context,
                    nativeType,
                    container,
                    newNativeAd
                )

                nativeAdLoadingMap[uniqueKey] = false
            }
            .withAdListener(object : AdListener() {

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)

                    Log.e(TAG, "clickToChangeNative failed: ${error.message}")
                    nativeAdLoadingMap[uniqueKey] = false

                    container.removeAllViews()


                    val bannerType = when {

                        uniqueKey == WallpaperListActivity::class.java.simpleName -> BannerType.COLLAPSIBLE_BANNER


                        nativeType == NativeType.LARGE_NATIVE_BANNER -> BannerType.LARGE_BANNER

                        nativeType == NativeType.SMALL_NATIVE_BANNER -> BannerType.NORMAL_BANNER

                        else -> BannerType.INLINE_BANNER
                    }

                    val myNativeType = when {
                        uniqueKey == WallpaperListActivity::class.java.simpleName -> NativeType.SMALL_NATIVE

                        else -> nativeType
                    }

                    nativeFailLoadAndShowBanner(
                        context = context,
                        uniqueKey = uniqueKey,
                        container = container,
                        nativeType = myNativeType,
                        bannerType = bannerType,
                        adId = AdsManager.adsData().Banner_Inline_Native_Fail,
                        onAdResult = onAdAction
                    )
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    isFromPromotionalContent = true

                    clickToChangeNative(
                        context = context,
                        container = container,
                        nativeType = nativeType,
                        adId = adId,
                        uniqueKey = uniqueKey,
                        onAdAction = onAdAction
                    )
                }

            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun inflateNative(
        context: Context,
        nativeType: NativeType,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        when (nativeType) {
            NativeType.SMALL_NATIVE -> {
                inflateSmallNativeAd(context, container, nativeAd)
            }

            NativeType.LARGE_NATIVE -> inflateLargeNativeAd(context, container, nativeAd)

            NativeType.LARGE_NATIVE_TOP_MEDIAVIEW -> inflateLargeNativeAdTopMediaview(
                context,
                container,
                nativeAd
            )

            NativeType.FULL_SCREEN_NATIVE -> inflateFullScreenNativeAd(context, container, nativeAd)

            NativeType.LARGE_NATIVE_LAUNCHER_SIDE_PAGE -> inflateLargeNativeAdLauncherSidePage(
                context,
                container,
                nativeAd
            )

            NativeType.SMALL_NATIVE_BANNER ->
                inflateSmallNativeBannerAd(
                    context,
                    container,
                    nativeAd,
                )

            NativeType.LARGE_NATIVE_BANNER -> {
                inflateLargeNativeBannerAd(context, container, nativeAd)
            }

        }
    }

    fun preLoadNative(
        context: Context,
        adId: String,
        uniqueKey: String,
    ) {
        if (!AdsManager.adsData().Ads_status || adId.isEmpty()) {
            return
        }

        val existingNativeAd: NativeAd? = nativeAdMap[uniqueKey]
        if (existingNativeAd == null) {
            Log.e(TAG, "preLoadNative: $uniqueKey")
            nativeAdLoadingMap[uniqueKey] = true

            val builder = AdLoader.Builder(context, adId)
            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val adLoader =
                builder
                    .forNativeAd {
                        it.setOnPaidEventListener { adValue ->
                            logNativeAdImpression(context, adValue, it)

                        }

                        Log.d("TAG_event", "preLoadNative onAdLoaded uniqueKey: $uniqueKey")
                        nativeAdMap[uniqueKey] = it
                        nativeAdLoadingMap[uniqueKey] = false
                        EventBus.getDefault().post(NativeAdEvent(true, uniqueKey))
                    }.withAdListener(
                        object : AdListener() {
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                super.onAdFailedToLoad(error)
                                Log.d(
                                    "TAG_event",
                                    "preLoadNative onAdFailedToLoad uniqueKey $uniqueKey: ${error.message}",
                                )
                                nativeAdMap.remove(uniqueKey)
                                nativeAdLoadingMap[uniqueKey] = false
                                EventBus.getDefault().post(NativeAdEvent(false, uniqueKey))
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                Log.d("TAG_event", "onAdClicked: preload")
                                isFromPromotionalContent = true
                            }
                        },
                    ).withNativeAdOptions(
                        NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
                    )
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun isNativeLoaded(uniqueKey: String): Boolean {
        val existingNativeAd: NativeAd? = nativeAdMap[uniqueKey]
        return existingNativeAd != null
    }

    private fun inflateLargeNativeAdTopMediaview(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeLargeTopMediaviewBinding.inflate(LayoutInflater.from(context))

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

    private fun inflateLargeNativeAd(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeLargeBinding.inflate(LayoutInflater.from(context))

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

    private fun inflateFullScreenNativeAd(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeFullScreenBinding.inflate(LayoutInflater.from(context))

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

    private fun inflateLargeNativeAdLauncherSidePage(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeLargeLauncherSidePageBinding.inflate(LayoutInflater.from(context))

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


    private fun inflateSmallNativeAd(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeSmallBinding.inflate(LayoutInflater.from(context))

        val nativeAdView = binding.root
        nativeAdView.headlineView = binding.headlineView
        nativeAdView.bodyView = binding.bodyView
        nativeAdView.callToActionView = binding.callToActionView
        nativeAdView.mediaView = binding.mediaView

        binding.headlineView.text = nativeAd.headline
        nativeAd.mediaContent?.let { binding.mediaView.mediaContent = it }



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

    private fun inflateLargeNativeBannerAd(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeBannerLargeBinding.inflate(LayoutInflater.from(context))

        val nativeAdView = binding.root
        nativeAdView.headlineView = binding.headlineView
        nativeAdView.bodyView = binding.bodyView
        nativeAdView.callToActionView = binding.callToActionView
        nativeAdView.iconView = binding.iconView

        binding.headlineView.text = nativeAd.headline

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


    private fun inflateSmallNativeBannerAd(
        context: Context,
        container: FrameLayout,
        nativeAd: NativeAd,
    ) {
        val binding = GNativeBannerSmallBinding.inflate(LayoutInflater.from(context))

        val nativeAdView = binding.root
        nativeAdView.headlineView = binding.headlineView
        nativeAdView.bodyView = binding.bodyView
        nativeAdView.callToActionView = binding.callToActionView
        nativeAdView.iconView = binding.iconView

        binding.headlineView.text = nativeAd.headline

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

    fun logNativeAdImpression(
        context: Context?,
        adValue: AdValue?,
        nativeAd: NativeAd?
    ) {
        try {
            if (!adsData().logadevent) return
            if (context == null || adValue == null || nativeAd == null) return

            val params = Bundle().apply {
                putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adsData().Native_All)
                putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)
                putDouble(
                    FirebaseAnalytics.Param.VALUE,
                    adValue.valueMicros / 1_000_000.0
                )
                putString(FirebaseAnalytics.Param.AD_FORMAT, "Native")
                putString(FirebaseAnalytics.Param.AD_PLATFORM, "Google Ad Manager")
                putString(
                    FirebaseAnalytics.Param.AD_SOURCE,
                    nativeAd.responseInfo?.mediationAdapterClassName ?: "unknown")
            }

            FirebaseAnalytics.getInstance(context)
                .logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params)

        } catch (e: Exception) {
            e.printStackTrace()
//            FirebaseCrashlytics.getInstance().recordException(e)
        }
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
            appLogEvent("ad_impression", params)
        }

        Log.e(TAG, "sendAdImpressionRevenueToFirebase: $params")
    }

    @SuppressLint("DefaultLocale")
    fun sendAdRevenueToMeta(adValue: AdValue, adUnitId: String) {

        val revenue = adValue.valueMicros / 1_000_000.0
        val params = Bundle().apply {
            putString(AppEventsConstants.EVENT_PARAM_CURRENCY, adValue.currencyCode)
            putString("formatted_revenue", String.format("%.6f", revenue))
            putInt("precision", adValue.precisionType)
            putString("ad_unit_id", adUnitId)
        }


        MyApplication.logger.logEvent(
            AppEventsConstants.EVENT_NAME_AD_IMPRESSION,
            revenue,
            params
        )

        Log.e(TAG, "sendAdRevenueToMeta: $params $revenue")
    }

    fun getInlineAdaptiveBannerAdSize(context: Context): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val adWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()

        return AdSize.getInlineAdaptiveBannerAdSize(adWidth, 100)
    }


}
