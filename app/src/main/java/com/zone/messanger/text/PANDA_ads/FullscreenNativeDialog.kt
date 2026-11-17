package com.zone.messanger.text.PANDA_ads

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.databinding.DialogFullscreenNativeBinding
import com.zone.messanger.text.Data.Smartly_Extension.config

class FullscreenNativeDialog(
    activity: Activity,
    private val onResult: (success: Boolean) -> Unit,
) : Dialog(activity, R.style.Dialog_EdgeToEdge2) {

    private var binding: DialogFullscreenNativeBinding =
        DialogFullscreenNativeBinding.inflate(layoutInflater)
    private var isAdShown = false


    init {
        setContentView(binding.root)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        GAds.showNative(
            context,
            container = binding.nativeContainer,
            nativeType = NativeType.FULL_SCREEN_NATIVE,
            adId = adsData().Native_Interstitial,
            uniqueKey = "FullScreenNative",
        ) { success ->
            if (!success) {
                isAdShown = false
                onResult(false)
                dismiss()
            } else {
                isAdShown = true
            }
        }
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }


            setNavigationBarColor(false)

            when {
                activity.config.isDarkModeEnabled -> {
                    setNavigationBarColor(true)
                }

                else -> {
                    setNavigationBarColor(false)
                }
            }
        }
    }

    private fun setNavigationBarColor(isDark: Boolean) {

        window.apply {
            this!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val insetsController = WindowCompat.getInsetsController(this, decorView)
            insetsController.isAppearanceLightNavigationBars = !isDark
            insetsController.isAppearanceLightStatusBars = !isDark
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnDismissListener {
            if (isAdShown) {
                onResult(true)
            }
        }
    }
}