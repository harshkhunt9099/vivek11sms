package com.launcher.launcherpackage.Codebizz_activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.launcher.launcherpackage.R
import com.launcher.launcherpackage.databinding.PmActivityInstallUninstallBinding
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_HomePressWatcher
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorAdsManager
import com.launcher.launcherpackage.Codebizz_helper.getAppInfo
import com.launcher.launcherpackage.Codebizz_helper.gone
import com.launcher.launcherpackage.Codebizz_helper.setCompoundDrawableColor
import com.launcher.launcherpackage.Codebizz_helper.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Codebizz_InstallUninstallActivity : AppCompatActivity() {
    private val isFromInstall by lazy { intent.getBooleanExtra("isFromInstall", false) }
    private val randomInt by lazy { (50..90).random() }
    private val binding: PmActivityInstallUninstallBinding by lazy {
        PmActivityInstallUninstallBinding.inflate(
            layoutInflater
        )
    }
    private var homeWatcher: Codebizz_HomePressWatcher? = null

    companion object {
        var installUninstallActivity: Codebizz_InstallUninstallActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installUninstallActivity = this
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }


        setupView()
        bindHandlers()
        loadAds()
        handleBackPress()
        setupHomePressWatcher()
    }

    private fun loadAds() = with(binding) {
        Codebizz_PackageMonitorAdsManager.loadAndShowNative(this@Codebizz_InstallUninstallActivity, nativeContainer)
    }

    private fun bindHandlers() {

        binding.ivSetting.setOnClickListener {
            startActivity(
                Intent(
                    this@Codebizz_InstallUninstallActivity,
                    Codebizz_PackageMonitorSettingActivity::class.java
                )
            )
        }

        binding.btnOpenDone.setOnClickListener {
            if (isFromInstall) {
                val packageName = intent.getStringExtra("packageName")
                launchApp(packageName)
                finish()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setupView() {
        binding.btnOpenDone.setText(if (isFromInstall) R.string.pm_open else R.string.pm_done)
        binding.tvHeading.setText(if (isFromInstall) R.string.pm_installing else R.string.pm_uninstalling)
        binding.tvUninstallDesc.text =
            getString(R.string.pm_storage_optimized_90mb_freed, randomInt)
        binding.clInstallView.isVisible = isFromInstall
        binding.clUninstallView.isVisible = !isFromInstall
        if (isFromInstall) {
            val appInfo = getAppInfo(this, intent.getStringExtra("packageName") ?: "")
            Glide.with(this)
                .load(appInfo.second)
                .into(binding.ivAppLogo)
            binding.tvAppName.text = appInfo.first
            binding.tvAppSize.text = "Size: ${appInfo.third}"
        }
        lifecycleScope.launch {
            delay(5000)
            onProgressCompleteView()
        }
    }

    private fun onProgressCompleteView() = with(binding) {
        groupProgress.gone()
        groupType.visible()
        btnOpenDone.visible()
        binding.tvTypeDescription.setTextColor(
            getColor(if (isFromInstall) R.color.pm_text_green else R.color.pm_text_grey)
        )
        binding.tvTypeDescription.setCompoundDrawableColor(getColor(if (isFromInstall) R.color.pm_text_green else R.color.pm_text_grey))
        binding.tvUninstallTitle.setText(R.string.pm_optimization_speed)
        binding.tvUninstallDesc.setText(R.string.pm_keeping_things_running_smoothly)
        tvTypeTitle.setText(if (isFromInstall) R.string.pm_app_installed else R.string.pm_app_uninstalled)
        tvTypeDescription.text = getString(
            if (isFromInstall) R.string.pm_this_app_secure_to_use else R.string.pm_storage_optimized_90mb_freed,
            randomInt
        )
    }


    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this) {
            if (binding.btnOpenDone.isVisible) finish()
        }
    }

    private fun launchApp(packageName: String?) {
        Log.e("TAG_PACKAGE", "launchApp: $packageName")
        if (packageName == null) return

        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                Log.e("Launcher", "No launch intent for $packageName")
            }
        } catch (e: Exception) {
            Log.e("Launcher", "Permission denied launching $packageName", e)
            // optionally show toast or ignore silently
        }

//        packageManager.getLaunchIntentForPackage(packageName)?.let {
//            startActivity(it)
//        }
    }

    private fun setupHomePressWatcher() {
        homeWatcher = Codebizz_HomePressWatcher(this)
        homeWatcher?.setOnHomePressedListener {
            Log.d("HomeWatcher", "Home button pressed")
            finishSafely()
        }
        homeWatcher?.startWatch()
    }

    private fun finishSafely() {

        if (!isFinishing && !isDestroyed) {
            try {
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    runOnUiThread {
                        if (!isFinishing && !isDestroyed) {
                            finish()
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        homeWatcher?.stopWatch()
    }

    override fun attachBaseContext(newBase: Context?) {
        val config = newBase!!.resources.configuration
        config.fontScale = 1.0f // Always use normal font scale
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

}