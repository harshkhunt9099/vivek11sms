package com.zone.messanger.text.PANDA_Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.AppNavigationManager
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.databinding.ActivityLauncherSettingsBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.launcher.launcherpackage.Codebizz_activity.Codebizz_PackageMonitorSettingActivity


class Spidy_LauncherSettingsActivity : Messanger__BaseActivity() {

    private lateinit var binding: ActivityLauncherSettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLauncherSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdsManager.showNative(
            this,
            container = binding.nativeContainer,
            nativeType = NativeType.LARGE_NATIVE,
            adId = adsData().Native_All_Center_Side_id,
            uniqueKey = this::class.java.simpleName,
        )

        initClick()
    }

    private fun initClick() {
        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@Spidy_LauncherSettingsActivity, false) {
                finish()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.llHomeScreenSetting.setOnClickListener {
            startActivity(Intent(this, Spidy_HomeScreenSettingActivity::class.java))
        }

        binding.llAppDrawerSetting.setOnClickListener {
            startActivity(Intent(this, Spidy_AppDrawerSettingActivity::class.java))
        }

        binding.llSearchSetting.setOnClickListener {
            startActivity(Intent(this, WebSearchSettingActivity::class.java))
        }


        binding.llGestureSetting.setOnClickListener {
            startActivity(Intent(this, Spidy_GesturesSettingActivity::class.java))
        }

        binding.llPackageMonitorSetting.setOnClickListener {
            startActivity(Intent(this, Codebizz_PackageMonitorSettingActivity::class.java))
        }

        binding.switchNavigationStatus.isChecked = config.showNavigationBar

        binding.switchNavigationStatus.setOnCheckedChangeListener { _, isChecked ->
            config.showNavigationBar = isChecked
            config.navigationUserOverride = true

            AppNavigationManager.handleAppNavigation(
                this,
                adsData().Ads_App_Navigation_Count,
                adsData().Ads_App_Navigation
            )
        }

        binding.switchDarkModeStatus.isChecked = config.isDarkModeEnabled

        binding.switchDarkModeStatus.setOnCheckedChangeListener { _, isChecked ->
            config.isDarkModeEnabled = isChecked

            val modeValue = if (isChecked)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(modeValue)
        }
    }


}