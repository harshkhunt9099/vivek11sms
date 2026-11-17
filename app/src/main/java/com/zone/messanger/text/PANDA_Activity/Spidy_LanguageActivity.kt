package com.zone.messanger.text.PANDA_Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.zone.messanger.text.R
import com.zone.messanger.text.databinding.ActivityLanguageBinding
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.PANDA_Adapter.LanguageAdapter
import com.zone.messanger.text.Intro.SMS__intro_1
import com.zone.messanger.text.Views.LanguageModel

class Spidy_LanguageActivity : Spidy_BaseActivityStarting() {


    private lateinit var binding: ActivityLanguageBinding
    private var isFromSplash: Boolean = false
    private lateinit var adapter: LanguageAdapter
    private val languageList = ArrayList<LanguageModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageBinding.inflate(layoutInflater)
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
            adId = adsData().Native_All,
            uniqueKey = this::class.java.simpleName
        )

        initialize()
        setupRecycler()
        initClick()
    }

    private fun initialize() {
        isFromSplash = intent.getBooleanExtra("isFromSplash", false)
        binding.ivBack.isVisible = !isFromSplash
    }

    // 🔥 Recycler Setup
    private fun setupRecycler() {

        languageList.clear()

        languageList.add(LanguageModel("English", true))
        languageList.add(LanguageModel("Hindi", false))
        languageList.add(LanguageModel("Chinese", false))
        languageList.add(LanguageModel("Japanese", false))
        languageList.add(LanguageModel("German", false))
        languageList.add(LanguageModel("Spanish", false))
        languageList.add(LanguageModel("French", false))
        languageList.add(LanguageModel("Arabic", false))
        languageList.add(LanguageModel("Russian", false))
        languageList.add(LanguageModel("Portuguese", false))
        languageList.add(LanguageModel("Korean", false))
        languageList.add(LanguageModel("Italian", false))

        adapter = LanguageAdapter(languageList)

        binding.rvLanguage.layoutManager = LinearLayoutManager(this)
        binding.rvLanguage.adapter = adapter

    }

    private fun passIntent() {
        startActivity(Intent(this@Spidy_LanguageActivity, SMS__intro_1::class.java))
    }

    private fun initClick() {

        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@Spidy_LanguageActivity, false) {
                if (isFromSplash) {
                    passIntent()
                } else {
                    finish()
                }
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnDone.setOnClickListener {

            val selectedLang = adapter.getSelectedLanguage()
            // ✅ Direct save (NO config needed)
            val pref = getSharedPreferences("app_lang", MODE_PRIVATE)
            pref.edit()
                .putString("selectedLanguage", selectedLang)
                .putBoolean("isLanguageChecked", true)
                .apply()

            AdsManager.showInterstitialLauncher(this, false) {
                passIntent()
            }
        }
    }
}
