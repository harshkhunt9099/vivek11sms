package com.launcher.launcherpackage.Codebizz_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class Codebizz_PackageChangeReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PackageChangeReceiver"
    }

    interface PackageChangeListener {
        fun onPackageInstalled(packageName: String)
        fun onPackageUninstalled(packageName: String)
        fun onPackageUpdated(packageName: String)
    }

    private val listeners = mutableSetOf<PackageChangeListener>()

    fun addListener(listener: PackageChangeListener) {
        listeners.add(listener)
    }

    fun removeAllListener() {
        listeners.forEach { listeners.remove(it) }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val action = intent.action
        val packageName = intent.data?.schemeSpecificPart

        Log.d(TAG, "Package change detected - Action: $action, Package: $packageName")

        when (action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (isReplacing) {
                    handlePackageUpdated(packageName)
                } else {
                    handlePackageInstalled(packageName)
                }
            }

            Intent.ACTION_PACKAGE_REMOVED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!isReplacing) {
                    handlePackageUninstalled(packageName)
                }
            }
            
        }

    }

    private fun handlePackageInstalled(packageName: String?) {
        if (packageName.isNullOrEmpty()) return

        Log.i(TAG, "Package installed: $packageName")

        // Notify all listeners
        listeners.forEach { listener ->
            listener.onPackageInstalled(packageName)
        }

    }

    private fun handlePackageUninstalled(packageName: String?) {
        if (packageName.isNullOrEmpty()) return

        Log.i(TAG, "Package uninstalled: $packageName")

        // Notify all listeners
        listeners.forEach { listener ->
            listener.onPackageUninstalled(packageName)
        }
    }

    private fun handlePackageUpdated(packageName: String?) {
        if (packageName.isNullOrEmpty()) return

        Log.i(TAG, "Package updated: $packageName")

        // Notify all listeners
        listeners.forEach { listener ->
            listener.onPackageUpdated(packageName)
        }
    }

    fun clear() {
        listeners.clear()
    }
}