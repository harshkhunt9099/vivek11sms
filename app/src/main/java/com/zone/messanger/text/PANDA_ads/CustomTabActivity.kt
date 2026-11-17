package com.zone.messanger.text.PANDA_ads

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.zone.messanger.text.PANDA_ads.AdsManager.isFromPermission

class CustomTabActivity : AppCompatActivity() {

    private var launched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url") ?: finish()

        if (!launched) {
            launched = true
            openCustomTabIntent(this, url.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        if (launched) {
            setResult(RESULT_OK)
            finish()
        }
    }


    fun openCustomTabIntent(context: Context, url: String) {
        if (url.isBlank()) return


        val uri = url.toUri()

        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .build()

        customTabsIntent.intent.setPackage("com.android.chrome")

        try {
            customTabsIntent.launchUrl(context, uri)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri)
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        isFromPermission = true
    }
}