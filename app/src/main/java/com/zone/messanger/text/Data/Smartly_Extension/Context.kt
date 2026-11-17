package com.zone.messanger.text.Data.Smartly_Extension

import android.app.Activity
import android.app.role.RoleManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.text.Editable
import android.text.TextWatcher
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsControllerCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.Data.AppsDatabase
import com.zone.messanger.text.Helper.BaseConfig
import com.zone.messanger.text.Helper.Config
import com.zone.messanger.text.Helper.DARK_GREY
import com.zone.messanger.text.Helper.PREFS_KEY
import com.zone.messanger.text.Helper.isOnMainThread
import com.zone.messanger.text.Helper.isQPlus
import com.zone.messanger.text.Helper.isSPlus
import com.zone.messanger.text.InterFaces.AppLaunchersDao
import com.zone.messanger.text.InterFaces.HomeScreenGridItemsDao
import com.zone.messanger.text.InterFaces.RecentAppsDao
import com.zone.messanger.text.Views.MyTextInputLayout
import com.zone.messanger.text.Views.MyTextView
import kotlin.math.ceil
import kotlin.math.max

val Context.config: Config get() = Config.newInstance(applicationContext)

val Context.windowManager: WindowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

fun Window.insetsController(view: View? = null): WindowInsetsControllerCompat {
    return WindowInsetsControllerCompat(this, view ?: decorView)
}

fun Window.setSystemBarsAppearance(backgroundColor: Int) {
    val isLightBackground = backgroundColor.getContrastColor() == DARK_GREY
    insetsController().apply {
        isAppearanceLightStatusBars = isLightBackground
        isAppearanceLightNavigationBars = isLightBackground
    }
}

val Context.baseConfig: BaseConfig get() = BaseConfig.newInstance(this)

val Context.launchersDB: AppLaunchersDao
    get() = AppsDatabase.getInstance(applicationContext).AppLaunchersDao()

val Context.recentAppsDB: RecentAppsDao
    get() = AppsDatabase.getInstance(applicationContext).RecentAppsDao()

val Context.homeScreenGridItemsDB: HomeScreenGridItemsDao
    get() = AppsDatabase.getInstance(
        applicationContext
    ).HomeScreenGridItemsDao()

@get:RequiresApi(Build.VERSION_CODES.Q)
val Context.roleManager: RoleManager
    get() = getSystemService(RoleManager::class.java)

fun Context.getDrawableForPackageName(packageName: String): Drawable? {
    var drawable: Drawable? = null
    try {
        // try getting the properly colored launcher icons
        val launcher = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val activityList = launcher.getActivityList(packageName, Process.myUserHandle())[0]
        drawable = activityList.getBadgedIcon(0)
    } catch (e: Exception) {
    } catch (e: Error) {
    }

    if (drawable == null) {
        drawable = try {
            packageManager.getApplicationIcon(packageName)
        } catch (ignored: Exception) {
            null
        }
    }

    return drawable
}

fun Context.getInitialCellSize(
    info: AppWidgetProviderInfo,
    fallbackWidth: Int,
    fallbackHeight: Int,
): Size {
    return if (isSPlus() && info.targetCellWidth != 0 && info.targetCellHeight != 0) {
        Size(info.targetCellWidth, info.targetCellHeight)
    } else {
        val widthCells = getCellCount(fallbackWidth)
        val heightCells = getCellCount(fallbackHeight)
        Size(widthCells, heightCells)
    }
}

fun Context.getCellCount(size: Int): Int {
    val tiles = ceil(((size / resources.displayMetrics.density) - 30) / 70.0).toInt()
    return max(tiles, 1)
}

fun Context.isDefaultLauncher(): Boolean {
    return if (isQPlus()) {
        with(roleManager) {
            isRoleAvailable(RoleManager.ROLE_HOME) && isRoleHeld(RoleManager.ROLE_HOME)
        }
    } else {
        val filters = ArrayList<IntentFilter>()
        val activities = ArrayList<ComponentName>()
        @Suppress("DEPRECATION")
        packageManager.getPreferredActivities(filters, activities, null)
        return activities.indices.any { i ->
            activities[i].packageName == packageName &&
                    filters[i].hasAction(Intent.ACTION_MAIN) &&
                    filters[i].hasCategory(Intent.CATEGORY_HOME)
        }
    }
}


fun Context.toast(id: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), length)
}

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (isOnMainThread()) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
    }
}

fun Context.showErrorToast(msg: String, length: Int = Toast.LENGTH_LONG) {
    toast(String.format(getString(R.string.lc_error), msg), length)
}

fun Context.showErrorToast(exception: Exception, length: Int = Toast.LENGTH_LONG) {
    showErrorToast(exception.toString(), length)
}

