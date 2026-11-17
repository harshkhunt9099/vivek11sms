package com.zone.messanger.text.Intro

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zone.messanger.text.PANDA_Activity.Spidy_MainActivity
import com.zone.messanger.text.R
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.PANDA_text.activity.DefaultSmsActivity

class intro_1_3 : AppCompatActivity() {

    private lateinit var next: ImageView

    private lateinit var layoutInsta: LinearLayout
    private lateinit var layoutTikTok: LinearLayout
    private lateinit var layoutFacebook: LinearLayout
    private lateinit var layoutTwitter: LinearLayout
    private lateinit var layoutMitron: LinearLayout
    private lateinit var layoutAll: LinearLayout

    private lateinit var tvInsta: TextView
    private lateinit var tvTikTok: TextView
    private lateinit var tvFacebook: TextView
    private lateinit var tvTwitter: TextView
    private lateinit var tvMitron: TextView
    private lateinit var tvAll: TextView

    private var selectedCategory = "Instagram"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro13)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        next = findViewById(R.id.next)

        layoutInsta = findViewById(R.id.layoutInsta)
        layoutTikTok = findViewById(R.id.layoutTikTok)
        layoutFacebook = findViewById(R.id.layoutFacebook)
        layoutTwitter = findViewById(R.id.layoutTwitter)
        layoutMitron = findViewById(R.id.layoutMitron)
        layoutAll = findViewById(R.id.layoutAll)

        tvInsta = findViewById(R.id.tvInsta)
        tvTikTok = findViewById(R.id.tvTikTok)
        tvFacebook = findViewById(R.id.tvFacebook)
        tvTwitter = findViewById(R.id.tvTwitter)
        tvMitron = findViewById(R.id.tvMitron)
        tvAll = findViewById(R.id.tvAll)

        layoutInsta.setOnClickListener { selectCategory("Instagram", layoutInsta, tvInsta) }
        layoutTikTok.setOnClickListener { selectCategory("TikTok", layoutTikTok, tvTikTok) }
        layoutFacebook.setOnClickListener { selectCategory("Facebook", layoutFacebook, tvFacebook) }
        layoutTwitter.setOnClickListener { selectCategory("Twitter", layoutTwitter, tvTwitter) }
        layoutMitron.setOnClickListener { selectCategory("Mitron", layoutMitron, tvMitron) }
        layoutAll.setOnClickListener { selectCategory("All Video", layoutAll, tvAll) }

        next.setOnClickListener {

            if (selectedCategory.isEmpty()) {

                Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show()

            } else {

                AdsManager.showInterstitialLauncher(this, false) {
                    if (adsData().INTRO_SCREEN_under) {
                        val intent = Intent(this@intro_1_3, SMS_intro_3::class.java)
                        val options = ActivityOptions.makeCustomAnimation(
                            this@intro_1_3, R.anim.slide_in_right, R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    } else {
                        val repository = SmsRepository(this@intro_1_3)
                        val next = if (repository.isDefaultSmsApp()) {
                            Intent(this@intro_1_3, Spidy_MainActivity::class.java)
                        } else {
                            Intent(this@intro_1_3, DefaultSmsActivity::class.java)
                        }
                        startActivity(next)
                        finish()
                    }
                }
            }
        }

        AdsManager.showNative(
            this,
            container = findViewById(R.id.nativeContainer),
            nativeType = NativeType.LARGE_NATIVE,
            adId = adsData().Native_All,
            uniqueKey = this::class.java.simpleName
        )

        findViewById<View>(R.id.btnBack).setOnClickListener { onBackPressed() }
        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@intro_1_3, false) { finish() }
        }
    }

    private fun selectCategory(
        category: String,
        selectedLayout: LinearLayout,
        selectedText: TextView
    ) {

        val layouts = listOf(layoutInsta, layoutTikTok, layoutFacebook, layoutTwitter, layoutMitron, layoutAll)
        val texts = listOf(tvInsta, tvTikTok, tvFacebook, tvTwitter, tvMitron, tvAll)

        layouts.forEach { it.setBackgroundResource(R.drawable.bg_gender_unselected) }
        texts.forEach { it.setTextColor(ContextCompat.getColor(this, R.color.black)) }

        selectedLayout.setBackgroundResource(R.drawable.bg_gender_selected)
        selectedText.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        selectedCategory = category

        next.isEnabled = true
        next.alpha = 1f
    }
}