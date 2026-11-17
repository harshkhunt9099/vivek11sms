package com.launcher.launcherpackage.Codebizz_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.launcher.launcherpackage.R
import com.launcher.launcherpackage.databinding.PmActivitySettingBinding
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorAdsManager
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorPrefManager

class Codebizz_PackageMonitorSettingActivity : AppCompatActivity() {
    private val binding: PmActivitySettingBinding by lazy {
        PmActivitySettingBinding.inflate(
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

        loadAds()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.imgBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.cvInstallScan.setOnClickListener {
            val intent = Intent(this, Codebizz_PackageMonitorAdvantagesActivity::class.java)
            intent.putExtra("packageMonitorType", 1)
            startActivity(intent)
        }

        binding.cvUninstallScan.setOnClickListener {
            val intent = Intent(this, Codebizz_PackageMonitorAdvantagesActivity::class.java)
            intent.putExtra("packageMonitorType", 2)
            startActivity(intent)
        }
    }

    private fun loadAds() = with(binding) {
        Codebizz_PackageMonitorAdsManager.loadAndShowNative(
            this@Codebizz_PackageMonitorSettingActivity,
            nativeContainer
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        val config = newBase!!.resources.configuration
        config.fontScale = 1.0f // Always use normal font scale
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        if (Codebizz_PackageMonitorPrefManager.installScanEnabled) {
            binding.tvInstallStatus.text = getString(R.string.pm_status_on)
            binding.tvInstallStatus.setTextColor("#979595".toColorInt())
        } else {
            binding.tvInstallStatus.text = getString(R.string.pm_status_off)
            binding.tvInstallStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        }

        if (Codebizz_PackageMonitorPrefManager.uninstallScanEnabled) {
            binding.tvUninstallStatus.text = getString(R.string.pm_status_on)
            binding.tvUninstallStatus.setTextColor("#979595".toColorInt())
        } else {
            binding.tvUninstallStatus.text = getString(R.string.pm_status_off)
            binding.tvUninstallStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }
}