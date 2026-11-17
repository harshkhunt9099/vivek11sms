package com.launcher.launcherpackage.Codebizz_activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.launcher.launcherpackage.R
import com.launcher.launcherpackage.databinding.PmActivityAdvantagesBinding
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorPrefManager

class Codebizz_PackageMonitorAdvantagesActivity : AppCompatActivity() {
    private var packageMonitorType: Int = 1 // 1=Install, 2=Uninstall

    private val binding: PmActivityAdvantagesBinding by lazy {
        PmActivityAdvantagesBinding.inflate(
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


        setupAdvantages()
        setupButtons()
        setupTitle()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.imgBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun setupAdvantages() {
        val advantages = getAdvantagesForPackageMonitorType(packageMonitorType)
        binding.llAdvantages.removeAllViews()

        advantages.forEach { advantage ->
            val advantageView = LayoutInflater.from(this)
                .inflate(R.layout.pm_item_advantage, binding.llAdvantages, false)

            val tvTitle = advantageView.findViewById<TextView>(R.id.tvAdvantageTitle)
            val tvDescription = advantageView.findViewById<TextView>(R.id.tvAdvantageDescription)

            tvTitle.text = advantage.title
            tvDescription.text = advantage.description

            binding.llAdvantages.addView(advantageView)
        }
    }


    private fun getAdvantagesForPackageMonitorType(packageMonitorType: Int): List<Advantage> {
        return when (packageMonitorType) {
            1 -> { // Install
                listOf(
                    Advantage(
                        getString(R.string.pm_advantage_app_install_alerts),
                        getString(R.string.pm_advantage_app_install_alerts_desc)
                    ),
                    Advantage(
                        getString(R.string.pm_advantage_app_junk_cleaner),
                        getString(R.string.pm_advantage_app_junk_cleaner_desc)
                    ),
                    Advantage(
                        getString(R.string.pm_advantage_app_smart_device_care),
                        getString(R.string.pm_advantage_app_smart_device_care_desc)
                    )
                )
            }

            2 -> { // Uninstall
                listOf(
                    Advantage(
                        getString(R.string.pm_advantage_uninstall_alert_system),
                        getString(R.string.pm_advantage_uninstall_alert_system_desc)
                    ),
                    Advantage(
                        getString(R.string.pm_advantage_free_storage_space),
                        getString(R.string.pm_advantage_free_storage_space_desc)
                    ),
                    Advantage(
                        getString(R.string.pm_advantage_speed_up_phone),
                        getString(R.string.pm_advantage_speed_up_phone_desc)
                    )
                )
            }

            else -> emptyList()
        }
    }

    private fun setupButtons() {
        binding.btnKeepOn.backgroundTintList =
            ColorStateList.valueOf(Codebizz_PackageMonitorPrefManager.tabLayoutColor.toColorInt())

        binding.btnKeepOn.setOnClickListener {
            if (packageMonitorType == 1) {
                Codebizz_PackageMonitorPrefManager.installScanEnabled = true
            } else {
                Codebizz_PackageMonitorPrefManager.uninstallScanEnabled = true
            }
            finish()
        }

        binding.btnTurnOff.setOnClickListener {
            if (packageMonitorType == 1 && !Codebizz_PackageMonitorPrefManager.installScanEnabled) {
                finish()
            } else if (packageMonitorType == 2 && !Codebizz_PackageMonitorPrefManager.uninstallScanEnabled) {
                finish()
            } else {
                val intent = Intent(this, Codebizz_PackageMonitorDisableConfirmationActivity::class.java)
                intent.putExtra("packageMonitorType", packageMonitorType)
                startActivity(intent)
            }
        }
    }


    private data class Advantage(
        val title: String,
        val description: String,
    )

    override fun attachBaseContext(newBase: Context?) {
        val config = newBase!!.resources.configuration
        config.fontScale = 1.0f // Always use normal font scale
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    private fun setupTitle() {
        val title = when (packageMonitorType) {
            1 -> getString(R.string.pm_title_package_install_scan)
            2 -> getString(R.string.pm_title_package_uninstall_scan)
            else -> getString(R.string.pm_title_package_install_scan)
        }
        binding.txtTitle.text = title
    }

}