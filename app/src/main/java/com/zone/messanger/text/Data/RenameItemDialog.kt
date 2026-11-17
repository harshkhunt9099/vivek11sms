package com.zone.messanger.text.Data

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.ShortcutInfo
import com.zone.messanger.text.R
import com.zone.messanger.text.databinding.DialogRenameItemBinding
import com.zone.messanger.text.Data.Smartly_Extension.getAlertDialogBuilder
import com.zone.messanger.text.Data.Smartly_Extension.homeScreenGridItemsDB
import com.zone.messanger.text.Data.Smartly_Extension.setupDialogStuff
import com.zone.messanger.text.Data.Smartly_Extension.showKeyboard
import com.zone.messanger.text.Data.Smartly_Extension.toast
import com.zone.messanger.text.Data.Smartly_Extension.value
import com.zone.messanger.text.Helper.ensureBackgroundThread
import com.zone.messanger.text.Models.HomeScreenGridItem

class RenameItemDialog(val activity: Activity, val item: HomeScreenGridItem, val callback: () -> Unit) {

    init {
        val binding = DialogRenameItemBinding.inflate(activity.layoutInflater)
        val view = binding.root
        binding.renameItemEdittext.setText(item.title)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.lc_ok, null)
            .setNegativeButton(R.string.lc_cancel, null)
            .apply {
                activity.setupDialogStuff(view, this, R.string.lc_rename) { alertDialog ->
                    alertDialog.showKeyboard(binding.renameItemEdittext)
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val newTitle = binding.renameItemEdittext.value
                        if (newTitle.isNotEmpty()) {
                            ensureBackgroundThread {
                                val result = activity.homeScreenGridItemsDB.updateItemTitle(newTitle, item.id!!)
                                if (result == 1) {
                                    callback()
                                    alertDialog.dismiss()
                                } else {
                                    activity.toast(R.string.lc_unknown_error_occurred)
                                }
                            }
                        } else {
                            activity.toast(R.string.lc_value_cannot_be_empty)
                        }
                    }
                }
            }
    }
}

fun ShortcutInfo?.getLabel() = this?.longLabel?.toString().ifNullOrEmpty { this?.shortLabel?.toString() } ?: ""

private fun String?.ifNullOrEmpty(block: () -> String?) = this?.ifEmpty { block() } ?: block()
