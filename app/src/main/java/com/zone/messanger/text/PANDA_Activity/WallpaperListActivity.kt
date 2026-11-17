package com.zone.messanger.text.PANDA_Activity

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_Adapter.WallpaperPagerAdapter
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.PANDA_ads.WallpaperCategory
import com.zone.messanger.text.databinding.ActivityWallpaperListBinding
import com.zone.messanger.text.Data.Smartly_Extension.config

class WallpaperListActivity : Messanger__BaseActivity() {
    lateinit var binding: ActivityWallpaperListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityWallpaperListBinding.inflate(layoutInflater)
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

    private fun initialize() {
        setupViewPager(adsData().Wallpaper_Array)
    }

    private fun setupViewPager(categories: List<WallpaperCategory>) {
        if (categories.isEmpty()) return


        val pagerAdapter = WallpaperPagerAdapter(this, categories)
        binding.vpWallpaper.adapter = pagerAdapter
        binding.vpWallpaper.offscreenPageLimit = categories.size

        TabLayoutMediator(
            binding.tlCategory,
            binding.vpWallpaper
        ) { tab, position ->
            tab.text = categories[position].categoryName
        }.attach()

        addTabMargins(binding.tlCategory)
    }

    private fun initClick() {
        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@WallpaperListActivity, false) {
                finish()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun addTabMargins(tabLayout: TabLayout) {
        tabLayout.post {
            val tabStrip = tabLayout.getChildAt(0) as? ViewGroup ?: return@post

            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                val params = tabView.layoutParams as ViewGroup.MarginLayoutParams

                params.marginStart = 10.dp
                params.marginEnd = 10.dp

                tabView.layoutParams = params
            }
        }
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.tvRemaining.text = getString(R.string.lc_remaining) + config.remainingWallpaperCount.toString()+"/3"
    }
}