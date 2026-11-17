package com.zone.messanger.text.PANDA_Activity

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.databinding.ActivityGesturesSettingBinding
import com.zone.messanger.text.databinding.DialogGestureSettingBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Models.GestureAction
import com.zone.messanger.text.Models.GestureType

class Spidy_GesturesSettingActivity : Messanger__BaseActivity() {
    lateinit var binding: ActivityGesturesSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGesturesSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdsManager.showNative(
            this,
            container = binding.nativeContainer,
            nativeType = NativeType.LARGE_NATIVE_TOP_MEDIAVIEW,
            adId = adsData().Native_All_Center_Side_id,
            uniqueKey = this::class.java.simpleName,
        )

        initialize()
        initClick()
    }

    private fun initClick() {
        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@Spidy_GesturesSettingActivity, false) {
                finish()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.llDoubleTap.setOnClickListener {
            showGestureSettingsDialog(GestureType.DOUBLE_TAP)
        }
        binding.llTapHold.setOnClickListener {
            showGestureSettingsDialog(GestureType.TAP_HOLD)
        }
        binding.llHomeButton.setOnClickListener {
            showGestureSettingsDialog(GestureType.HOME_BUTTON)
        }
        binding.llBackButton.setOnClickListener {
            showGestureSettingsDialog(GestureType.BACK_BUTTON)
        }
    }


    private fun showGestureSettingsDialog(type: GestureType) {

        val dialog = Dialog(this)
        val dialogBinding = DialogGestureSettingBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setDimAmount(0.8f)
        }

        dialogBinding.tvTitle.setText(type.titleRes())

        val savedAction = config.getGestureAction(type)
        selectRadio(dialogBinding, savedAction)

        dialogBinding.btnSelect.setOnClickListener {
            val selectedAction = getSelectedAction(dialogBinding)
            config.setGestureAction(type, selectedAction)
            updateGestureUI(type, selectedAction)
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun selectRadio(
        binding: DialogGestureSettingBinding,
        action: GestureAction,
    ) {
        when (action) {
            GestureAction.NONE -> binding.radioDoNothing.isChecked = true
            GestureAction.OPEN_APP_DRAWER -> binding.radioOpenAppDrawer.isChecked = true
            GestureAction.OPEN_LAUNCHER_SETTING -> binding.radioOpenLauncherSetting.isChecked = true
            GestureAction.OPEN_HOME_POPUP -> binding.radioOpenHomeScreenPopup.isChecked = true
            GestureAction.OPEN_GLOBAL_SEARCH -> binding.radioOpenGlobalSearch.isChecked = true
        }
    }

    private fun getSelectedAction(binding: DialogGestureSettingBinding): GestureAction {
        return when {
            binding.radioDoNothing.isChecked -> GestureAction.NONE
            binding.radioOpenAppDrawer.isChecked -> GestureAction.OPEN_APP_DRAWER
            binding.radioOpenLauncherSetting.isChecked -> GestureAction.OPEN_LAUNCHER_SETTING
            binding.radioOpenHomeScreenPopup.isChecked -> GestureAction.OPEN_HOME_POPUP
            binding.radioOpenGlobalSearch.isChecked -> GestureAction.OPEN_GLOBAL_SEARCH
            else -> GestureAction.NONE
        }
    }

    private fun updateGestureUI(type: GestureType, action: GestureAction) {
        val label = action.displayName()

        when (type) {
            GestureType.DOUBLE_TAP -> binding.tvDoubleTapAction.text = label
            GestureType.TAP_HOLD -> binding.tvTapAndHoldAction.text = label
            GestureType.HOME_BUTTON -> binding.tvHomeButtonAction.text = label
            GestureType.BACK_BUTTON -> binding.tvBackButtonAction.text = label
        }
    }


    private fun initialize() {
        updateGestureUI(
            GestureType.DOUBLE_TAP,
            config.getGestureAction(GestureType.DOUBLE_TAP)
        )

        updateGestureUI(
            GestureType.TAP_HOLD,
            config.getGestureAction(GestureType.TAP_HOLD)
        )

        updateGestureUI(
            GestureType.HOME_BUTTON,
            config.getGestureAction(GestureType.HOME_BUTTON)
        )

        updateGestureUI(
            GestureType.BACK_BUTTON,
            config.getGestureAction(GestureType.BACK_BUTTON)
        )
    }

    fun GestureAction.displayName(): String {
        return when (this) {
            GestureAction.NONE -> resources.getString(R.string.lc_do_nothing)
            GestureAction.OPEN_APP_DRAWER -> resources.getString(R.string.lc_open_app_drawer)
            GestureAction.OPEN_LAUNCHER_SETTING -> resources.getString(R.string.lc_open_launcher_setting)
            GestureAction.OPEN_HOME_POPUP -> resources.getString(R.string.lc_open_home_screen_pop_up)
            GestureAction.OPEN_GLOBAL_SEARCH -> resources.getString(R.string.lc_open_global_search)
        }
    }

}