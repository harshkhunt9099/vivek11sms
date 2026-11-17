package com.zone.messanger.text.PANDA_ads

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.gson.Gson
import com.zone.messanger.text.BuildConfig
import com.zone.messanger.text.PANDA_Activity.WallpaperListActivity
import com.zone.messanger.text.databinding.WebBannerBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorPrefManager
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorAdsResponse
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitor_WebAdItem
import com.launcher.launcherpackage.Codebizz_helper.setPerfectClickListener
import kotlin.collections.iterator

object AdsManager {
    private const val TAG = "AdsManager2"
    var adsClick: Int = 0
    var INTER_LINKED_COUNT: Int = 0
    var INTER_LINKED_COUNT_RECYCLE: Int = 0

    var isInterShowing: Boolean = false
    var isFromPromotionalContent: Boolean = false
    var isFromPermission: Boolean = false


    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
    }

    fun adsData(adsResponse: AdsResponse) =
        sharedPreferences.edit { putString("adsDataV1", Gson().toJson(adsResponse)) }


    fun adsData(): AdsResponse {
        val json = sharedPreferences.getString("adsDataV1", null) ?: return AdsResponse()
        logLargeString("FirebaseDatax", json.toString())
        return Gson().fromJson(json, AdsResponse::class.java)
    }

    fun logLargeString(tag: String, content: String) {
        val chunkSize = 1000
        var i = 0
        while (i < content.length) {
            val end = (i + chunkSize).coerceAtMost(content.length)
            android.util.Log.d(tag, content.substring(i, end))
            i += chunkSize
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)?.let { it ->
                return it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }

        return false
    }

    fun fetchRemoteConfigData(
        context: Context,
        onAdAction: GAds.OnAdAction,
    ) {
        Log.e(TAG, "fetchData: ")
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // 🔥 force fresh
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        Log.e(TAG, "fetchData start: ")
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val allValues = remoteConfig.all
                for ((key, value) in allValues) {
                    println("Key: $key, Value: ${value.asString()}")
                    Log.e(TAG, "fetchData: Key: $key, Value: ${value.asString()}")
                }
                Log.e("#sadaddsadaxxx", remoteConfig.all.toString());
                Log.e("#sadaddsadaxxx", remoteConfig.getBoolean("Ads_status").toString());
                Log.e("#sadaddsadaxxx", remoteConfig.getString("Ads_Redirect").toString());

                val adsResponse = if (BuildConfig.DEBUG) {
                    AdsResponse.remoteConfigReleaseAds(remoteConfig)
                } else {
                    AdsResponse.remoteConfigReleaseAds(remoteConfig)
                }

                INTER_LINKED_COUNT = adsData().INTER_LINKED_SHOW.size

                Log.e("#sadaddsada", remoteConfig.getBoolean("Ads_status").toString());
                Log.e("#sadaddsada", adsResponse.Ads_status.toString());
                adsClick = adsResponse.Ads_click

                val firebaseNavEnabled = adsResponse.Ads_App_Navigation
                if (!context.config.navigationUserOverride) {
                    context.config.showNavigationBar = firebaseNavEnabled
                }

                Log.e(TAG, "fetchRemoteConfigData: $adsResponse")

                //TODO FOR PackageMonitor
                Codebizz_PackageMonitorPrefManager.setPackageMonitorAdsResponse(
                    Codebizz_PackageMonitorAdsResponse(
                        PackageMonitor_Native = adsResponse.Native_All,
                        PackageMonitor_Banner_Inline = adsResponse.Banner_Inline_Native_Fail,
                        PackageMonitor_Rewarded_Video = adsResponse.Rewarded_Video,
                        PackageMonitor_Native_All_Click2Change = adsResponse.Native_All_Click2Change,
                        PackageMonitor_Native_All_Fail_Array = adsResponse.Native_All_Fail_Array.map { it.toPackageMonitorWebAdItem() }
                    )
                )

                adsData(adsResponse)

                onAdAction.onCallBack()
            }
        }
    }

    fun WebAdItem.toPackageMonitorWebAdItem(): Codebizz_PackageMonitor_WebAdItem {
        return Codebizz_PackageMonitor_WebAdItem(
            image_url = image_url,
            click_url = click_url
        )
    }


    fun loadInterstitialLauncher(context: Context) {
        if (adsData().Ads_status && adsData().Interstitial_All.isNotEmpty() && isNetworkAvailable(
                context
            )
        ) {
            GAds.loadInterstitialLauncher(context, adsData())
        }
    }

    fun loadAndShowBanner(
        context: Context,
        uniqueKey: String = "default",
        container: FrameLayout,
        bannerType: BannerType = BannerType.NORMAL_BANNER,
        adId: String = adsData().Banner_All,
    ) {

        try {

            if (context is Activity) {

                if (context.isDestroyed || context.isFinishing) {
                    return
                }
            }

            if (!adsData().Ads_status || !isNetworkAvailable(context)) {

                container.visibility = View.GONE
                container.removeAllViews()
                return
            }

            if (container.childCount > 0) {
                return
            }

            container.visibility = View.VISIBLE

            if (adId.isNotEmpty()) {

                GAds.loadAndShowBanner(
                    context = context,
                    uniqueKey = uniqueKey,
                    container = container,
                    bannerType = bannerType,
                    adId = adId
                ) { success ->

                    if (!success) {

                        container.removeAllViews()

                        when (bannerType) {

                            BannerType.INLINE_BANNER -> {

                                showWebNative(
                                    context,
                                    container,
                                    NativeType.LARGE_NATIVE
                                )
                            }

                            else -> {

                                showWebBanner(
                                    context,
                                    container
                                )
                            }
                        }
                    }
                }

            } else {

                showWebBanner(
                    context,
                    container
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
// AdsManager object ni andar paste karo (loadAndShowBanner1 replace karo)

    /**
     * Banner load order:
     *  1) adId        (server ni pehli id)
     *  2) secondAdId  (server ni biji id)  -> pehli fail thay to
     *  3) Web banner / web native          -> bane fail thay ya id j na hoy to
     *
     * NOTE: adsData().Banner_All_2 tamara server model ma je naam hoy te rakho.
     */
    fun loadAndShowBanner1(
        context: Context,
        uniqueKey: String = "default1",
        container: FrameLayout,
        bannerType: BannerType = BannerType.INLINE_BANNER,
        adId: String = adsData().Banner_All_1,
        secondAdId: String = adsData().Banner_All_2,
    ) {
        try {

            if (isContextDead(context)) return

            if (!adsData().Ads_status || !isNetworkAvailable(context)) {
                container.visibility = View.GONE
                container.removeAllViews()
                return
            }

            if (container.childCount > 0) {
                return
            }

            container.visibility = View.VISIBLE

            // khali / sarkhi id hatavo (same id be vaar try na thay)
            val adIds = listOf(adId, secondAdId)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            loadBannerWithFallback(
                context = context,
                uniqueKey = uniqueKey,
                container = container,
                bannerType = bannerType,
                adIds = adIds,
                index = 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBannerWithFallback(
        context: Context,
        uniqueKey: String,
        container: FrameLayout,
        bannerType: BannerType,
        adIds: List<String>,
        index: Int,
    ) {
        try {

            if (isContextDead(context)) return

            // badhi id fail thai gai (ya koi id j nathi) -> web banner
            if (index >= adIds.size) {
                showBannerFallback(context, container, bannerType)
                return
            }

            // biji id mate alag key, jethi pehla ni failed/cached state vaparay nahi
            val key = if (index == 0) uniqueKey else "${uniqueKey}_$index"

            GAds.loadAndShowBanner(
                context = context,
                uniqueKey = key,
                container = container,
                bannerType = bannerType,
                adId = adIds[index]
            ) { success ->

                if (isContextDead(context)) return@loadAndShowBanner

                if (!success) {
                    container.removeAllViews()

                    // next id try karo (na hoy to upar na check thi web aavse)
                    loadBannerWithFallback(
                        context = context,
                        uniqueKey = uniqueKey,
                        container = container,
                        bannerType = bannerType,
                        adIds = adIds,
                        index = index + 1
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showBannerFallback(context, container, bannerType)
        }
    }

    private fun showBannerFallback(
        context: Context,
        container: FrameLayout,
        bannerType: BannerType,
    ) {
        if (isContextDead(context)) return

        container.removeAllViews()
        container.visibility = View.VISIBLE

        when (bannerType) {
            BannerType.INLINE_BANNER -> showWebNative(context, container, NativeType.SMALL_NATIVE_BANNER)
            else -> showWebBanner(context, container)
        }
    }




// AdsManager object ni andar paste karo (loadAndShowBanner1 replace karo)

    /**
     * Banner load order:
     *  1) adId        (server ni pehli id)
     *  2) secondAdId  (server ni biji id)  -> pehli fail thay to
     *  3) Web banner / web native          -> bane fail thay ya id j na hoy to
     *
     * NOTE: adsData().Banner_All_2 tamara server model ma je naam hoy te rakho.
     */
    fun loadAndShowBanner3(
        context: Context,
        uniqueKey: String = "default3",
        container: FrameLayout,
        bannerType: BannerType = BannerType.INLINE_BANNER,
        adId: String = adsData().Banner_Home1_1,
        secondAdId: String = adsData().Banner_Home1_2,
    ) {
        try {

            if (isContextDead(context)) return

            if (!adsData().Ads_status || !isNetworkAvailable(context)) {
                container.visibility = View.GONE
                container.removeAllViews()
                return
            }

            if (container.childCount > 0) {
                return
            }

            container.visibility = View.VISIBLE

            // khali / sarkhi id hatavo (same id be vaar try na thay)
            val adIds = listOf(adId, secondAdId)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            loadBannerWithFallback3(
                context = context,
                uniqueKey = uniqueKey,
                container = container,
                bannerType = bannerType,
                adIds = adIds,
                index = 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBannerWithFallback3(
        context: Context,
        uniqueKey: String,
        container: FrameLayout,
        bannerType: BannerType,
        adIds: List<String>,
        index: Int,
    ) {
        try {

            if (isContextDead(context)) return

            // badhi id fail thai gai (ya koi id j nathi) -> web banner
            if (index >= adIds.size) {
                container.visibility = View.GONE
                return
            }

            // biji id mate alag key, jethi pehla ni failed/cached state vaparay nahi
            val key = if (index == 0) uniqueKey else "${uniqueKey}_$index"

            GAds.loadAndShowBanner(
                context = context,
                uniqueKey = key,
                container = container,
                bannerType = bannerType,
                adId = adIds[index]
            ) { success ->

                if (isContextDead(context)) return@loadAndShowBanner

                if (!success) {
                    container.removeAllViews()

                    // next id try karo (na hoy to upar na check thi web aavse)
                    loadBannerWithFallback3(
                        context = context,
                        uniqueKey = uniqueKey,
                        container = container,
                        bannerType = bannerType,
                        adIds = adIds,
                        index = index + 1
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            container.visibility = View.GONE
        }
    }







    fun loadAndShowBanner2(
        context: Context,
        uniqueKey: String = "default2",
        container: FrameLayout,
        bannerType: BannerType = BannerType.INLINE_BANNER,
        adId: String = adsData().Banner_All_swipe_1,
        secondAdId: String = adsData().Banner_All_swipe_2,
    ) {
        try {

            if (isContextDead(context)) return

            if (!adsData().Ads_status || !isNetworkAvailable(context)) {
                container.visibility = View.GONE
                container.removeAllViews()
                return
            }

            if (container.childCount > 0) {
                return
            }

            container.visibility = View.VISIBLE

            // khali / sarkhi id hatavo (same id be vaar try na thay)
            val adIds = listOf(adId, secondAdId)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            loadBannerWithFallback2(
                context = context,
                uniqueKey = uniqueKey,
                container = container,
                bannerType = bannerType,
                adIds = adIds,
                index = 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBannerWithFallback2(
        context: Context,
        uniqueKey: String,
        container: FrameLayout,
        bannerType: BannerType,
        adIds: List<String>,
        index: Int,
    ) {
        try {

            if (isContextDead(context)) return

            // badhi id fail thai gai (ya koi id j nathi) -> web banner
            if (index >= adIds.size) {
                showBannerFallback2(context, container, bannerType)
                return
            }

            // biji id mate alag key, jethi pehla ni failed/cached state vaparay nahi
            val key = if (index == 0) uniqueKey else "${uniqueKey}_$index"

            GAds.loadAndShowBanner(
                context = context,
                uniqueKey = key,
                container = container,
                bannerType = bannerType,
                adId = adIds[index]
            ) { success ->

                if (isContextDead(context)) return@loadAndShowBanner

                if (!success) {
                    container.removeAllViews()

                    // next id try karo (na hoy to upar na check thi web aavse)
                    loadBannerWithFallback2(
                        context = context,
                        uniqueKey = uniqueKey,
                        container = container,
                        bannerType = bannerType,
                        adIds = adIds,
                        index = index + 1
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showBannerFallback2(context, container, bannerType)
        }
    }

    private fun showBannerFallback2(
        context: Context,
        container: FrameLayout,
        bannerType: BannerType,
    ) {
        if (isContextDead(context)) return

        container.removeAllViews()
        container.visibility = View.VISIBLE

        when (bannerType) {
            BannerType.INLINE_BANNER -> showWebNative(context, container, NativeType.SMALL_NATIVE_BANNER)
            else -> showWebBanner(context, container)
        }
    }











    private fun isContextDead(context: Context): Boolean {
        return context is Activity && (context.isDestroyed || context.isFinishing)
    }





    fun showNative2(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        uniqueKey: String,
        adId: String,
        secondAdId: String,
    ) {
        try {

            if (isContextDead(context)) return

            if (!adsData().Ads_status || !isNetworkAvailable(context)) {
                Log.e(TAG, "showNative2: ads off ya network nathi")
                container.animateFoldUpFromTopAndHide {
                    container.removeAllViews()
                }
                return
            }

            // pehlethi ad batave chhe to fari load nahi (banner jevu j)
            if (container.childCount > 0) {
                return
            }

            container.visibility = View.VISIBLE

            // khali / sarkhi id hatavo (same id be vaar try na thay)
            val adIds = listOf(adId, secondAdId)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            loadNativeWithFallback2(
                context = context,
                container = container,
                nativeType = nativeType,
                uniqueKey = uniqueKey,
                adIds = adIds,
                index = 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadNativeWithFallback2(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        uniqueKey: String,
        adIds: List<String>,
        index: Int,
    ) {
        try {

            if (isContextDead(context)) return

            // badhi id fail thai gai (ya koi id j nathi) -> web native
            if (index >= adIds.size) {
                container.visibility = View.GONE
                return
            }

            // biji id mate alag key, jethi pehla ni failed/cached state vaparay nahi
            val key = if (index == 0) uniqueKey else "${uniqueKey}_$index"

            GAds.showNative(
                context = context,
                container = container,
                nativeType = nativeType,
                adId = adIds[index],
                uniqueKey = key
            ) { success ->

                if (isContextDead(context)) return@showNative

                Log.e(TAG, "native id[$index] nativeType=$nativeType success=$success")

                if (!success) {
                    container.removeAllViews()

                    // next id try karo (na hoy to upar na check thi web aavse)
                    loadNativeWithFallback2(
                        context = context,
                        container = container,
                        nativeType = nativeType,
                        uniqueKey = uniqueKey,
                        adIds = adIds,
                        index = index + 1
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            container.visibility = View.GONE
        }
    }



















    fun showNative1(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        uniqueKey: String,
        adId: String,
        secondAdId: String,
    ) {
        try {

            if (isContextDead(context)) return

            if (!adsData().Ads_status || !isNetworkAvailable(context)) {
                Log.e(TAG, "showNative2: ads off ya network nathi")
                container.animateFoldUpFromTopAndHide {
                    container.removeAllViews()
                }
                return
            }

            // pehlethi ad batave chhe to fari load nahi (banner jevu j)
            if (container.childCount > 0) {
                return
            }

            container.visibility = View.VISIBLE

            // khali / sarkhi id hatavo (same id be vaar try na thay)
            val adIds = listOf(adId, secondAdId)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            loadNativeWithFallback(
                context = context,
                container = container,
                nativeType = nativeType,
                uniqueKey = uniqueKey,
                adIds = adIds,
                index = 0
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadNativeWithFallback(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        uniqueKey: String,
        adIds: List<String>,
        index: Int,
    ) {
        try {

            if (isContextDead(context)) return

            // badhi id fail thai gai (ya koi id j nathi) -> web native
            if (index >= adIds.size) {
                showNativeFallback(context, container, nativeType, uniqueKey)
                return
            }

            // biji id mate alag key, jethi pehla ni failed/cached state vaparay nahi
            val key = if (index == 0) uniqueKey else "${uniqueKey}_$index"

            GAds.showNative(
                context = context,
                container = container,
                nativeType = nativeType,
                adId = adIds[index],
                uniqueKey = key
            ) { success ->

                if (isContextDead(context)) return@showNative

                Log.e(TAG, "native id[$index] nativeType=$nativeType success=$success")

                if (!success) {
                    container.removeAllViews()

                    // next id try karo (na hoy to upar na check thi web aavse)
                    loadNativeWithFallback(
                        context = context,
                        container = container,
                        nativeType = nativeType,
                        uniqueKey = uniqueKey,
                        adIds = adIds,
                        index = index + 1
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showNativeFallback(context, container, nativeType, uniqueKey)
        }
    }

    private fun showNativeFallback(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        uniqueKey: String,
    ) {
        if (isContextDead(context)) return

        container.removeAllViews()
        container.visibility = View.VISIBLE

        // showNative1 jevi j condition
        val webType = if (
            nativeType == NativeType.LARGE_NATIVE_BANNER
            || nativeType == NativeType.SMALL_NATIVE_BANNER
            || uniqueKey == WallpaperListActivity::class.java.simpleName
        ) nativeType else NativeType.LARGE_NATIVE

        showWebNative(context, container, webType)
    }




    fun showNative(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
        adId: String,
        uniqueKey: String,
    ) {
        Log.e(
            TAG,
            "showNative: ${adsData().Ads_status}  ${adId.isNotEmpty()} ${isNetworkAvailable(context)}",
        )
        if (adsData().Ads_status && isNetworkAvailable(context)) {
            if (adId.isNotEmpty()) {
                GAds.showNative(
                    context = context,
                    container = container,
                    nativeType = nativeType,
                    adId = adId,
                    uniqueKey = uniqueKey
                ) { success ->
                    Log.e("axxxxx", "nativeType ==: $nativeType success ==:$success")
                    if (!success) {
                        showWebNative(
                            context,
                            container,
                            if (nativeType == NativeType.LARGE_NATIVE_BANNER || nativeType == NativeType.SMALL_NATIVE_BANNER || uniqueKey == WallpaperListActivity::class.java.simpleName) nativeType else NativeType.LARGE_NATIVE
                        )
                    }
                }
            } else {
                showWebNative(context, container, nativeType)
            }
        } else {
            Log.e(TAG, "showNative something went wrong: ")
            container.animateFoldUpFromTopAndHide {
                container.removeAllViews()
            }
        }
    }

    fun preLoadNative(
        context: Context,
        adId: String,
        uniqueKey: String,
    ) {
        if (adsData().Ads_status && adId.isNotEmpty() && isNetworkAvailable(context)) {
            GAds.preLoadNative(context, adId, uniqueKey)
        }
    }

    fun isNativeLoaded(uniqueKey: String): Boolean {
        if (adsData().Ads_status) {
            return GAds.isNativeLoaded(uniqueKey)
        }
        return false
    }


    fun showWebNative(
        context: Context,
        container: FrameLayout,
        nativeType: NativeType,
    ) {
        val ads = adsData()
        Log.e("nativeadd4444", nativeType.toString())
        Log.e("nativeadd444444444", ads.Native_Banner_All_Fail_Array.toString())

        val webList = when (nativeType) {



            NativeType.SMALL_NATIVE_BANNER -> {
                ads.Banner_All_Fail_Array
            }

            else -> {
                ads.Native_All_Fail_Array
            }
        }
        Log.e("nativeadd555", ads.Native_All_Fail_Array.toString())

        if (webList.isEmpty()) {
            container.removeAllViews()
            container.background = null
            return
        }

        val randomAd = webList.random()

        val binding = WebBannerBinding.inflate(
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

    private fun showWebBanner(
        context: Context,
        container: FrameLayout,
    ) {
        val webList = adsData().Banner_All_Fail_Array

        if (webList.isEmpty()) {
            container.removeAllViews()
            container.background = null
            container.visibility = View.GONE
            return
        }

        val randomAd = webList.random()
        Log.e("#xcer12234", randomAd.click_url);

        val binding = WebBannerBinding.inflate(
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
                        container.visibility = View.VISIBLE
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
        isFromPermission = true
    }
    fun openCustomTabIntent1(context: Context, url: String) {

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

        if (!adsData().stopanotherapp) {
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }


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
        isFromPermission = true
    }







    fun showInterstitialLauncher(
        activity: Activity,
        showLoader: Boolean = false,
        onComplete: () -> Unit,
    ) {
        try {

            if (adsData().INTER_LINKED_SHOW.isNullOrEmpty()) {
                onComplete()
                return
            }
            if (adsData().Ads_status && isNetworkAvailable(activity)) {
                Log.e("xxxxx44442", shouldShowAd().toString())
                if (!shouldShowAd()) {
                    Log.e("xxxxx44443", AdCycleController.getStep().toString())
                    onComplete()
                    return
                }
                if (adsData().INTER_LINKED_SHOW != null && adsData().INTER_LINKED_SHOW.size > 0) {
                    if (INTER_LINKED_COUNT_RECYCLE >= adsData().INTER_LINKED_SHOW.size) {
                        INTER_LINKED_COUNT_RECYCLE = 0   // global
                    }
                }
                Log.e("xxxxxlllladd11", adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE])

                if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "L",
                        ignoreCase = true
                    )
                ) {

                    onComplete()

                    Log.e("xcasfterwe33xxxxx", adsData().Interstitial_All_Fail_Web_Array[0].toString())
                    Log.e("xcasfterwe44xxxxx", adsData().Interstitial_All_Fail_Web_Array[1].toString())
                    Log.e("xcasfterwe55xxxxx", adsData().Interstitial_All_Fail_Web_Array[2].toString())
                    adsData().Interstitial_All_Fail_Web_Array
                        .randomOrNull()
                        ?.let { openCustomTabIntent(activity, it) }


                    if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                        INTER_LINKED_COUNT_RECYCLE = 0
                        load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                    } else {
                        INTER_LINKED_COUNT_RECYCLE++
                        load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                    }

                    return
                }else if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "I",
                        ignoreCase = true
                    )
                ) {

                    if (adsData().Interstitial_All.isEmpty()) {


                        onComplete()

                        adsData().Interstitial_All_Fail_Web_Array
                            .randomOrNull()
                            ?.let { openCustomTabIntent(activity, it) }



                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        }

                        return
                    }


                    GAds.showInterstitialLauncher(
                        activity,
                        adsData(),
                        object : GAds.OnAdAction1 {
                            override fun onResult(success: Boolean) {
                                Log.e(
                                    "AdCycleControllerxxx2222",
                                    AdCycleController.getStep().toString()
                                )

                                Log.e(TAG, "onResult: ")
                                onComplete()

                                if (!success) {
                                    adsData().Interstitial_All_Fail_Web_Array
                                        .randomOrNull()
                                        ?.let { openCustomTabIntent(activity, it) }
                                }



                                if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                                    INTER_LINKED_COUNT_RECYCLE = 0
                                    load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                                } else {
                                    INTER_LINKED_COUNT_RECYCLE++
                                    load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                                }
                                return
                            }
                        },
                        showLoader = showLoader
                    )
                }else  if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "A",
                        ignoreCase = true
                    )
                ) {


                    if (adsData().Appopen_All.isEmpty() || GAds.appOpen?.isAdAvailable() == false) {
                        onComplete()

                        adsData().Interstitial_All_Fail_Web_Array
                            .randomOrNull()
                            ?.let { openCustomTabIntent(activity, it) }

                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        }

                        AdCycleController.nextStep()
                        return
                    }

                    GAds.appOpen?.showAppOpenAd(object : GAppOpen.AppOpenResult {
                        override fun isAppOpenAdShown(shown: Boolean) {
                            Log.e("AdCycleControllerxxx444", AdCycleController.getStep().toString())

                            onComplete()

                            if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                                INTER_LINKED_COUNT_RECYCLE = 0
                                load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                            } else {
                                INTER_LINKED_COUNT_RECYCLE++
                                load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                            }

                            AdCycleController.nextStep()
                            return
                        }
                    })
                }else  if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "N",
                        ignoreCase = true
                    )
                ) {

                    Log.e("AdCycleControllerxxx555", AdCycleController.getStep().toString())

                    if (adsData().Native_Interstitial.isEmpty() || !isNativeLoaded("FullScreenNative")) {
                        onComplete()

                        adsData().Interstitial_All_Fail_Web_Array
                            .randomOrNull()
                            ?.let { openCustomTabIntent(activity, it) }

                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        }

                        return
                    }


                    FullscreenNativeDialog(activity) { success ->
                        onComplete()
                        Log.e("AdCycleControllerxxx666", AdCycleController.getStep().toString())

                        if (!success) {
                            adsData().Interstitial_All_Fail_Web_Array
                                .randomOrNull()
                                ?.let { openCustomTabIntent(activity, it) }
                        }

                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE,activity)

                        }
                        AdCycleController.nextStep()
                    }.show()

                    return
                } else {
                    onComplete()
                    return
                }


            } else {
                onComplete()
                return
            }
        }catch (e: Exception){
            onComplete()
        }
    }



    fun showInterstitialLauncher1(
        activity: Activity,
        showLoader: Boolean = false,
        onComplete: () -> Unit,
    ) {
        try {

            if (adsData().INTER_LINKED_SHOW.isNullOrEmpty()) {
                onComplete()
                return
            }
            if (adsData().Ads_status && isNetworkAvailable(activity)) {
                Log.e("xxxxx44442", shouldShowAd().toString())
                if (!shouldShowAd()) {
                    Log.e("xxxxx44443", AdCycleController.getStep().toString())
                    onComplete()
                    return
                }
                if (adsData().INTER_LINKED_SHOW != null && adsData().INTER_LINKED_SHOW.size > 0) {

                    if (INTER_LINKED_COUNT_RECYCLE >= adsData().INTER_LINKED_SHOW.size) {
                        INTER_LINKED_COUNT_RECYCLE = 0   // global
                    }
                }

                    onComplete()

                    Log.e("xcasfterwe33xxxxx", adsData().Interstitial_All_Fail_Web_Array[0].toString())
                    Log.e("xcasfterwe44xxxxx", adsData().Interstitial_All_Fail_Web_Array[1].toString())
                    Log.e("xcasfterwe55xxxxx", adsData().Interstitial_All_Fail_Web_Array[2].toString())
                    adsData().Interstitial_All_Fail_Web_Array
                        .randomOrNull()
                        ?.let { openCustomTabIntent(activity, it) }




                    return



            } else {
                onComplete()
                return
            }
        }catch (e: Exception){
            onComplete()
        }
    }

    fun load_next_ad(interLinkedCountRecycle: Int,activity: Activity) {
        if (adsData().INTER_LINKED_SHOW != null && adsData().INTER_LINKED_SHOW.size > 0) {

            if (interLinkedCountRecycle >= adsData().INTER_LINKED_SHOW.size) {
                INTER_LINKED_COUNT_RECYCLE = 0   // global
            }
        }
        Log.e("#nextadddd", adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE])
        Log.e("xxxxxlllladd22", adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE])

        if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                "I",
                ignoreCase = true
            )
        ) {
            loadInterstitialLauncher(activity)
        }else if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                "A",
                ignoreCase = true
            )
        ) {
            GAds.appOpen?.loadAppOpenAd(
                activity,
                adsData().Appopen_All
            )
        }else if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                "N",
                ignoreCase = true
            )
        ) {
            preLoadNative(
                activity,
                adsData().Native_Interstitial,
                "FullScreenNative"
            )
        }
    }

    fun shouldShowAd(): Boolean {
        Log.e("#xxxxxadsClick", adsClick.toString());
        return if (adsClick >= adsData().Ads_click) {
            adsClick = 0
            if (adsData().Ads_click == 0) {
                Log.e("#xxxxxadsClick11", adsClick.toString());
                true
            } else {
                false
            }
        } else {
            if (adsData().Ads_click != 0) {
                adsClick++
            }
            false
        }
    }

}