package com.launcher.launcherpackage.Codebizz_activity

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.launcher.launcherpackage.R
import com.launcher.launcherpackage.databinding.PmActivityDisableConfirmationBinding
import com.launcher.launcherpackage.databinding.PmDialogWatchAdBinding
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorAdsManager
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorPrefManager
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_RewardedAdLoader
import com.launcher.launcherpackage.Codebizz_helper.dpToPx

class Codebizz_PackageMonitorDisableConfirmationActivity : AppCompatActivity() {
    private var packageMonitorType: Int = 1 // 1=Install, 2=Uninstall


    private val binding: PmActivityDisableConfirmationBinding by lazy {
        PmActivityDisableConfirmationBinding.inflate(
            layoutInflater
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        packageMonitorType = intent.getIntExtra("packageMonitorType", 1)

        loadAds()

        setupTitle()
        updateSwitchOnColor(Codebizz_PackageMonitorPrefManager.tabLayoutColor.toColorInt())

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.imgBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }


        binding.switchStatus.isChecked = when (packageMonitorType) {
            1 -> Codebizz_PackageMonitorPrefManager.installScanEnabled
            else -> Codebizz_PackageMonitorPrefManager.uninstallScanEnabled
        }


        binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                when (packageMonitorType) {
                    1 -> {
                        Codebizz_PackageMonitorPrefManager.installScanEnabled = true
                    }

                    else -> {
                        Codebizz_PackageMonitorPrefManager.uninstallScanEnabled = true
                    }
                }
            } else {
                showWatchAdToDisableDialog(packageMonitorType)
            }
        }


    }

    private fun loadAds() = with(binding) {
        Codebizz_PackageMonitorAdsManager.loadAndShowNative(
            this@Codebizz_PackageMonitorDisableConfirmationActivity,
            nativeContainer
        )
    }

    private fun setupTitle() {
        when (packageMonitorType) {
            1 -> {
                binding.txtTitle.text = getString(R.string.pm_title_package_install_scan)
                binding.tvSwitchTitle.text = getString(R.string.pm_title_package_install_scan)
            }

            2 -> {
                binding.txtTitle.text = getString(R.string.pm_title_package_uninstall_scan)
                binding.tvSwitchTitle.text = getString(R.string.pm_title_package_uninstall_scan)
            }

            else -> {
                binding.txtTitle.text = getString(R.string.pm_title_package_install_scan)
                binding.tvSwitchTitle.text = getString(R.string.pm_title_package_install_scan)
            }
        }
    }

    private fun showWatchAdToDisableDialog(packageMonitorType: Int) {
        val dialog = Dialog(this)
        val dialogBinding = PmDialogWatchAdBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnWatchAd.backgroundTintList =
            ColorStateList.valueOf(Codebizz_PackageMonitorPrefManager.tabLayoutColor.toColorInt())


        var isDialogDismissByUserAction = false

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setDimAmount(0.8f)
            setGravity(Gravity.CENTER)
        }

        if (packageMonitorType == 1) {
            dialogBinding.tvTitle.text = getString(R.string.pm_disable_install_scan)
            dialogBinding.tvDesc.text =
                getString(R.string.pm_watch_a_video_ad_to_disable_install_scan_alerts)
        } else {
            dialogBinding.tvTitle.text = getString(R.string.pm_disable_uninstall_scan)
            dialogBinding.tvDesc.text =
                getString(R.string.pm_watch_a_video_ad_to_disable_uninstall_scan_alerts)
        }

        dialogBinding.btnWatchAd.setOnClickListener {
            if (!Codebizz_PackageMonitorAdsManager.isNetworkAvailable(this)) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isDialogDismissByUserAction=true
            dialog.dismiss()

            Codebizz_RewardedAdLoader.loadAndShowRewardAd(
                this,
                Codebizz_PackageMonitorPrefManager.getPackageMonitorAdsResponse().PackageMonitor_Rewarded_Video,
                object : Codebizz_RewardedAdLoader.OnRewardAdAction {
                    override fun onRewarded() {
                        if (packageMonitorType == 1) {
                            Codebizz_PackageMonitorPrefManager.installScanEnabled = false
                        } else {
                            Codebizz_PackageMonitorPrefManager.uninstallScanEnabled = false
                        }
                    }

                    override fun onAdShowing() {

                    }

                    override fun onDismiss() {

                    }

                    override fun onRewardNotEarned() {
                        binding.switchStatus.isChecked = false

                        Toast.makeText(
                            this@Codebizz_PackageMonitorDisableConfirmationActivity,
                            "Please try again after some time.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }

        dialog.setOnDismissListener {
            if (!isDialogDismissByUserAction){
                binding.switchStatus.isChecked = true
            }
        }

        dialog.show()
    }

    private fun updateSwitchOnColor(color: Int) {

        try {
            // ----- THUMB -----
            val thumb = binding.switchStatus.thumbDrawable?.mutate()

            if (thumb is StateListDrawable) {
                thumb.state = intArrayOf(android.R.attr.state_checked)
                val current = thumb.current

                if (current is GradientDrawable) {
                    current.setStroke(3.dpToPx(this), color)
                }
            }

            // ----- TRACK -----
            val track = binding.switchStatus.trackDrawable?.mutate()

            if (track is StateListDrawable) {
                track.state = intArrayOf(android.R.attr.state_checked)
                val current = track.current

                if (current is GradientDrawable) {
                    current.setColor(color)
                }
            }

            binding.switchStatus.invalidate()
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun attachBaseContext(newBase: Context?) {
        val config = newBase!!.resources.configuration
        config.fontScale = 1.0f
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

}