package com.zone.messanger.text.Views

import com.zone.messanger.text.R

data class LanguageModel(
    val name: String,
    var isSelected: Boolean
) {

    // Flag icon shown next to the language name.
    val flagRes: Int
        get() = when (name) {
            "English" -> R.drawable.ic_flag_uk
            "Hindi" -> R.drawable.ic_flag_india
            "Chinese" -> R.drawable.ic_flag_china
            "Japanese" -> R.drawable.ic_flag_japan
            "German" -> R.drawable.ic_flag_germany
            "Spanish" -> R.drawable.ic_flag_spain
            "French" -> R.drawable.ic_flag_france
            "Arabic" -> R.drawable.ic_flag_saudi
            "Russian" -> R.drawable.ic_flag_russia
            "Portuguese" -> R.drawable.ic_flag_portugal
            "Korean" -> R.drawable.ic_flag_korea
            "Italian" -> R.drawable.ic_flag_italy
            else -> R.drawable.ic_flag_india
        }
}
