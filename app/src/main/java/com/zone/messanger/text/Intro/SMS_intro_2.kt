package com.zone.messanger.text.Intro

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
import androidx.lifecycle.Lifecycle
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType

class SMS_intro_2 : AppCompatActivity() {

    private lateinit var next: ImageView

    private lateinit var layout18: LinearLayout
    private lateinit var layout25: LinearLayout
    private lateinit var layout32: LinearLayout
    private lateinit var layout40: LinearLayout

    private lateinit var tv18: TextView
    private lateinit var tv25: TextView
    private lateinit var tv32: TextView
    private lateinit var tv40: TextView

    var selectedAge = "18-24"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        next = findViewById(R.id.next)

        layout18 = findViewById(R.id.layout18)
        layout25 = findViewById(R.id.layout25)
        layout32 = findViewById(R.id.layout32)
        layout40 = findViewById(R.id.layout40)

        tv18 = findViewById(R.id.tv18)
        tv25 = findViewById(R.id.tv25)
        tv32 = findViewById(R.id.tv32)
        tv40 = findViewById(R.id.tv40)

        layout18.setOnClickListener { selectAge("18-24", layout18, tv18) }
        layout25.setOnClickListener { selectAge("25-31", layout25, tv25) }
        layout32.setOnClickListener { selectAge("32-39", layout32, tv32) }
        layout40.setOnClickListener { selectAge("40+", layout40, tv40) }

        next.setOnClickListener {

            if (selectedAge.isEmpty()) {
                Toast.makeText(this, "Please select age range", Toast.LENGTH_SHORT).show()
            } else {
                AdsManager.showInterstitialLauncher(this, false) {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        startActivity(Intent(this@SMS_intro_2, intro_1_3::class.java))
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
            AdsManager.showInterstitialLauncher(this@SMS_intro_2, false) { finish() }
        }
    }

    private fun selectAge(age: String, selectedLayout: LinearLayout, selectedText: TextView) {

        layout18.setBackgroundResource(R.drawable.bg_gender_unselected)
        layout25.setBackgroundResource(R.drawable.bg_gender_unselected)
        layout32.setBackgroundResource(R.drawable.bg_gender_unselected)
        layout40.setBackgroundResource(R.drawable.bg_gender_unselected)

        tv18.setTextColor(ContextCompat.getColor(this, R.color.black))
        tv25.setTextColor(ContextCompat.getColor(this, R.color.black))
        tv32.setTextColor(ContextCompat.getColor(this, R.color.black))
        tv40.setTextColor(ContextCompat.getColor(this, R.color.black))

        selectedLayout.setBackgroundResource(R.drawable.bg_gender_selected)
        selectedText.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        selectedAge = age

        next.isEnabled = true
        next.alpha = 1f
    }
}