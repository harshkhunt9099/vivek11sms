package com.zone.messanger.text.Data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat

class LauncherHomePressWatcher(private val context: Context) {

    private var listener: (() -> Unit)? = null
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra("reason")
                Log.d("TAG_RECEIVER", "onReceive: $reason")
                if (reason == "homekey") {
                    listener?.invoke()
                }
            }
        }
    }

    private var isRegistered = false

    fun setOnHomePressedListener(listener: () -> Unit) {
        this.listener = listener
    }

    fun startWatch() {
        if (!isRegistered) {
            val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            isRegistered = true
        }
    }

    fun stopWatch() {
        if (isRegistered) {
            context.unregisterReceiver(receiver)
            isRegistered = false
        }
    }

    fun isWatching(): Boolean {
        return isRegistered
    }
}