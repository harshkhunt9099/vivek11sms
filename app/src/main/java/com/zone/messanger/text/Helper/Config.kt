package com.zone.messanger.text.Helper

import android.content.Context
import androidx.core.content.edit
import com.zone.messanger.text.BuildConfig
import com.zone.messanger.text.R
import com.zone.messanger.text.Models.GestureAction
import com.zone.messanger.text.Models.GestureType
import com.zone.messanger.text.Models.SearchEngine

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var wasHomeScreenInit: Boolean
        get() = prefs.getBoolean(WAS_HOME_SCREEN_INIT, false)
        set(wasHomeScreenInit) = prefs.edit { putBoolean(WAS_HOME_SCREEN_INIT, wasHomeScreenInit) }

    var homeColumnCount: Int
        get() = prefs.getInt(HOME_COLUMN_COUNT, COLUMN_COUNT)
        set(homeColumnCount) = prefs.edit { putInt(HOME_COLUMN_COUNT, homeColumnCount) }

    var homeRowCount: Int
        get() = prefs.getInt(HOME_ROW_COUNT, ROW_COUNT)
        set(homeRowCount) = prefs.edit { putInt(HOME_ROW_COUNT, homeRowCount) }

    var drawerColumnCount: Int
        get() = prefs.getInt(
            DRAWER_COLUMN_COUNT,
            context.resources.getInteger(R.integer.lc_portrait_column_count)
        )
        set(drawerColumnCount) = prefs.edit { putInt(DRAWER_COLUMN_COUNT, drawerColumnCount) }

    var showSearchBar: Boolean
        get() = prefs.getBoolean(SHOW_SEARCH_BAR, true)
        set(showSearchBar) = prefs.edit { putBoolean(SHOW_SEARCH_BAR, showSearchBar) }

    var closeAppDrawer: Boolean
        get() = prefs.getBoolean(CLOSE_APP_DRAWER, true)
        set(closeAppDrawer) = prefs.edit { putBoolean(CLOSE_APP_DRAWER, closeAppDrawer) }

    var autoShowKeyboardInAppDrawer: Boolean
        get() = prefs.getBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, false)
        set(autoShowKeyboardInAppDrawer) = prefs.edit {
            putBoolean(AUTO_SHOW_KEYBOARD_IN_APP_DRAWER, autoShowKeyboardInAppDrawer)
        }

    var drawerLabelHide: Boolean
        get() = prefs.getBoolean(DRAWER_LABEL_HIDE, false)
        set(drawerLabelHide) = prefs.edit {
            putBoolean(DRAWER_LABEL_HIDE, drawerLabelHide)
        }

    var homeLabelHide: Boolean
        get() = prefs.getBoolean(HOME_LABEL_HIDE, false)
        set(homeLabelHide) = prefs.edit {
            putBoolean(HOME_LABEL_HIDE, homeLabelHide)
        }

    var tutorialShow: Boolean
        get() = prefs.getBoolean(TUTORIAL_SHOW, false)
        set(tutorialShow) = prefs.edit {
            putBoolean(TUTORIAL_SHOW, tutorialShow)
        }

    var showNavigationBar: Boolean
        get() = prefs.getBoolean(SHOW_NAVIGATION_BAR, false)
        set(showNavigationBar) = prefs.edit {
            putBoolean(SHOW_NAVIGATION_BAR, showNavigationBar)
        }

    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean(IS_DARK_MODE_ENABLED, false)
        set(isDarkModeEnabled) = prefs.edit {
            putBoolean(IS_DARK_MODE_ENABLED, isDarkModeEnabled)
        }

    var isConsentChecked: Boolean
        get() = prefs.getBoolean(IS_CONSENT_CHECKED, false)
        set(isConsentChecked) = prefs.edit {
            putBoolean(IS_CONSENT_CHECKED, isConsentChecked)
        }

    var isLanguageChecked: Boolean
        get() = prefs.getBoolean(IS_CONSENT_CHECKED, false)
        set(isLanguageChecked) = prefs.edit {
            putBoolean(IS_CONSENT_CHECKED, isLanguageChecked)
        }


    var navigationUserOverride: Boolean
        get() = prefs.getBoolean(KEY_NAV_USER_OVERRIDE, false)
        set(navigationUserOverride) = prefs.edit {
            putBoolean(KEY_NAV_USER_OVERRIDE, navigationUserOverride)
        }

    var showAppSuggestion: Boolean
        get() = prefs.getBoolean(SHOW_APP_SUGGESTION, true)
        set(showAppSuggestion) = prefs.edit { putBoolean(SHOW_APP_SUGGESTION, showAppSuggestion) }

    var homeIconSize: Float
        get() = prefs.getFloat(HOME_ICON_SIZE, 0.85f)
        set(homeIconSize) = prefs.edit { putFloat(HOME_ICON_SIZE, homeIconSize) }

    var homeTextSize: Float
        get() = prefs.getFloat(HOME_TEXT_SIZE, 12.0f)
        set(homeTextSize) = prefs.edit { putFloat(HOME_TEXT_SIZE, homeTextSize) }

    var drawerIconSize: Float
        get() = prefs.getFloat(DRAWER_ICON_SIZE, 0.85f)
        set(drawerIconSize) = prefs.edit { putFloat(DRAWER_ICON_SIZE, drawerIconSize) }

    var drawerTextSize: Float
        get() = prefs.getFloat(DRAWER_TEXT_SIZE, 12.0f)
        set(drawerTextSize) = prefs.edit { putFloat(DRAWER_TEXT_SIZE, drawerTextSize) }


    /* ---------------- Search Engine ---------------- */

    var searchEngine: SearchEngine
        get() {
            val value = prefs.getString(KEY_SEARCH_ENGINE, null)
            return runCatching {
                SearchEngine.valueOf(value ?: "")
            }.getOrDefault(SearchEngine.GOOGLE)
        }
        set(value) {
            prefs.edit {
                putString(KEY_SEARCH_ENGINE, value.name)
            }
        }


    fun setLastRemoteConfigFetchTime(timeMillis: Long = System.currentTimeMillis()) {
        prefs.edit { putLong(KEY_LAST_REMOTE_CONFIG_FETCH, timeMillis) }
    }

    fun getLastRemoteConfigFetchTime(): Long {
        return prefs.getLong(KEY_LAST_REMOTE_CONFIG_FETCH, 0L)
    }

    fun canFetchRemoteConfig(intervalMillis: Long): Boolean {
        val lastFetch = getLastRemoteConfigFetchTime()
        return System.currentTimeMillis() - lastFetch > intervalMillis
    }


    /* ---------------- Gesture Setting ---------------- */
    private fun gestureKey(type: GestureType): String {
        return when (type) {
            GestureType.DOUBLE_TAP -> KEY_GESTURE_DOUBLE_TAP
            GestureType.TAP_HOLD -> KEY_GESTURE_TAP_HOLD
            GestureType.HOME_BUTTON -> KEY_GESTURE_HOME_BUTTON
            GestureType.BACK_BUTTON -> KEY_GESTURE_BACK_BUTTON
        }
    }

    private fun defaultGestureAction(type: GestureType): GestureAction {
        return when (type) {
            GestureType.TAP_HOLD -> GestureAction.OPEN_HOME_POPUP
            else -> GestureAction.NONE
        }
    }

    fun setGestureAction(type: GestureType, action: GestureAction) {
        prefs.edit {
            putString(gestureKey(type), action.name)
        }
    }

    fun getGestureAction(type: GestureType): GestureAction {
        val savedValue = prefs.getString(gestureKey(type), null)

        return if (savedValue == null) {
            defaultGestureAction(type)
        } else {
            runCatching {
                GestureAction.valueOf(savedValue)
            }.getOrDefault(defaultGestureAction(type))
        }
    }


    var currentVersionCode: Int
        get() = prefs.getInt(
            CURRENT_VERSION_CODE,
            BuildConfig.VERSION_CODE
        )
        set(currentVersionCode) = prefs.edit { putInt(CURRENT_VERSION_CODE, currentVersionCode) }

    var overlayDialogCount: Int
        get() = prefs.getInt(
            OVERLAY_DIALOG_COUNT,
            0
        )
        set(overlayDialogCount) = prefs.edit { putInt(OVERLAY_DIALOG_COUNT, overlayDialogCount) }


    var remainingWallpaperCount: Int
        get() = prefs.getInt(
            "RemainingWallpaperCount",
            0
        )
        set(value) = prefs.edit { putInt("RemainingWallpaperCount", value) }


}
