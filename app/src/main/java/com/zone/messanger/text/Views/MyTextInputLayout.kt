package com.zone.messanger.text.Views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.zone.messanger.text.Data.Smartly_Extension.adjustAlpha
import com.zone.messanger.text.Data.Smartly_Extension.value
import com.zone.messanger.text.Helper.HIGHER_ALPHA
import com.zone.messanger.text.Helper.MEDIUM_ALPHA

open class MyTextInputLayout : TextInputLayout {
    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    fun setColors(textColor: Int, accentColor: Int, backgroundColor: Int) {
        try {
            editText!!.setTextColor(Color.WHITE)
            editText!!.backgroundTintList = ColorStateList.valueOf(accentColor)

            val hintColor = if (editText!!.value.isEmpty()) textColor.adjustAlpha(HIGHER_ALPHA) else textColor
            val defaultTextColor = TextInputLayout::class.java.getDeclaredField("defaultHintTextColor")
            defaultTextColor.isAccessible = true
            defaultTextColor.set(this, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(hintColor)))

            val focusedTextColor = TextInputLayout::class.java.getDeclaredField("focusedTextColor")
            focusedTextColor.isAccessible = true
            focusedTextColor.set(this, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(accentColor)))

            val defaultHintTextColor = textColor.adjustAlpha(MEDIUM_ALPHA)
            val boxColorState = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_active),
                    intArrayOf(android.R.attr.state_focused)
                ),
                intArrayOf(
                    defaultHintTextColor,
                    accentColor
                )
            )

            setEndIconTintList(ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(hintColor)))
            setBoxStrokeColorStateList(boxColorState)
            defaultTextColor.set(this, ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(defaultHintTextColor)))
            setHelperTextColor(ColorStateList.valueOf(textColor))
        } catch (e: Exception) {
        }
    }
}
