package com.zone.messanger.text.Helper

import android.content.Context
import androidx.core.content.ContextCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.Data.Smartly_Extension.getSharedPrefs
import androidx.core.content.edit


open class BaseConfig(val context: Context) {
    protected val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = BaseConfig(context)
    }

    var textColor: Int
        get() = prefs.getInt(
            TEXT_COLOR,
            ContextCompat.getColor(context, R.color.you_neutral_text_color)
        )
        set(textColor) = prefs.edit { putInt(TEXT_COLOR, textColor) }

    var backgroundColor: Int
        get() = prefs.getInt(
            BACKGROUND_COLOR,
            ContextCompat.getColor(context, R.color.you_background_color)
        )
        set(backgroundColor) = prefs.edit { putInt(BACKGROUND_COLOR, backgroundColor) }

    var primaryColor: Int
        get() = prefs.getInt(
            PRIMARY_COLOR,
            ContextCompat.getColor(context, R.color.lc_default_primary_color)
        )
        set(primaryColor) = prefs.edit {putInt(PRIMARY_COLOR, primaryColor) }

    var accentColor: Int
        get() = prefs.getInt(
            ACCENT_COLOR,
            ContextCompat.getColor(context, R.color.lc_default_accent_color)
        )
        set(accentColor) = prefs.edit {putInt(ACCENT_COLOR, accentColor) }

    var isSystemThemeEnabled: Boolean
        get() = prefs.getBoolean(IS_SYSTEM_THEME_ENABLED, isSPlus())
        set(isSystemThemeEnabled) = prefs.edit {
            putBoolean(IS_SYSTEM_THEME_ENABLED, isSystemThemeEnabled)
        }
}