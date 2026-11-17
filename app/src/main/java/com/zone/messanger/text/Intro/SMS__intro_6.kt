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
import com.zone.messanger.text.PANDA_Activity.Spidy_MainActivity
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.PANDA_Activity.SetDefaultLauncherActivity
import com.zone.messanger.text.Data.Smartly_Extension.isDefaultLauncher
import com.zone.messanger.text.R
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_text.activity.DefaultSmsActivity

class SMS__intro_6 : AppCompatActivity() {

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


        findViewById<View>(R.id.back).

        setOnClickListener {
            finish()
        }
        // ➡️ Next Button
        findViewById<View>(R.id.image).setOnClickListener {

            AdsManager.showInterstitialLauncher1(this, false) {

            }
        }

        AdsManager.showNative(
            this,
            container = findViewById(R.id.nativeContainer),
            nativeType = NativeType.LARGE_NATIVE,
            adId = adsData().Native_All,
            uniqueKey = this::class.java.simpleName
        )

        findViewById<View>(R.id.next).setOnClickListener {


            if (isDefaultLauncher()) {
                AdsManager.showInterstitialLauncher(this, false) {
                    val repository = SmsRepository(this@SMS__intro_6)
                    if (repository.isDefaultSmsApp()) {
                        val intent = Intent(this@SMS__intro_6, Spidy_MainActivity::class.java)
                        val options = ActivityOptions.makeCustomAnimation(
                            this@SMS__intro_6,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    } else {
                        val intent = Intent(this@SMS__intro_6, DefaultSmsActivity::class.java)
                        val options = ActivityOptions.makeCustomAnimation(
                            this@SMS__intro_6,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    }



                }
            }else{
                AdsManager.showInterstitialLauncher(this, false) {
                    val intent = Intent(this@SMS__intro_6, SetDefaultLauncherActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this@SMS__intro_6,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    startActivity(intent, options.toBundle())
                    finish()
                }
            }



        }

        val next = findViewById<TextView>(R.id.next)
        next.text = "GO"

        val hint_text = findViewById<TextView>(R.id.hint_text)
        hint_text.text = "Fast & Simple SMS"
        val hint_text1 = findViewById<TextView>(R.id.hint_text1)
        hint_text1.text = "Fast, Simple & Private Text\nessaging."


        val image = findViewById<ImageView>(R.id.image)


        if (adsData().intro_under_list.isNotEmpty()) {
            Glide.with(this)
                .load(adsData().intro_under_list[3])
                .into(image)
        }

        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@SMS__intro_6, false) {
                finish()
            }
        }

    }



}