private fun doToast(context: Context, message: String, length: Int) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            Toast.makeText(context, message, length).show()
        }
    } else {
        Toast.makeText(context, message, length).show()
    }
}

fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

fun Context.isDynamicTheme() = isSPlus() && baseConfig.isSystemThemeEnabled

fun Context.isSystemInDarkMode() =
    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_YES != 0

fun Context.isWhiteTheme(): Boolean {
    return baseConfig.textColor == DARK_GREY
            && baseConfig.primaryColor == Color.WHITE
            && baseConfig.backgroundColor == Color.WHITE
}

fun Context.getPopupMenuTheme(): Int {
    return if (isDynamicTheme()) {
        if (isSystemInDarkMode()) {
            R.style.AppTheme_YouPopupMenuStyle
        } else {
            R.style.AppTheme_YouPopupMenuStyle_Light
        }
    } else if (isWhiteTheme()) {
        R.style.AppTheme_PopupMenuLightStyle
    } else {
        R.style.AppTheme_PopupMenuDarkStyle
    }
}

fun Context.getProperTextColor() = when {
    isDynamicTheme() -> resources.getColor(R.color.you_neutral_text_color, theme)
    else -> baseConfig.textColor
}

fun Context.getProperPrimaryColor() = when {
    isDynamicTheme() -> resources.getColor(R.color.you_primary_color, theme)
    isWhiteTheme() || isBlackAndWhiteTheme() -> baseConfig.accentColor
    else -> baseConfig.primaryColor
}

fun Context.isBlackAndWhiteTheme(): Boolean {
    return baseConfig.textColor == Color.WHITE
            && baseConfig.primaryColor == Color.BLACK
            && baseConfig.backgroundColor == Color.BLACK
}

fun Context.getProperBackgroundColor() = when {
    isDynamicTheme() -> resources.getColor(R.color.you_background_color, theme)
    else -> baseConfig.backgroundColor
}

fun Context.updateTextColors(viewGroup: ViewGroup) {
    val textColor = when {
        isDynamicTheme() -> getProperTextColor()
        else -> baseConfig.textColor
    }

    val backgroundColor = baseConfig.backgroundColor
    val accentColor = when {
        isWhiteTheme() || isBlackAndWhiteTheme() -> baseConfig.accentColor
        else -> getProperPrimaryColor()
    }

    val cnt = viewGroup.childCount
    (0 until cnt).map { viewGroup.getChildAt(it) }.forEach {
        when (it) {
            is MyTextView -> it.setColors(textColor, accentColor, backgroundColor)
            is MyTextInputLayout -> it.setColors(textColor, accentColor, backgroundColor)
            is ViewGroup -> updateTextColors(it)
        }
    }
}

val Context.realScreenSize: Point
    get() {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        return size
    }


fun Context.isPackageInstalled(pkgName: String): Boolean {
    return try {
        packageManager.getPackageInfo(pkgName, 0)
        true
    } catch (e: Exception) {
        false
    }
}

fun Context.openStore(packageName: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = "https://play.google.com/store/apps/details?id=${packageName}".toUri()
        })
    } catch (e: ActivityNotFoundException) {
        try {
            startActivity(createMarketIntent(packageName))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun createMarketIntent(packageName: String) = Intent(Intent.ACTION_VIEW).apply {
    data = "market://details?id=${packageName}".toUri()
    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
}


fun Context.getLauncherActivityName(packageName: String): String? {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        `package` = packageName
    }

    val resolveInfo = packageManager.queryIntentActivities(intent, 0)
        .firstOrNull()

    return resolveInfo?.activityInfo?.name
}

fun AlertDialog.showKeyboard(editText: AppCompatEditText) {
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    editText.apply {
        requestFocus()
        onGlobalLayout {
            setSelection(text.toString().length)
        }
    }
}

val EditText.value: String get() = text.toString().trim()

fun EditText.onTextChangeListener(onTextChangedAction: (newText: String) -> Unit) =
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onTextChangedAction(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })


fun Int.adjustAlpha(factor: Float): Int {
    val alpha = Math.round(Color.alpha(this) * factor)
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    return Color.argb(alpha, red, green, blue)
}

fun Int.getContrastColor(): Int {
    val luminance = ColorUtils.calculateLuminance(this)
    return if (luminance > 0.5) DARK_GREY else Color.WHITE
}


fun isXiaomiDevice(): Boolean {
    val info = "${Build.MANUFACTURER} ${Build.BRAND} ${Build.DEVICE} ${Build.MODEL}".lowercase()
    return info.contains("xiaomi") || info.contains("redmi") || info.contains("poco")
}


