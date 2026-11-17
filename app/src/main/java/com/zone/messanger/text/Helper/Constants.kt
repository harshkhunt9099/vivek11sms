package com.zone.messanger.text.Helper

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.zone.messanger.text.PANDA_ads.OfflineLauncherApp
import com.zone.messanger.text.PANDA_ads.LauncherAppUiModel
import com.zone.messanger.text.PANDA_ads.OnlineLauncherApp
import com.zone.messanger.text.Models.AppLauncher
import com.zone.messanger.text.Models.RecentApps
import java.text.Normalizer

const val PREFS_KEY = "Prefs"
const val TEXT_COLOR = "text_color"
const val BACKGROUND_COLOR = "background_color"
const val PRIMARY_COLOR = "primary_color_2"
const val ACCENT_COLOR = "accent_color"
const val IS_SYSTEM_THEME_ENABLED = "is_using_system_theme"
val DARK_GREY = 0xFF333333.toInt()

const val LOWER_ALPHA = 0.25f
const val MEDIUM_ALPHA = 0.5f
const val HIGHER_ALPHA = 0.75f

const val ITEM_TYPE_ICON = 0
const val ITEM_TYPE_WIDGET = 1
const val ITEM_TYPE_SHORTCUT = 2
const val ITEM_TYPE_FOLDER = 3

// default home screen grid size
const val ROW_COUNT = 8
const val COLUMN_COUNT = 5

// shared prefs
const val WAS_HOME_SCREEN_INIT = "was_home_screen_init"
const val HOME_ROW_COUNT = "home_row_count"
const val HOME_COLUMN_COUNT = "home_column_count"
const val DRAWER_COLUMN_COUNT = "drawer_column_count"
const val SHOW_SEARCH_BAR = "show_search_bar"
const val CLOSE_APP_DRAWER = "close_app_drawer"
const val AUTO_SHOW_KEYBOARD_IN_APP_DRAWER = "auto_show_keyboard_in_app_drawer"
const val DRAWER_LABEL_HIDE = "drawer_label_hide"
const val HOME_LABEL_HIDE = "home_label_hide"

const val TUTORIAL_SHOW = "tutorial_show"

const val HOME_TEXT_SIZE = "home_text_size"
const val HOME_ICON_SIZE = "home_icon_size"

const val DRAWER_TEXT_SIZE = "drawer_text_size"
const val DRAWER_ICON_SIZE = "drawer_icon_size"

const val KEY_SEARCH_ENGINE = "key_search_engine"

const val KEY_GESTURE_DOUBLE_TAP = "key_gesture_double_tap"
const val KEY_GESTURE_TAP_HOLD = "key_gesture_tap_hold"
const val KEY_GESTURE_HOME_BUTTON = "key_gesture_home_button"
const val KEY_GESTURE_BACK_BUTTON = "key_gesture_back_button"

const val IS_CONSENT_CHECKED = "is_consent_checked"
const val CURRENT_VERSION_CODE = "current_version_code"

const val OVERLAY_DIALOG_COUNT = "overlay_dialog_count"

const val IS_DARK_MODE_ENABLED = "is_dark_mode_enabled"
const val SHOW_NAVIGATION_BAR = "show_navigation_bar"
const val SHOW_APP_SUGGESTION = "show_app_suggestion"
const val MAX_CLICK_DURATION = 150
const val KEY_LAST_REMOTE_CONFIG_FETCH = "key_last_remote_config_fetch"
const val KEY_NAV_USER_OVERRIDE = "nav_user_override"

const val SORT_BY_TITLE = 2048
const val SORT_DESCENDING = 1024
const val WIDGET_HOST_ID = 12345

const val UNINSTALL_APP_REQUEST_CODE = 50
const val REQUEST_CONFIGURE_WIDGET = 51
const val REQUEST_ALLOW_BINDING_WIDGET = 52
const val REQUEST_CREATE_SHORTCUT = 53
const val REQUEST_SET_DEFAULT = 54

const val WIDGET_LIST_SECTION = 0
const val WIDGET_LIST_ITEMS_HOLDER = 1


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

private val mainHandler = Handler(Looper.getMainLooper())

fun runOnMainThread(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        mainHandler.post(block)
    }
}

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}

val normalizeRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun String.normalizeString() =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(normalizeRegex, "")



fun RecentApps.toAppLauncher(pm: PackageManager): AppLauncher {

    val loadedDrawable = try {
        pm.getApplicationIcon(this.packageName)
    } catch (e: Exception) {
        null
    }

    return AppLauncher(
        id = null,
        title = this.title,
        packageName = this.packageName,
        activityName = this.activityName,
        order = 0,
        thumbnailColor = this.thumbnailColor,
        drawable = loadedDrawable
    )
}



fun OfflineLauncherApp.toUiItem(): LauncherAppUiModel {
    return LauncherAppUiModel(
        title = label,
        iconDrawable = icon,
        packageName = packageName
    )
}

fun OnlineLauncherApp.toUiItem(): LauncherAppUiModel {
    return LauncherAppUiModel(
        title = title,
        iconUrl = logo,
        packageName = packageName
    )
}

private fun requireDrawable(context: Context, @DrawableRes resId: Int): Drawable {
    return ContextCompat.getDrawable(context, resId)
        ?: error("Drawable resource not found: $resId")
}
