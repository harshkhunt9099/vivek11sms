package com.zone.messanger.text.PANDA_Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextClock
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.databinding.ActivitySetWallpaperBinding
import com.zone.messanger.text.databinding.DialogApplyingWallpaperBinding
import com.zone.messanger.text.databinding.DialogSetWallpaperBinding
import com.zone.messanger.text.databinding.DialogUnlockWallpaperBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Helper.WallpaperApplier
import com.zone.messanger.text.Models.WallpaperType
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorAdsManager
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_RewardedAdLoader
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SetWallpaperActivity : Spidy_BaseActivityStarting() {

    private lateinit var binding: ActivitySetWallpaperBinding

    private var isHomePreviewVisible = false
    private var applyingDialog: Dialog? = null
    private val wallpaperUrl: String? by lazy { intent.getStringExtra(WALLPAPER_URL) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.isNavigationBarContrastEnforced = false

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        binding = ActivitySetWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialize()
        initClick()
        setupTouchToggle()
    }

    private fun initialize() {
        updateTime()

        Glide.with(binding.ivWallpaper).clear(binding.ivWallpaper)
        binding.ivWallpaper.setImageDrawable(null)

        binding.pbProgress.isVisible = true

        Glide.with(binding.ivWallpaper)
            .load(wallpaperUrl)
            .placeholder(R.color.lc_black)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .onlyRetrieveFromCache(false)
            .disallowHardwareConfig()
            .dontAnimate() // helps prevent bitmap reuse issues
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.btnApply.alpha = 0.5f
                    binding.btnApply.isEnabled = false
                    binding.pbProgress.isVisible = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.btnApply.alpha = 1.0f
                    binding.btnApply.isEnabled = true
                    binding.pbProgress.isVisible = false
                    return false
                }
            })
            .into(binding.ivWallpaper)
    }

    private fun initClick() {
        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.btnApply.setOnClickListener {
            if (config.remainingWallpaperCount > 0) {
                showWallpaperSetDialog()
            } else {
                showUnlockWallpaperDialog()
            }
        }
    }


    private fun applyWallpaper(type: WallpaperType) {
        val wallpaperUrl = wallpaperUrl ?: return

        WallpaperApplier.apply(
            context = this,
            imageUrl = wallpaperUrl,
            type = type
        ) { success ->

            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread

                binding.root.postDelayed({

                    safelyDismissApplyingDialog()

                    Toast.makeText(
                        this,
                        if (success) {
                            config.remainingWallpaperCount = config.remainingWallpaperCount - 1
                            getString(R.string.lc_wallpaper_applied_successfully)
                        } else getString(R.string.lc_failed_to_apply_wallpaper),
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                }, 1500)
            }
        }
    }

    companion object {
        const val WALLPAPER_URL = "wallpaper_url"
    }

    private fun updateTime() {
        val formatter = DateTimeFormatter.ofPattern("EEE, d MMMM", Locale.getDefault())
        binding.tvDayDateAndMonth.text = LocalDate.now().format(formatter)
        binding.timeText.format12Hour = "hh:mma"

        setupClockWithSmallAmPm(binding.timeText)
    }

    private var isUpdatingClock = false
    private fun setupClockWithSmallAmPm(textClock: TextClock) {
        textClock.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdatingClock) return

                val text = s?.toString() ?: return

                val amPmIndex = findAmPmIndex(text) ?: return

                isUpdatingClock = true

                val spannable = SpannableString(text)
                spannable.setSpan(
                    RelativeSizeSpan(0.5f),
                    amPmIndex,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                textClock.text = spannable
                isUpdatingClock = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun findAmPmIndex(text: String): Int? {
        val symbols = DateFormatSymbols.getInstance().amPmStrings
        for (ampm in symbols) {
            val index = text.indexOf(ampm)
            if (index >= 0) return index
        }
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchToggle() {

        binding.root.setOnTouchListener { _, event ->
            toggleUiState()
            false
        }
    }

    private fun toggleUiState() {
        isHomePreviewVisible = !isHomePreviewVisible

        binding.llHomePreview.isVisible = isHomePreviewVisible
    }

    private fun showWallpaperSetDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogSetWallpaperBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setDimAmount(0.8f)
            setGravity(Gravity.BOTTOM)

            attributes.windowAnimations = R.style.DialogAnimationWallpaper
        }


        dialogBinding.tvSetAsLockScreen.setOnClickListener {
            showWallpaperApplyingDialog()
            applyWallpaper(WallpaperType.LOCK)
            dialog.dismiss()
        }

        dialogBinding.tvSetAsHomeScreen.setOnClickListener {
            showWallpaperApplyingDialog()
            applyWallpaper(WallpaperType.HOME)
            dialog.dismiss()
        }

        dialogBinding.tvSetAsBoth.setOnClickListener {
            showWallpaperApplyingDialog()
            applyWallpaper(WallpaperType.BOTH)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showWallpaperApplyingDialog() {
        if (isFinishing || isDestroyed) return

        val dialog = Dialog(this)
        val dialogBinding = DialogApplyingWallpaperBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setDimAmount(0.8f)
        }

        applyingDialog = dialog
        dialog.show()
    }

    private fun safelyDismissApplyingDialog() {
        try {
            applyingDialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
            applyingDialog = null
        } catch (e: Exception) {
            Log.e("DialogError", "Dialog dismiss crash: ${e.message}")
        }
    }

    override fun onDestroy() {
        safelyDismissApplyingDialog()
        super.onDestroy()
    }

    fun showUnlockWallpaperDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogUnlockWallpaperBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setDimAmount(0.8f)
            setGravity(Gravity.CENTER)
        }

        dialogBinding.btnWatchAd.setOnClickListener {
            if (!Codebizz_PackageMonitorAdsManager.isNetworkAvailable(this)) {
                Toast.makeText(
                    this,
                    getString(R.string.lc_check_your_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            dialog.dismiss()

            Codebizz_RewardedAdLoader.loadAndShowRewardAd(
                this,
                AdsManager.adsData().Rewarded_Video,
                object : Codebizz_RewardedAdLoader.OnRewardAdAction {
                    override fun onRewarded() {
                        config.remainingWallpaperCount = 3
                        showWallpaperSetDialog()
                    }

                    override fun onAdShowing() {

                    }

                    override fun onDismiss() {

                    }

                    override fun onRewardNotEarned() {
                        Toast.makeText(
                            this@SetWallpaperActivity,
                            "Please try again after some time.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }


        dialog.show()
    }
}