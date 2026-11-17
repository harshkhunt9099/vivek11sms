package com.zone.messanger.text.Data.Smartly_Extension

import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.HapticFeedbackConstants
import android.view.RoundedCorner.POSITION_TOP_LEFT
import android.view.RoundedCorner.POSITION_TOP_RIGHT
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.zone.messanger.text.R
import com.zone.messanger.text.Helper.isSPlus


fun Drawable.applyColorFilter(color: Int) = mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)

fun ImageView.applyColorFilter(color: Int) = setColorFilter(color, PorterDuff.Mode.SRC_IN)

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.performHapticFeedback() = performHapticFeedback(
    HapticFeedbackConstants.VIRTUAL_KEY,
    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
)

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (viewTreeObserver != null) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

fun View.animateScale(
    from: Float,
    to: Float,
    duration: Long,
) = animate()
    .scaleX(to)
    .scaleY(to)
    .setDuration(duration)
    .setInterpolator(AccelerateDecelerateInterpolator())
    .withStartAction {
        scaleX = from
        scaleY = from
    }

fun View.setupDrawerBackground() {
    val backgroundColor = context.getProperBackgroundColor()
    background = backgroundColor.toDrawable()

    val insets = rootWindowInsets
    if (isSPlus() && insets != null) {
        val topRightCorner = insets.getRoundedCorner(POSITION_TOP_RIGHT)?.radius ?: 0
        val topLeftCorner = insets.getRoundedCorner(POSITION_TOP_LEFT)?.radius ?: 0
        if (topRightCorner > 0 && topLeftCorner > 0) {
            background = ResourcesCompat.getDrawable(
                context.resources, R.drawable.lc_bottom_sheet_bg, context.theme
            ).apply {
                (this as LayerDrawable)
                    .findDrawableByLayerId(R.id.bottom_sheet_background)
                    .applyColorFilter(backgroundColor)
            }
        }
    }
}

fun View.updatePaddingWithBase(
    @Px left: Int? = null,
    @Px top: Int? = null,
    @Px right: Int? = null,
    @Px bottom: Int? = null,
) {
    val base = ensureBasePadding()
    updatePadding(
        left = left?.let { base[0] + it } ?: paddingLeft,
        top = top?.let { base[1] + it } ?: paddingTop,
        right = right?.let { base[2] + it } ?: paddingRight,
        bottom = bottom?.let { base[3] + it } ?: paddingBottom,
    )
}

fun View.ensureBasePadding(): IntArray {
    val key = R.id.tag_base_padding
    val base = getTag(key) as? IntArray
    if (base != null) return base
    val arr = intArrayOf(paddingLeft, paddingTop, paddingRight, paddingBottom)
    setTag(key, arr)
    return arr
}


fun Resources.getColoredDrawableWithColor(drawableId: Int, color: Int, alpha: Int = 255): Drawable {
    val drawable = getDrawable(drawableId)
    drawable.mutate().applyColorFilter(color)
    drawable.mutate().alpha = alpha
    return drawable
}


fun View.ensureBaseMargin(): IntArray {
    val key = R.id.tag_base_margin
    val base = getTag(key) as? IntArray
    if (base != null) return base
    val arr = intArrayOf(marginLeft, marginTop, marginRight, marginBottom)
    setTag(key, arr)
    return arr
}

fun View.updateMarginWithBase(
    @Px left: Int? = null,
    @Px top: Int? = null,
    @Px right: Int? = null,
    @Px bottom: Int? = null,
) {
    val base = ensureBaseMargin()
    try {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = left?.let { base[0] + it } ?: leftMargin
            topMargin = top?.let { base[1] + it } ?: topMargin
            rightMargin = right?.let { base[2] + it } ?: rightMargin
            bottomMargin = bottom?.let { base[3] + it } ?: bottomMargin
        }
    } catch (_: ClassCastException) {
    }
}

val TextView.value: String get() = text.toString().trim()
