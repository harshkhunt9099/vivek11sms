package com.launcher.launcherpackage.Codebizz_helper

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlin.jvm.java

object Codebizz_PackageMonitorPrefManager {

    private lateinit var sharedPreferences: SharedPreferences
    var tabLayoutColor: String = ""

    fun init(context: Context,customColor: String? = null) {
        sharedPreferences = context.getSharedPreferences("PackageMonitorPref", Context.MODE_PRIVATE)
        tabLayoutColor = (customColor ?: "#328EEF")
    }

    fun setPackageMonitorAdsResponse(adsResponse: Codebizz_PackageMonitorAdsResponse) {
        sharedPreferences.edit { putString("PackageMonitorAdsResponse", Gson().toJson(adsResponse)) }
    }

    fun getPackageMonitorAdsResponse(): Codebizz_PackageMonitorAdsResponse {
        val json = sharedPreferences.getString("PackageMonitorAdsResponse", null)

        return try {
            if (!json.isNullOrEmpty()) {
                Gson().fromJson(json, Codebizz_PackageMonitorAdsResponse::class.java)
            } else {
                Codebizz_PackageMonitorAdsResponse()
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            Codebizz_PackageMonitorAdsResponse()
        }
    }

    var installScanEnabled: Boolean
        get() = sharedPreferences.getBoolean("KEY_INSTALL_SCAN", true)
        set(installScanEnabled) = sharedPreferences.edit { putBoolean("KEY_INSTALL_SCAN", installScanEnabled) }

    var uninstallScanEnabled: Boolean
        get() = sharedPreferences.getBoolean("KEY_UNINSTALL_SCAN", true)
        set(uninstallScanEnabled) = sharedPreferences.edit { putBoolean("KEY_UNINSTALL_SCAN", uninstallScanEnabled) }


}