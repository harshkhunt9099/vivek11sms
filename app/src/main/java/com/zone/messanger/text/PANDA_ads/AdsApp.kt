package com.zone.messanger.text.PANDA_ads

import android.Manifest
import android.app.Application
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import androidx.core.content.ContextCompat


open class AdsApp : Application() {


    companion object {
        lateinit var instance: AdsApp
    }

    var gAppOpen: GAppOpen? = null

    override fun onCreate() {
        super.onCreate()
        instance = this



        AdsManager.initialize(this)
        gAppOpen = GAppOpen(this)
    }
    private fun isDefaultAdsApp(): Boolean {
        val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_SMS) == true
        } else {
            val defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(this)
            packageName == defaultSmsPackage
        }

        val hasContactsPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        return isDefault || hasContactsPermission
    }

}