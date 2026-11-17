package com.zone.messanger.text.Data.Smartly_Extension

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherApps
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zone.messanger.text.R
import com.zone.messanger.text.databinding.DialogTitleBinding
import com.zone.messanger.text.Helper.DARK_GREY
import com.zone.messanger.text.Helper.ITEM_TYPE_FOLDER
import com.zone.messanger.text.Helper.ITEM_TYPE_ICON
import com.zone.messanger.text.Helper.ITEM_TYPE_WIDGET
import com.zone.messanger.text.Helper.UNINSTALL_APP_REQUEST_CODE
import com.zone.messanger.text.Helper.isOnMainThread
import com.zone.messanger.text.Helper.isQPlus
import com.zone.messanger.text.Helper.isSPlus
import com.zone.messanger.text.InterFaces.ItemMenuListener
import com.zone.messanger.text.Models.HomeScreenGridItem
import com.zone.messanger.text.Views.MyTextView

fun Activity.launchApp(packageName: String, activityName: String) {
    try {
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
             `package`= packageName
             component = ComponentName.unflattenFromString("$packageName/$activityName")
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            startActivity(this)
        }
    } catch (e: Exception) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(launchIntent)
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }
}

fun Activity.launchAppInfo(packageName: String) {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        startActivity(this)
    }
}

fun Activity.canAppBeUninstalled(packageName: String): Boolean {
    return try {
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
    } catch (ignored: Exception) {
        false
    }
}

fun Activity.uninstallApp(packageName: String) {
    Intent(Intent.ACTION_DELETE).apply {
        data = Uri.fromParts("package", packageName, null)
        startActivityForResult(this, UNINSTALL_APP_REQUEST_CODE)
    }
}

fun Activity.handleGridItemPopupMenu(
    anchorView: View,
    gridItem: HomeScreenGridItem,
    isOnAllAppsFragment: Boolean,
    listener: ItemMenuListener,
): PopupMenu {
    val contextTheme = ContextThemeWrapper(this, getPopupMenuTheme())
    return PopupMenu(contextTheme, anchorView, Gravity.TOP or Gravity.END).apply {
        if (isQPlus()) {
            setForceShowIcon(true)
        }

        inflate(R.menu.menu_app_icon)
        menu.forEach {
            val default = getProperTextColor()
            val color = if (isSPlus() && isDynamicTheme()) {
                default
            } else {
                MaterialColors.getColor(contextTheme, android.R.attr.actionMenuTextColor, default)
            }
            it.iconTintList = ColorStateList.valueOf(color)
        }
        menu.findItem(R.id.rename).isVisible =
            (gridItem.type == ITEM_TYPE_FOLDER) && !isOnAllAppsFragment
        menu.findItem(R.id.resize).isVisible = gridItem.type == ITEM_TYPE_WIDGET
        menu.findItem(R.id.app_info).isVisible = gridItem.type == ITEM_TYPE_ICON
        menu.findItem(R.id.uninstall).isVisible = gridItem.type == ITEM_TYPE_ICON
                && canAppBeUninstalled(gridItem.packageName)
                && gridItem.packageName != packageName
        menu.findItem(R.id.remove).isVisible = !isOnAllAppsFragment

        val launcherApps =
            applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val shortcuts = if (launcherApps.hasShortcutHostPermission()) {
            try {
                val query = LauncherApps.ShortcutQuery().setQueryFlags(
                    LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
                ).setPackage(gridItem.packageName)
                launcherApps.getShortcuts(query, Process.myUserHandle())
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        setOnMenuItemClickListener { item ->
            listener.onAnyClick()
            when (item.itemId) {
                R.id.rename -> listener.rename(gridItem)
                R.id.resize -> listener.resize(gridItem)
                R.id.app_info -> listener.appInfo(gridItem)
                R.id.remove -> listener.remove(gridItem)
                R.id.uninstall -> listener.uninstall(gridItem)
            }
            true
        }

        setOnDismissListener {
            listener.onDismiss()
        }

        listener.beforeShow(menu)

        show()
    }
}

fun Activity.showKeyboard(et: EditText) {
    et.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity.hideKeyboard() {
    if (isOnMainThread()) {
        hideKeyboardSync()
    } else {
        Handler(Looper.getMainLooper()).post {
            hideKeyboardSync()
        }
    }
}

fun Activity.hideKeyboardSync() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    currentFocus?.clearFocus()
}

fun Activity.getAlertDialogBuilder() = if (isDynamicTheme()) {
    MaterialAlertDialogBuilder(this)
} else {
    AlertDialog.Builder(this)
}


fun Activity.setupDialogStuff(
    view: View,
    dialog: AlertDialog.Builder,
    titleId: Int = 0,
    titleText: String = "",
    cancelOnTouchOutside: Boolean = true,
    callback: ((alertDialog: AlertDialog) -> Unit)? = null,
) {
    if (isDestroyed || isFinishing) {
        return
    }

    val textColor = getProperTextColor()
    val backgroundColor = getProperBackgroundColor()
    val primaryColor = getProperPrimaryColor()
    if (view is ViewGroup) {
        updateTextColors(view)
    } else if (view is MyTextView) {
        view.setColors(textColor, primaryColor, backgroundColor)
    }

    if (dialog is MaterialAlertDialogBuilder) {
        dialog.create().apply {
            if (titleId != 0) {
                setTitle(titleId)
            } else if (titleText.isNotEmpty()) {
                setTitle(titleText)
            }

            setView(view)
            setCancelable(cancelOnTouchOutside)
            if (!isFinishing) {
                show()
            }
            getButton(Dialog.BUTTON_POSITIVE)?.apply {
                setTextColor(primaryColor)
            }
            getButton(Dialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(primaryColor)
            }
            getButton(Dialog.BUTTON_NEUTRAL)?.apply {
                setTextColor(primaryColor)
            }
            callback?.invoke(this)
        }
    } else {
        var title: DialogTitleBinding? = null
        if (titleId != 0 || titleText.isNotEmpty()) {
            title = DialogTitleBinding.inflate(layoutInflater, null, false)
            title.dialogTitleTextview.apply {
                if (titleText.isNotEmpty()) {
                    text = titleText
                } else {
                    setText(titleId)
                }
                setTextColor(textColor)
            }
        }

        // if we use the same primary and background color, use the text color for dialog confirmation buttons
        val dialogButtonColor = if (primaryColor == baseConfig.backgroundColor) {
            textColor
        } else {
            primaryColor
        }

        dialog.create().apply {
            setView(view)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCustomTitle(title?.root)
            setCanceledOnTouchOutside(cancelOnTouchOutside)
            if (!isFinishing) {
                show()
            }
            getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setTextColor(dialogButtonColor)
            }
            getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setTextColor(dialogButtonColor)
            }
            getButton(AlertDialog.BUTTON_NEUTRAL)?.apply {
                setTextColor(dialogButtonColor)
            }


            val bgDrawable = when {
                isBlackAndWhiteTheme() -> resources.getDrawable(
                    R.drawable.lc_black_dialog_background,
                    theme
                )

                isDynamicTheme() -> resources.getDrawable(R.drawable.lc_dialog_you_background, theme)
                else -> resources.getColoredDrawableWithColor(
                    R.drawable.lc_dialog_bg,
                    baseConfig.backgroundColor
                )
            }

            window?.setBackgroundDrawable(bgDrawable)
            callback?.invoke(this)
        }
    }
}

