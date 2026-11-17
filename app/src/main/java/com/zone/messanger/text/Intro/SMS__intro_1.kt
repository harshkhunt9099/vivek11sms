package com.zone.messanger.text.Intro


import android.content.Intent
import android.os.Bundle
import android.view.View

import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType

class SMS__intro_1 : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {


        lateinit var next: ImageView


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        lateinit var tvFemale: TextView
        lateinit var tvMale: TextView

        var selectedGender = "Female"


        tvFemale = findViewById(R.id.tvFemale)
        tvMale = findViewById(R.id.tvMale)

        val layoutFemale = findViewById<RelativeLayout>(R.id.layoutFemale)
        val layoutMale   = findViewById<RelativeLayout>(R.id.layoutMale)

// Female Select
        layoutFemale.setOnClickListener {

            layoutFemale.setBackgroundResource(R.drawable.bg_gender_selected)
            layoutMale.setBackgroundResource(R.drawable.bg_gender_unselected)

            tvFemale.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            tvMale.setTextColor(ContextCompat.getColor(this, R.color.black))

            selectedGender = "Female"

            next.isEnabled = true
            next.alpha = 1f
        }

// Male Select
        layoutMale.setOnClickListener {

            layoutMale.setBackgroundResource(R.drawable.bg_gender_selected)
            layoutFemale.setBackgroundResource(R.drawable.bg_gender_unselected)

            tvMale.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            tvFemale.setTextColor(ContextCompat.getColor(this, R.color.black))

            selectedGender = "Male"

            next.isEnabled = true
            next.alpha = 1f
        }


        next = findViewById(R.id.next)

        // Next Button
        next.setOnClickListener {

            if (selectedGender.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please select gender first",
                    Toast.LENGTH_SHORT
                ).show()

            } else {



                AdsManager.showInterstitialLauncher(this, false) {
                    startActivity(
                        Intent(this, SMS_intro_2::class.java)
                    )
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
        // 🔙 Back Button
        findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@SMS__intro_1, false) {
                finish()
            }
        }

    }


}