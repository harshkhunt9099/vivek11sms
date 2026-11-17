package com.zone.messanger.text.Models

import androidx.annotation.Keep
import com.zone.messanger.text.R

@Keep
enum class GestureType {
    DOUBLE_TAP,
    TAP_HOLD,
    HOME_BUTTON,
    BACK_BUTTON;

    fun titleRes(): Int {
        return when (this) {
            DOUBLE_TAP -> R.string.lc_double_tap
            TAP_HOLD -> R.string.lc_tap_amp_hold
            HOME_BUTTON -> R.string.lc_home_button
            BACK_BUTTON -> R.string.lc_back_button
        }
    }
}
