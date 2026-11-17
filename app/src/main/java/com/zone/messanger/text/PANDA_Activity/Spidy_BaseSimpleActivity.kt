package com.zone.messanger.text.PANDA_Activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.zone.messanger.text.Data.Smartly_Extension.baseConfig
import com.zone.messanger.text.Data.Smartly_Extension.getProperBackgroundColor
import com.zone.messanger.text.Data.Smartly_Extension.getThemeId
import com.zone.messanger.text.Data.Smartly_Extension.hideKeyboard

abstract class Spidy_BaseSimpleActivity : Spidy_EdgeToEdgeActivity() {
    var useDynamicTheme = true

    private lateinit var backCallback: OnBackPressedCallback


    protected open fun onBackPressedCompat(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (useDynamicTheme) {
            setTheme(getThemeId(showTransparentTop = true))
        }

        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
        registerBackPressedCallback()
    }

    override fun onResume() {
        super.onResume()
        if (useDynamicTheme) {
            setTheme(getThemeId(showTransparentTop = true))
            updateBackgroundColor(getProperBackgroundColor())
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        ViewCompat.requestApplyInsets(findViewById(android.R.id.content))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                finish()
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun registerBackPressedCallback() {
        backCallback = onBackPressedDispatcher.addCallback(this) {
            if (onBackPressedCompat()) return@addCallback
            // fallback to system
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
            isEnabled = true
        }
    }

    fun updateBackgroundColor(color: Int = baseConfig.backgroundColor) {
        window.decorView.setBackgroundColor(color)
    }


}
