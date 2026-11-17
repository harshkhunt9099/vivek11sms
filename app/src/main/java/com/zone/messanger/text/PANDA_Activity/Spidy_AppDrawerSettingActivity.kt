package com.zone.messanger.text.PANDA_Activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.databinding.ActivityAppDrawerSettingBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import kotlin.math.roundToInt

class Spidy_AppDrawerSettingActivity : Messanger__BaseActivity() {
    lateinit var binding: ActivityAppDrawerSettingBinding

    private val MIN_TEXT_LIMIT = 8
    private val MAX_TEXT_LIMIT = 28
    private val SEEKBAR_TEXT_MAX = 32

    private val MIN_ICON_DP = 32
    private val MAX_ICON_DP = 128
    private val SEEKBAR_ICON_MAX = 128
    private val BASE_ICON_SCALE_REF = 64.0f

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAppDrawerSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdsManager.showNative(
            this,
            container = binding.nativeContainer,
            nativeType = NativeType.SMALL_NATIVE,
            adId = adsData().Native_All_Center_Side_id,
            uniqueKey = this::class.java.simpleName,
        )

        initialize()
        initClick()
    }

    private fun initClick() {
        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@Spidy_AppDrawerSettingActivity, false) {
                finish()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.switchHideDrawerLabels.setOnCheckedChangeListener { _, isChecked ->
            config.drawerLabelHide = isChecked
            manageLabelsVisibility(isChecked)

            if (isChecked) {
                binding.llTextSize.alpha = 0.5f
                binding.seekBarTextSize.isEnabled = false
            } else {
                binding.llTextSize.alpha = 1.0f
                binding.seekBarTextSize.isEnabled = true
            }
        }

    }

    private fun manageLabelsVisibility(isHidden: Boolean) {
        val visibility = if (isHidden) View.INVISIBLE else View.VISIBLE
        binding.tvLabel1.visibility = visibility
        binding.tvLabel2.visibility = visibility
        binding.tvLabel3.visibility = visibility
        binding.tvLabel4.visibility = visibility
    }


    private fun initialize() {
        val isHidden = config.drawerLabelHide
        binding.switchHideDrawerLabels.isChecked = isHidden
        binding.llTextSize.alpha = if (isHidden) 0.5f else 1.0f
        manageLabelsVisibility(isHidden)
        binding.seekBarTextSize.isEnabled = !isHidden

        val currentTextSize = config.drawerTextSize.toInt()

        binding.seekBarTextSize.max = SEEKBAR_TEXT_MAX

        if (currentTextSize >= MAX_TEXT_LIMIT) {
            binding.seekBarTextSize.progress = SEEKBAR_TEXT_MAX
        } else if (currentTextSize <= MIN_TEXT_LIMIT) {
            binding.seekBarTextSize.progress = 0
        } else {
            binding.seekBarTextSize.progress = currentTextSize
        }

        val clampedTextValue = currentTextSize.coerceIn(MIN_TEXT_LIMIT, MAX_TEXT_LIMIT)
        updateTextSizeLabel(clampedTextValue)
        updatePreviewLabelsSize(clampedTextValue.toFloat())

        binding.seekBarTextSize.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    val effectiveSize = progress.coerceIn(MIN_TEXT_LIMIT, MAX_TEXT_LIMIT)

                    config.drawerTextSize = effectiveSize.toFloat()

                    updateTextSizeLabel(effectiveSize)
                    updatePreviewLabelsSize(effectiveSize.toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        val storedScale = if (config.drawerIconSize <= 0f) 1.0f else config.drawerIconSize

        val currentIconDp = (storedScale * BASE_ICON_SCALE_REF).roundToInt()

        binding.seekBarIconSize.max = SEEKBAR_ICON_MAX

        if (currentIconDp >= MAX_ICON_DP) {
            binding.seekBarIconSize.progress = SEEKBAR_ICON_MAX
        } else if (currentIconDp <= MIN_ICON_DP) {
            binding.seekBarIconSize.progress = 0
        } else {
            binding.seekBarIconSize.progress = currentIconDp
        }

        val clampedIconDp = currentIconDp.coerceIn(MIN_ICON_DP, MAX_ICON_DP)
        updateIconSizeLabel(clampedIconDp)
        updateIconPreview(clampedIconDp)

        binding.seekBarIconSize.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val effectiveSizeDp = progress.coerceIn(MIN_ICON_DP, MAX_ICON_DP)

                val newScale = effectiveSizeDp.toFloat() / BASE_ICON_SCALE_REF

                config.drawerIconSize = newScale

                updateIconSizeLabel(effectiveSizeDp)
                updateIconPreview(effectiveSizeDp)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateTextSizeLabel(size: Int) {
        binding.tvTextSize.text = "$size pt"
    }

    private fun updatePreviewLabelsSize(size: Float) {
        binding.tvLabel1.textSize = size
        binding.tvLabel2.textSize = size
        binding.tvLabel3.textSize = size
        binding.tvLabel4.textSize = size
    }

    private fun updateIconSizeLabel(sizeDp: Int) {
        binding.tvIconSize.text = "$sizeDp dp"
    }

    private fun updateIconPreview(sizeDp: Int) {
        val sizePx = (sizeDp * resources.displayMetrics.density).toInt()

        setParams(binding.ivIcon1, sizePx)
        setParams(binding.ivIcon2, sizePx)
        setParams(binding.ivIcon3, sizePx)
        setParams(binding.ivIcon4, sizePx)
    }

    private fun setParams(view: ImageView, sizePx: Int) {
        val params = view.layoutParams
        params.width = sizePx
        params.height = sizePx
        view.layoutParams = params
    }
}