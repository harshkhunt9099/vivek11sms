package com.zone.messanger.text.PANDA_Activity


import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.get
import androidx.core.view.size
import com.zone.messanger.text.R
import com.zone.messanger.text.Data.Smartly_Extension.applyColorFilter
import com.zone.messanger.text.Data.Smartly_Extension.getColoredDrawableWithColor
import com.zone.messanger.text.Data.Smartly_Extension.getContrastColor
import com.zone.messanger.text.Data.Smartly_Extension.getProperBackgroundColor
import com.zone.messanger.text.Data.Smartly_Extension.onApplyWindowInsets
import com.zone.messanger.text.Data.Smartly_Extension.setSystemBarsAppearance
import com.zone.messanger.text.Data.Smartly_Extension.updateMarginWithBase
import com.zone.messanger.text.Data.Smartly_Extension.updatePaddingWithBase
import com.zone.messanger.text.Views.MyAppBarLayout

abstract class Spidy_EdgeToEdgeActivity : Spidy_BaseActivityStarting() {
    open var isSearchBarEnabled = false
    open val padCutout: Boolean
        get() = true

    private var materialScrollColorAnimation: ValueAnimator? = null

    private val contentRoot by lazy { findViewById<View>(android.R.id.content) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)
    }

    override fun onResume() {
        super.onResume()
        window.setSystemBarsAppearance(getProperBackgroundColor())
    }

    fun setupEdgeToEdge(
        padTopSystem: List<View> = emptyList(),
        padBottomSystem: List<View> = emptyList(),
        padBottomImeAndSystem: List<View> = emptyList(),
        moveBottomSystem: List<View> = emptyList(),
        animateIme: Boolean = false,
    ) {
        onApplyWindowInsets { insets ->
            val system = insets.getInsetsIgnoringVisibility(Type.systemBars())
            val imeAndSystem = insets.getInsets(Type.ime() or Type.systemBars())

            padTopSystem.forEach { it.updatePaddingWithBase(top = system.top) }
            padBottomSystem.forEach { it.updatePaddingWithBase(bottom = system.bottom) }
            padBottomImeAndSystem.forEach { it.updatePaddingWithBase(bottom = imeAndSystem.bottom) }
            moveBottomSystem.forEach { it.updateMarginWithBase(bottom = system.bottom) }

            if (padCutout) {
                val cutout = insets.getInsets(Type.displayCutout())
                val sideLeft = maxOf(system.left, cutout.left)
                val sideRight = maxOf(system.right, cutout.right)
                contentRoot.updatePaddingWithBase(left = sideLeft, right = sideRight)
            }

            if (animateIme) {
                ViewCompat.setWindowInsetsAnimationCallback(
                    contentRoot,
                    object :
                        WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                        override fun onProgress(
                            insets: WindowInsetsCompat,
                            runningAnimations: MutableList<WindowInsetsAnimationCompat>,
                        ): WindowInsetsCompat {
                            val bottom = insets.getInsets(Type.systemBars() or Type.ime()).bottom
                            padBottomImeAndSystem.forEach {
                                it.updatePaddingWithBase(bottom = bottom)
                            }
                            return insets
                        }
                    }
                )
            }
        }
    }


    fun updateTopBarColors(topAppBar: MyAppBarLayout, color: Int) {
        val contrastColor = if (isSearchBarEnabled) {
            getProperBackgroundColor().getContrastColor()
        } else {
            color.getContrastColor()
        }

        window.setSystemBarsAppearance(color)
        if (!isSearchBarEnabled) {
            topAppBar.setBackgroundColor(color)
            topAppBar.toolbar?.setBackgroundColor(color)
            topAppBar.toolbar?.setTitleTextColor(contrastColor)
            topAppBar.toolbar?.navigationIcon?.applyColorFilter(contrastColor)
            topAppBar.toolbar?.collapseIcon = resources.getColoredDrawableWithColor(
                drawableId = R.drawable.lc_ic_arrow_left_vector,
                color = contrastColor
            )
        }

        topAppBar.toolbar?.overflowIcon =
            resources.getColoredDrawableWithColor(R.drawable.lc_ic_three_dots_vector, contrastColor)

        val menu = topAppBar.toolbar?.menu ?: return
        for (i in 0 until menu.size) {
            try {
                menu[i].icon?.setTint(contrastColor)
            } catch (ignored: Exception) {
            }
        }
    }

    override fun onDestroy() {
        materialScrollColorAnimation?.cancel()
        materialScrollColorAnimation = null
        super.onDestroy()
    }
}