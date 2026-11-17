package com.zone.messanger.text.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import com.zone.messanger.text.PANDA_Activity.Spidy_MainActivity
import com.zone.messanger.text.PANDA_Activity.MainLauncherActivity
import com.zone.messanger.text.PANDA_Adapter.AdsAppsAdapterSideBar
import com.zone.messanger.text.PANDA_Adapter.RecentAppsAdapterRightPage
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.databinding.FragmentRightSideBinding
import com.zone.messanger.text.Data.Smartly_Extension.getLauncherActivityName
import com.zone.messanger.text.Data.Smartly_Extension.isPackageInstalled
import com.zone.messanger.text.Data.Smartly_Extension.launchApp
import com.zone.messanger.text.Data.Smartly_Extension.openStore
import com.zone.messanger.text.Data.Smartly_Extension.recentAppsDB
import com.zone.messanger.text.Helper.ensureBackgroundThread

import com.zone.messanger.text.Helper.toUiItem
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_text.activity.DefaultSmsActivity
import kotlin.math.abs

class RightSideFragment(context: Context, attributeSet: AttributeSet) :
    MyFragment<FragmentRightSideBinding>(context, attributeSet) {

    private var touchDownX = 0f
    private var touchDownY = 0f
    private var isDragging = false
    private val swipeThreshold = 50f


    @SuppressLint("ClickableViewAccessibility")
    override fun setupFragment(activity: MainLauncherActivity) {
        this.activity = activity
        this.binding = FragmentRightSideBinding.bind(this)


        initClick()
    }

    private fun initClick() {
        binding.rlSchedule.setOnClickListener {


            val repository = SmsRepository(context)
            if (repository.isDefaultSmsApp()) {
                activity!!.startActivity(Intent(activity!!, Spidy_MainActivity::class.java))
            } else {
                activity!!.startActivity(Intent(activity!!, DefaultSmsActivity::class.java))
            }

            activity?.hideRightSideFragment()
        }

        binding.rlBackup.setOnClickListener {
            activity!!.startActivity(Intent(activity!!, Spidy_MainActivity::class.java))

            activity?.hideRightSideFragment()
        }
    }

    fun refreshContent() {
        AdsManager.loadAndShowBanner(
            context = activity!!,
            container = binding.bannerContainer,
            adId = adsData().Banner_All,
            uniqueKey = "LauncherSidePage"
        )

        AdsManager.showNative(
            activity!!,
            container = binding.nativeContainer,
            nativeType = NativeType.LARGE_NATIVE_LAUNCHER_SIDE_PAGE,
            adId = adsData().Native_All_Right_Side_id,
            uniqueKey = "LauncherSidePageNative",
        )


        setupAdsAppsRightPage()

        ensureBackgroundThread {
            // Fetch fresh data from DB
            val recentsInDb = context.recentAppsDB.getRecentApps()

            activity?.runOnUiThread {
                // Update the adapter
                binding.rcvRecentApps.adapter = RecentAppsAdapterRightPage(
                    recentsInDb,
                    onAppClick = { app ->
//                        activity?.launchApp(app.packageName, app.activityName)
//                        activity?.hideRightSideFragment()
                    }
                )
            }
        }
    }

    private fun setupAdsAppsRightPage() {
        val uiItems =
            adsData().Launcher_CustomApps_Array
                .shuffled()
                .map { it.toUiItem() }

        binding.rcvAdsApp.adapter = AdsAppsAdapterSideBar(uiItems) { item ->

            if (activity!!.isPackageInstalled(item.packageName!!)) {
                activity!!.launchApp(
                    item.packageName,
                    activity!!.getLauncherActivityName(item.packageName)!!
                )
            } else {
                activity!!.openStore(item.packageName)
            }

        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = ev.rawX
                touchDownY = ev.rawY
                isDragging = false
                super.dispatchTouchEvent(ev)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = ev.rawX
                val currentY = ev.rawY
                val diffX = currentX - touchDownX
                val diffY = currentY - touchDownY

                if (!isDragging && diffX > swipeThreshold && abs(diffX) > abs(diffY)) {
                    isDragging = true

                    val cancelEvent = MotionEvent.obtain(ev)
                    cancelEvent.action = MotionEvent.ACTION_CANCEL
                    super.dispatchTouchEvent(cancelEvent)
                    cancelEvent.recycle()
                }

                if (isDragging) {
                    if (diffX > 0) {
                        this.x = diffX
                    }
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    val currentX = this.x

                    if (currentX > (screenWidth * 0.3f)) {
                        activity?.hideRightSideFragment()
                    } else {
                        this.animate()
                            .x(0f)
                            .setDuration(300)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                    isDragging = false
                    return true
                }
                isDragging = false
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
