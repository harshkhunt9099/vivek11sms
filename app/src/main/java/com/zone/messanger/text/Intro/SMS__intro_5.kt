package com.zone.messanger.text.Intro

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType

class SMS__intro_5 : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inter_under)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔙 Back Button
        findViewById<View>(R.id.back).setOnClickListener {
            finish()
        }


        AdsManager.showNative(
            this,
            container = findViewById(R.id.nativeContainer),
            nativeType = NativeType.LARGE_NATIVE,
            adId = adsData().Native_All,
            uniqueKey = this::class.java.simpleName
        )

        // ➡️ Next Button
        findViewById<View>(R.id.image).setOnClickListener {
            AdsManager.showInterstitialLauncher1(this, false) {
            }
        }

        findViewById<View>(R.id.next).setOnClickListener {
            AdsManager.showInterstitialLauncher(this, false) {

                    val intent = Intent(this@SMS__intro_5, SMS__intro_6::class.java)

                    val options = ActivityOptions.makeCustomAnimation(
                        this@SMS__intro_5,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )

                    startActivity(intent, options.toBundle())
                    finish()
                }

        }


        val next = findViewById<TextView>(R.id.next)
        next.text = "Start"

        val hint_text = findViewById<TextView>(R.id.hint_text)
        hint_text.text = "Smothly Messanger"
        val hint_text1 = findViewById<TextView>(R.id.hint_text1)
        hint_text1.text = "Organize Your Conversations\nEffortlessly."


        val image = findViewById<ImageView>(R.id.image)


        if (adsData().intro_under_list.isNotEmpty()) {
            Glide.with(this)
                .load(adsData().intro_under_list[2])
                .into(image)
        }

        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@SMS__intro_5, false) {
                finish()
            }
        }

    }
}