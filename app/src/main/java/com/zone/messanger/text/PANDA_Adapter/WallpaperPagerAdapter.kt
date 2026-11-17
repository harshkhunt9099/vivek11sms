package com.zone.messanger.text.PANDA_Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zone.messanger.text.PANDA_ads.WallpaperCategory
import com.zone.messanger.text.Fragments.WallpaperCategoryFragment

class WallpaperPagerAdapter(
    activity: FragmentActivity,
    private val categories: List<WallpaperCategory>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = categories.size

    override fun createFragment(position: Int): Fragment {
        return WallpaperCategoryFragment.Companion.newInstance(
            categories[position]
        )
    }
}