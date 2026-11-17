package com.zone.messanger.text.PANDA_ads

import android.animation.ValueAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.net.toUri

private const val TAG = "AdsUtils"

private fun Context.isPackageInstalled(packageName: String): Boolean {
    val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return false
    val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return list.isNotEmpty()
}


fun Context.launchOrRedirectToPlayStore(packageName: String) {
    Log.e(TAG,
        "launchOrRedirectToPlayStore isPackageInstalled: ${isPackageInstalled(packageName)}")

    if (isPackageInstalled(packageName)) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(launchIntent)
        } catch (e: Exception) {
            Log.e(TAG, "launchOrRedirectToPlayStore: ${e.message}")
        }
    } else {
        openStore(packageName)
    }
    if (this is Activity) finishAffinity()
}


private fun Context.openStore(packageName: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = "https://play.google.com/store/apps/details?id=${packageName}".toUri()
        })
    } catch (e: ActivityNotFoundException) {
        try {
            startActivity(createMarketIntent(packageName))
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}

private fun createMarketIntent(packageName: String) = Intent(Intent.ACTION_VIEW).apply {
    data = "market://details?id=${packageName}".toUri()
    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
}


fun appLogEvent(eventName: String, params: Bundle) {
    try {
        MyApplication.firebaseLogAnalytics?.logEvent(eventName, params)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.animateFoldUpFromTopAndHide(duration: Long = 700, onEnd: (() -> Unit)? = null) {
    val startHeight = this.height

    val animator = ValueAnimator.ofInt(startHeight, 0).apply {
        this.duration = duration

        addUpdateListener { valueAnimator ->
            val newHeight = valueAnimator.animatedValue as Int
            val layoutParams = this@animateFoldUpFromTopAndHide.layoutParams
            layoutParams.height = newHeight
            this@animateFoldUpFromTopAndHide.layoutParams = layoutParams
        }

        doOnEnd {
            val layoutParams = this@animateFoldUpFromTopAndHide.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            this@animateFoldUpFromTopAndHide.layoutParams = layoutParams

            onEnd?.invoke()
        }
    }

    animator.start()
}