fun Activity.onApplyWindowInsets(callback: (WindowInsetsCompat) -> Unit) {
    window.decorView.setOnApplyWindowInsetsListener { view, insets ->
        callback(WindowInsetsCompat.toWindowInsetsCompat(insets))
        view.onApplyWindowInsets(insets)
        insets
    }
}


inline fun <T : ViewBinding> Activity.viewBinding(crossinline bindingInflater: (LayoutInflater) -> T) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }


fun Activity.getThemeId(color: Int = baseConfig.primaryColor, showTransparentTop: Boolean = false) =
    when {
        isDynamicTheme() -> if (isSystemInDarkMode()) R.style.AppTheme_Base_System else R.style.AppTheme_Base_System_Light
        isBlackAndWhiteTheme() -> when {
            showTransparentTop -> R.style.AppTheme_BlackAndWhite_NoActionBar
            baseConfig.primaryColor.getContrastColor() == DARK_GREY -> R.style.AppTheme_BlackAndWhite_DarkTextColor
            else -> R.style.AppTheme_BlackAndWhite
        }

        isWhiteTheme() -> when {
            showTransparentTop -> R.style.AppTheme_White_NoActionBar
            baseConfig.primaryColor.getContrastColor() == Color.WHITE -> R.style.AppTheme_White_LightTextColor
            else -> R.style.AppTheme_White
        }

        else -> {
            R.style.AppTheme_Green_900_core
        }
    }



fun Activity.applyBlur(radius: Float = 5f) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.window?.decorView?.findViewById<View>(android.R.id.content)?.setRenderEffect(
            RenderEffect.createBlurEffect(
                radius,
                radius,
                Shader.TileMode.CLAMP
            )
        )
    }
}


fun Activity.clearBlur() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.window?.decorView?.findViewById<View>(android.R.id.content)?.setRenderEffect(null)
    }
}


