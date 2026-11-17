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
import com.zone.messanger.text.databinding.ActivityWebSearchSettingBinding
import com.zone.messanger.text.databinding.DialogSearchEngineBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Models.SearchEngine

class WebSearchSettingActivity : Messanger__BaseActivity() {
    lateinit var binding: ActivityWebSearchSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWebSearchSettingBinding.inflate(layoutInflater)
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
            AdsManager.showInterstitialLauncher(this@WebSearchSettingActivity, false) {
                finish()
            }
        }

        binding.switchDrawerSearch.setOnCheckedChangeListener { _, isChecked ->
            config.showSearchBar = isChecked
        }

        binding.switchAutoOpenKeyboard.setOnCheckedChangeListener { _, isChecked ->
            config.autoShowKeyboardInAppDrawer = isChecked
        }

        binding.switchCloseDrawer.setOnCheckedChangeListener { _, isChecked ->
            config.closeAppDrawer = isChecked
        }

        binding.switchAppSuggestions.setOnCheckedChangeListener { _, isChecked ->
            config.showAppSuggestion = isChecked
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.llSearchEngine.setOnClickListener {
            showSearchEngineDialog()
        }
    }

    private fun showSearchEngineDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogSearchEngineBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setDimAmount(0.8f)
        }

        // Set default
        when (config.searchEngine) {
            SearchEngine.GOOGLE -> dialogBinding.radioGoogle.isChecked = true
            SearchEngine.BING -> dialogBinding.radioBing.isChecked = true
            SearchEngine.DUCKDUCKGO -> dialogBinding.radioDuckDuck.isChecked = true
        }

        dialogBinding.btnApply.setOnClickListener {
            val selectedEngine = when (dialogBinding.radioGroup.checkedRadioButtonId) {
                R.id.radioGoogle -> SearchEngine.GOOGLE
                R.id.radioBing -> SearchEngine.BING
                R.id.radioDuckDuck -> SearchEngine.DUCKDUCKGO
                else -> SearchEngine.GOOGLE
            }

            config.searchEngine = selectedEngine

            val engine = config.searchEngine
            binding.tvSearchEngineName.text = engine.displayName

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initialize() {
        val engine = config.searchEngine

        when (engine.displayName) {
            "Google (Default)" -> {
                binding.tvSearchEngineName.text = resources.getString(R.string.lc_google_default)
            }

            "Bing" -> {
                binding.tvSearchEngineName.text = resources.getString(R.string.lc_bing)
            }

            "DuckDuckGo" -> {
                binding.tvSearchEngineName.text = resources.getString(R.string.lc_duckduckgo)
            }
        }

        binding.tvSearchEngineName.text = engine.displayName

        binding.switchDrawerSearch.isChecked = config.showSearchBar

        binding.switchAutoOpenKeyboard.isChecked = config.autoShowKeyboardInAppDrawer

        binding.switchCloseDrawer.isChecked = config.closeAppDrawer

        binding.switchAppSuggestions.isChecked = config.showAppSuggestion
    }

}