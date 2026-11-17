package com.zone.messanger.text.PANDA_Activity

import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.databinding.ActivitySetDefaultLauncherBinding
import com.zone.messanger.text.PANDA_ads.AdCycleController
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.isDefaultLauncher
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_text.activity.DefaultSmsActivity

class SetDefaultLauncherActivity : AppCompatActivity() {
    lateinit var binding: ActivitySetDefaultLauncherBinding

    private val launcherRoleResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (isDefaultLauncher()) {
                navigateToNextScreen()
            } else {
                if (adsData().Cancel_defult_on_off) {
                    binding.btnCancel.visibility = android.view.View.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            if (isDefaultLauncher()) {
                startActivity(Intent(this, MainLauncherActivity::class.java))
                finish()

                return
            }

            binding = ActivitySetDefaultLauncherBinding.inflate(layoutInflater)

            if (adsData().defultscreen_privacyshow_on_off) {
                binding.txtPrivacy.visibility = android.view.View.VISIBLE
            }else{
                binding.txtPrivacy.visibility = android.view.View.GONE
            }

            setContentView(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            AdCycleController.reset()



            val isDarkModeEnabled = config.isDarkModeEnabled

            if (isDarkModeEnabled) {
                binding.lottieLauncher.apply {
                    setAnimation(R.raw.lottie_default_launcher_dark)
                    imageAssetsFolder = "lottie_default_launcher_dark"
                    repeatCount = LottieDrawable.INFINITE
                    playAnimation()
                }

            } else {
                binding.lottieLauncher.apply {
                    setAnimation(R.raw.lottie_default_launcher_light)
                    imageAssetsFolder = "lottie_default_launcher_light"
                    repeatCount = LottieDrawable.INFINITE
                    playAnimation()
                }
            }

            setupPrivacySpan()
            initClick()


            binding.btnCancel.setOnClickListener {
                AdsManager.showInterstitialLauncher(this, false) {
                    if (adsData().INTRO_SCREEN) {
                        startActivity(
                            Intent(
                                this@SetDefaultLauncherActivity,
                                Spidy_LanguageActivity::class.java
                            )
                        )
                    }else{
                        val repository = SmsRepository(this@SetDefaultLauncherActivity)
                        if (repository.isDefaultSmsApp()) {
                            startActivity(
                                Intent(
                                    this@SetDefaultLauncherActivity,
                                    Spidy_MainActivity::class.java
                                )
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@SetDefaultLauncherActivity,
                                    DefaultSmsActivity::class.java
                                )
                            )
                        }


                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun initClick() {
        try {
            binding.lottieLauncher.setOnClickListener {
                requestHomeRole()
            }

            binding.flSetDefault.setOnClickListener {
                requestHomeRole()
            }

            onBackPressedDispatcher.addCallback(this) {
                showImportantNoticeDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun requestHomeRole() {
        try {
            if (isDefaultLauncher()) {
                navigateToNextScreen()
            } else {
                launchHomeSelector()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun launchHomeSelector() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            val roleManager = getSystemService(RoleManager::class.java)

            if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
            ) {

                binding.btnCancel.visibility = android.view.View.GONE

                launcherRoleResult.launch(
                    roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                )
            }

        } else {
            try {
                binding.btnCancel.visibility = android.view.View.GONE
                startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun navigateToNextScreen() {
            startActivity(
                Intent(
                    this,
                    MainLauncherActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()

    }


    private fun showImportantNoticeDialog() {
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialogTheme)
            .setTitle(R.string.lc_important_notice)
            .setMessage("The Messagesing needs to be set as your default home app to work properly.")
            .setPositiveButton(R.string.lc_setup_now) { dialog, _ ->
                requestHomeRole()
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun setupPrivacySpan() {
        binding.txtPrivacy.setOnClickListener {
            openPrivacyPolicy()
        }
    }

    private fun openPrivacyPolicy() {
        if (!AdsManager.isNetworkAvailable(this)) {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://smspandmessages.blogspot.com/".toUri()
                )
            )
            AdsManager.isFromPermission = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}