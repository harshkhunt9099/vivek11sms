package com.zone.messanger.text.PANDA_ads

import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zone.messanger.text.BuildConfig
import com.zone.messanger.text.Intro.SMS__Splash_Screen

import kotlin.collections.List

@Keep
data class AdsResponse(
    //static id
    val Native_Language: String = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110" else "ca-app-pub-3940256099942544/2247696110",

    val Home_banner1_onoff: Boolean = true,
    val home_Banner1true_Native1false: Boolean = true,
    val home_app_Bannertrue_Nativefalse: Boolean = true,
    val home_Bannertrue_Nativefalse: Boolean = true,
    val Home_banner_onoff: Boolean = true,
    val Home_banner_app_onoff: Boolean = true,
    val Ads_status: Boolean = true,
    val Organic_Marketing_Both: Boolean = true,
    val defultscreen_privacyshow_on_off: Boolean = true,
    val logadevent: Boolean = true,
    val stopanotherapp: Boolean = true,
    val APP_CLICK_SHOW_AD: Boolean = true,
    val INTRO_SCREEN: Boolean = true,
    val INTRO_SCREEN_under: Boolean = true,
    val Cancel_defult_on_off: Boolean = true,
    val Ads_App_Navigation: Boolean = false,
    val Native_All_Click2Change: Boolean = false,

    val Ads_click: Int = 0,
    val Ads_App_Navigation_Count: Int = 0,
    val Ads_Redirect: String = "3",
    val home_Native_Large_Small_Banner: String = "",
    val home_app_Native_Large_Small_Banner: String = "",
    val home_Native1_Large_Small_Banner: String = "",
    val Launcher_ON_OFF_ADDRESS: String = "",

    val Appopen_All: String = "",
    val Appopen_All_Fail_Web_Array: List<String> = emptyList(),
    val Banner_All: String = "",
    val Banner_Home1_1: String = "",
    val Banner_Home1_2: String = "",
    val Banner_All_1: String = "",
    val Banner_All_2: String = "",
    val Banner_All_swipe_1: String = "",
    val Banner_All_swipe_2: String = "",
    val Banner_Inline_Native_Fail: String = "",
    val Banner_All_Fail_Array: List<WebAdItem> = emptyList(),
    val Interstitial_All: String = "",
    var INTER_LINKED_SHOW: MutableList<String> = mutableListOf(),
    val Interstitial_All_Fail_Web_Array: List<String> = emptyList(),
    val intro_under_list: List<String> = emptyList(),
    val Native_Banner_All_Fail_Array: List<WebAdItem> = emptyList(),
    val Native_All: String = "",
    val Native_All_Left_Side_id: String = "",
    val Native_All_Right_Side_id: String = "",
    val Native_All_Center_Side_id: String = "",
    val Native_All_Fail_Array: List<WebAdItem> = emptyList(),
    val Native_Interstitial: String = "",
    val Native_Interstitial_Fail_Web_Array: List<String> = emptyList(),
    val Rewarded_Video: String = "",

    val Launcher_CustomApps_Array: List<OnlineLauncherApp> = emptyList(),
    val Wallpaper_Array: List<WallpaperCategory> = emptyList(),
) {


    companion object {


        fun remoteConfigReleaseAds(remoteConfig: FirebaseRemoteConfig): AdsResponse {
            return AdsResponse(



                Ads_status = if (SMS__Splash_Screen.isMarketingUser) {
                    Log.e("isMarketingUser222222", SMS__Splash_Screen.isMarketingUser.toString())

                    remoteConfig.getBoolean("Ads_status")
                } else {
                    Log.e("isMarketingUser333333", SMS__Splash_Screen.isMarketingUser.toString())
                        remoteConfig.getBoolean("Ads_status_normal_user")
                },
                home_Bannertrue_Nativefalse = remoteConfig.getBoolean("home_Bannertrue_Nativefalse"),
                Home_banner1_onoff = remoteConfig.getBoolean("Home_banner1_onoff"),
                home_Banner1true_Native1false = remoteConfig.getBoolean("home_Banner1true_Native1false"),
                home_app_Bannertrue_Nativefalse = remoteConfig.getBoolean("home_app_Bannertrue_Nativefalse"),
                Home_banner_onoff = remoteConfig.getBoolean("Home_banner_onoff"),
                Home_banner_app_onoff = remoteConfig.getBoolean("Home_banner_app_onoff"),
                Organic_Marketing_Both = remoteConfig.getBoolean("Organic_Marketing_Both"),
                defultscreen_privacyshow_on_off = remoteConfig.getBoolean("defultscreen_privacyshow_on_off"),
                logadevent = true,
                stopanotherapp = remoteConfig.getBoolean("stopanotherapp"),
                APP_CLICK_SHOW_AD = remoteConfig.getBoolean("APP_CLICK_SHOW_AD"),
                INTRO_SCREEN = remoteConfig.getBoolean("INTRO_SCREEN"),
                INTRO_SCREEN_under = true,
                Cancel_defult_on_off = remoteConfig.getBoolean("Cancel_defult_on_off"),
                Ads_Redirect = "",
                home_Native_Large_Small_Banner = remoteConfig.getString("home_Native_Large_Small_Banner"),
                home_app_Native_Large_Small_Banner = remoteConfig.getString("home_app_Native_Large_Small_Banner"),
                home_Native1_Large_Small_Banner = remoteConfig.getString("home_Native1_Large_Small_Banner"),
                Launcher_ON_OFF_ADDRESS = remoteConfig.getString("Launcher_ON_OFF_ADDRESS"),
                Ads_click = remoteConfig.getString("Ads_click").toIntOrNull() ?: 1,

                Native_All_Click2Change = true,
                Ads_App_Navigation = true,
                Ads_App_Navigation_Count = remoteConfig.getString("Ads_App_Navigation_Count")
                    .toIntOrNull() ?: 1,

                Appopen_All = remoteConfig.getString("Appopen_All"),
                Appopen_All_Fail_Web_Array = parseCommaString(remoteConfig.getString("Appopen_All_Fail_Web_Array")),
                Banner_All = remoteConfig.getString("Banner_All"),
                Banner_Home1_1 = remoteConfig.getString("Banner_Home1_1"),
                Banner_Home1_2 = remoteConfig.getString("Banner_Home1_2"),
                Banner_All_1 = remoteConfig.getString("Banner_All_1"),
                Banner_All_2 = remoteConfig.getString("Banner_All_2"),
                Banner_All_swipe_1 = remoteConfig.getString("Banner_All_swipe_1"),
                Banner_All_swipe_2 = remoteConfig.getString("Banner_All_swipe_2"),
                Banner_All_Fail_Array = parseJsonList(remoteConfig.getString("Banner_All_Fail_Array_ref")),
                Banner_Inline_Native_Fail = remoteConfig.getString("Banner_Inline_Native_Fail"),
                Interstitial_All = remoteConfig.getString("Interstitial_All"),
                INTER_LINKED_SHOW = parseCommaString(remoteConfig.getString("INTER_LINKED_SHOW")) as MutableList<String>,
                Interstitial_All_Fail_Web_Array = parseCommaString(remoteConfig.getString("Interstitial_All_Fail_Web_Array")),
                intro_under_list = parseCommaString(remoteConfig.getString("intro_under_list")),
                Native_Banner_All_Fail_Array = parseJsonList(remoteConfig.getString("Native_Banner_All_Fail_Array")),
                Native_All = remoteConfig.getString("Native_All"),
                Native_All_Left_Side_id = remoteConfig.getString("Native_All_Left_Side_id"),
                Native_All_Right_Side_id = remoteConfig.getString("Native_All_Right_Side_id"),
                Native_All_Center_Side_id = remoteConfig.getString("Native_All_Center_Side_id"),
                Native_All_Fail_Array =  parseJsonList(remoteConfig.getString("Native_All_Fail_Array_ref")),
                Native_Interstitial = remoteConfig.getString("Native_Interstitial"),
                Native_Interstitial_Fail_Web_Array = parseCommaString(remoteConfig.getString("Native_Interstitial_Fail_Web_Array")),
                Rewarded_Video = remoteConfig.getString("Rewarded_Video"),

                Launcher_CustomApps_Array = parseJsonList(""),
                Wallpaper_Array = parseJsonList("[ {     \"categoryName\": \"Popular\",     \"wallpapers\": [     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/popular_1.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/thumb/popular_thumb_1.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/popular_2.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/thumb/popular_thumb_2.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/popular_3.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/thumb/popular_thumb_3.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/popular_4.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/thumb/popular_thumb_4.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/popular_5.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/thumb/popular_thumb_5.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/popular_6.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/popular/thumb/popular_thumb_6.webp\"     }     ] }, {     \"categoryName\": \"Default\",     \"wallpapers\": [     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/default_1.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/thumb/default_thumb_1.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/default_2.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/thumb/default_thumb_2.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/default_3.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/thumb/default_thumb_3.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/default_4.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/thumb/default_thumb_4.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/default_5.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/thumb/default_thumb_5.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/default_6.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/default/thumb/default_thumb_6.webp\"     }     ] }, {     \"categoryName\": \"Nature\",     \"wallpapers\": [     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/nature_1.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/thumb/nature_thumb_1.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/nature_2.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/thumb/nature_thumb_2.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/nature_3.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/thumb/nature_thumb_3.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/nature_4.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/thumb/nature_thumb_4.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/nature_5.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/thumb/nature_thumb_5.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/nature_6.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/nature/thumb/nature_thumb_6.webp\"     }     ] }, {     \"categoryName\": \"Night\",     \"wallpapers\": [     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/night_1.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/thumb/night_thumb_1.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/night_2.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/thumb/night_thumb_2.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/night_3.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/thumb/night_thumb_3.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/night_4.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/thumb/night_thumb_4.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/night_5.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/thumb/night_thumb_5.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/night_6.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/night/thumb/night_thumb_6.webp\"     }     ] }, {     \"categoryName\": \"Car\",     \"wallpapers\": [     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/car_1.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/thumb/car_thumb_1.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/car_2.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/thumb/car_thumb_2.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/car_3.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/thumb/car_thumb_3.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/car_4.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/thumb/car_thumb_4.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/car_5.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/thumb/car_thumb_5.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/car_6.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/car/thumb/car_thumb_6.webp\"     }     ] }, {     \"categoryName\": \"Other\",     \"wallpapers\": [     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/other_1.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/thumb/other_thumb_1.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/other_2.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/thumb/other_thumb_2.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/other_3.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/thumb/other_thumb_3.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/other_4.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/thumb/other_thumb_4.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/other_5.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/thumb/other_thumb_5.webp\"     },     {         \"imageUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/other_6.webp\",         \"thumbUrl\": \"https://photovideomakerwithmusic.com/ax_app_launcher/wallpaper/other/thumb/other_thumb_6.webp\"     }     ] } ]"),
            )
        }


        inline fun <reified T> parseJsonList(json: String): List<T> {
            if (json.isBlank()) return emptyList()

            return try {
                val type = object : TypeToken<List<T>>() {}.type
                Gson().fromJson<List<T>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("exxxxx",e.toString());
                emptyList()
            }
        }

        fun parseCommaString(json: String): List<String> {
            if (json.isBlank()) return emptyList()

            return try {
                json.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
            } catch (e: Exception) {
                emptyList()
            }
        }

    }



}