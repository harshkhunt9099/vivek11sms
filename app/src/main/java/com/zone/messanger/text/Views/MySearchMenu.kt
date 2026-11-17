package com.zone.messanger.text.Views

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.appbar.MaterialToolbar
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_Activity.Spidy_BaseSimpleActivity
import com.zone.messanger.text.PANDA_Activity.Spidy_LauncherSettingsActivity
import com.zone.messanger.text.databinding.MenuSearchBinding
import com.zone.messanger.text.Data.Smartly_Extension.adjustAlpha
import com.zone.messanger.text.Data.Smartly_Extension.getContrastColor
import com.zone.messanger.text.Data.Smartly_Extension.getProperBackgroundColor
import com.zone.messanger.text.Data.Smartly_Extension.getProperPrimaryColor
import com.zone.messanger.text.Data.Smartly_Extension.hideKeyboard
import com.zone.messanger.text.Data.Smartly_Extension.onTextChangeListener
import com.zone.messanger.text.Data.Smartly_Extension.showKeyboard
import com.zone.messanger.text.Helper.LOWER_ALPHA
import com.zone.messanger.text.Helper.MEDIUM_ALPHA
import com.zone.messanger.text.Data.Smartly_Extension.applyColorFilter

open class MySearchMenu(context: Context, attrs: AttributeSet) : MyAppBarLayout(context, attrs) {
    var isSearchOpen = false
    var useArrowIcon = false
    var onSearchOpenListener: (() -> Unit)? = null
    var onSearchClosedListener: (() -> Unit)? = null
    var onSearchTextChangedListener: ((text: String) -> Unit)? = null
    var onNavigateBackClickListener: (() -> Unit)? = null

    val binding = MenuSearchBinding.inflate(LayoutInflater.from(context), this)

    override val toolbar: MaterialToolbar?
        get() = binding.topToolbar

    fun setupMenu() {
        binding.ivSetting.setOnClickListener {
            context.startActivity(Intent(context, Spidy_LauncherSettingsActivity::class.java))

            closeSearch()
        }

        binding.topToolbarSearchIcon.setOnClickListener {
            if (isSearchOpen) {
                closeSearch()
            } else if (useArrowIcon && onNavigateBackClickListener != null) {
                onNavigateBackClickListener!!()
            } else {
                binding.topToolbarSearch.requestFocus()
                (context as? Activity)?.showKeyboard(binding.topToolbarSearch)
            }
        }

        post {
            binding.topToolbarSearch.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    openSearch()
                }
            }
        }

        binding.topToolbarSearch.onTextChangeListener { text ->
            onSearchTextChangedListener?.invoke(text)
        }
    }


    private fun openSearch() {
        isSearchOpen = true
        onSearchOpenListener?.invoke()
        binding.topToolbarSearchIcon.setImageResource(R.drawable.lc_ic_arrow_left_vector)
    }

    fun closeSearch() {
        isSearchOpen = false
        onSearchClosedListener?.invoke()
        binding.topToolbarSearch.setText("")
        if (!useArrowIcon) {
            binding.topToolbarSearchIcon.setImageResource(R.drawable.lc_ic_search)
        }
        (context as? Activity)?.hideKeyboard()
    }

    fun getCurrentQuery() = binding.topToolbarSearch.text.toString()

    @Suppress("unused", "EmptyFunctionBlock")
    @Deprecated("This feature is broken for now.")
    fun toggleHideOnScroll(hideOnScroll: Boolean) {}

    fun updateColors() {
        val backgroundColor = context.getProperBackgroundColor()
        val contrastColor = backgroundColor.getContrastColor()

        setBackgroundColor(backgroundColor)
        binding.topToolbarSearchIcon.applyColorFilter(contrastColor)
        binding.toolbarContainer.background?.applyColorFilter(
            color = context.getProperPrimaryColor().adjustAlpha(LOWER_ALPHA)
        )
        binding.topToolbarSearch.setTextColor(contrastColor)
        binding.topToolbarSearch.setHintTextColor(contrastColor.adjustAlpha(MEDIUM_ALPHA))
        (context as? Spidy_BaseSimpleActivity)?.updateTopBarColors(this, backgroundColor)
    }
}