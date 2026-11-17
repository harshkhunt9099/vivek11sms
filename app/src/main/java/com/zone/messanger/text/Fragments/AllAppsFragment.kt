package com.zone.messanger.text.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdView
import com.zone.messanger.text.Models.LauncherDisplayItem
import com.zone.messanger.text.PANDA_Activity.MainLauncherActivity
import com.zone.messanger.text.PANDA_Adapter.LaunchersAdapter
import com.zone.messanger.text.databinding.FragmentAllAppsBinding
import com.zone.messanger.text.Data.Smartly_Extension.beGone
import com.zone.messanger.text.Data.Smartly_Extension.beVisible
import com.zone.messanger.text.Data.Smartly_Extension.beVisibleIf
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.getProperPrimaryColor
import com.zone.messanger.text.Data.Smartly_Extension.getProperTextColor
import com.zone.messanger.text.Data.Smartly_Extension.hideKeyboard
import com.zone.messanger.text.Data.Smartly_Extension.isDefaultLauncher
import com.zone.messanger.text.Data.Smartly_Extension.recentAppsDB
import com.zone.messanger.text.Data.Smartly_Extension.setupDrawerBackground
import com.zone.messanger.text.Helper.ITEM_TYPE_ICON
import com.zone.messanger.text.Helper.ensureBackgroundThread
import com.zone.messanger.text.Helper.normalizeString
import com.zone.messanger.text.Helper.toAppLauncher
import com.zone.messanger.text.InterFaces.AllAppsListener
import com.zone.messanger.text.Models.AppLauncher
import com.zone.messanger.text.Models.HeaderType
import com.zone.messanger.text.Models.HomeScreenGridItem
import com.zone.messanger.text.Views.MyGridLayoutManager
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
// NEW: HomeScreenGrid ma je import chhe te j (NativeType jya hoy tyathi)
import com.zone.messanger.text.PANDA_ads.NativeType

class AllAppsFragment(
    context: Context,
    attributeSet: AttributeSet,
) : MyFragment<FragmentAllAppsBinding>(context, attributeSet), AllAppsListener {

    private var lastTouchCoords = Pair(0f, 0f)
    var touchDownY = -1
    var ignoreTouches = false
    private var lastLabelHideState: Boolean? = false
    private var lastTextSize: Float? = 12.0f
    private var lastIconScale: Float = 0.85f

    private var lastShowRecentApps: Boolean = true

    private var allAppsList = emptyList<AppLauncher>()
    private var recentAppsList = emptyList<AppLauncher>()

    private var lastIsDefaultLauncher: Boolean = false
    private var lastHasOverlayPermission: Boolean = false

    // ---------------- Banner state ----------------
    private var isViewReady = false
    private var bannerLoaded = false
    private var isDrawerExpanded = false
    private var observedLifecycleOwner: LifecycleOwner? = null

    companion object {
        private const val TAG = "BannerDebug"

        // Drawer position thresholds (fraction of root view height)
        private const val EXPANDED_TOP_FRACTION = 0.25f
        private const val COLLAPSED_TOP_FRACTION = 0.90f
    }

    // Detects drawer open/close by itself, no activity call needed
    private val drawerPositionListener = ViewTreeObserver.OnPreDrawListener {
        checkDrawerPosition()
        true
    }
    private var drawerListenerAdded = false

    @SuppressLint("ClickableViewAccessibility")
    override fun setupFragment(activity: MainLauncherActivity) {
        this.activity = activity
        this.binding = FragmentAllAppsBinding.bind(this)
        isViewReady = true
        Log.d(TAG, "setupFragment: view ready, isDrawerExpanded=$isDrawerExpanded")

        binding.allAppsGrid.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                touchDownY = -1
            }

            return@setOnTouchListener false
        }

        // If the drawer was already expanded before the view was ready, load now
        if (isDrawerExpanded) {
            loadBanner()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupDrawerBackground()

        val owner = findLifecycleOwner()
        Log.d(TAG, "onAttachedToWindow: lifecycleOwner=$owner")
        if (owner != null && observedLifecycleOwner == null) {
            observedLifecycleOwner = owner
            owner.lifecycle.addObserver(bannerLifecycleObserver)
        }

        if (!drawerListenerAdded) {
            viewTreeObserver.addOnPreDrawListener(drawerPositionListener)
            drawerListenerAdded = true
        }
    }

    override fun onDetachedFromWindow() {
        if (drawerListenerAdded) {
            viewTreeObserver.removeOnPreDrawListener(drawerPositionListener)
            drawerListenerAdded = false
        }
        observedLifecycleOwner?.lifecycle?.removeObserver(bannerLifecycleObserver)
        observedLifecycleOwner = null
        isDrawerExpanded = false
        unloadBanner()
        super.onDetachedFromWindow()
    }

    /**
     * Runs every frame (cheap math only). Decides if the drawer is open
     * based on where the fragment is on screen.
     */
    private fun checkDrawerPosition() {
        if (!isViewReady) return

        val rootHeight = rootView.height
        if (rootHeight <= 0) return

        val loc = IntArray(2)
        getLocationInWindow(loc)
        val top = loc[1]

        val visibleOnScreen = isShown && alpha > 0f

        val isOpen = visibleOnScreen && top < rootHeight * EXPANDED_TOP_FRACTION
        val isClosed = !visibleOnScreen || top > rootHeight * COLLAPSED_TOP_FRACTION

        if (isOpen && !isDrawerExpanded) {
            Log.d(TAG, "auto-detect: drawer OPEN (top=$top, rootHeight=$rootHeight)")
            fragmentExpanded()
        } else if (isClosed && isDrawerExpanded) {
            Log.d(TAG, "auto-detect: drawer CLOSED (top=$top, rootHeight=$rootHeight)")
            fragmentCollapsed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onResume() {
        if (binding.allAppsGrid.layoutManager == null || binding.allAppsGrid.adapter == null) {
            return
        }

        val adapter = binding.allAppsGrid.adapter as LaunchersAdapter
        var shouldRefresh = false

        val layoutManager = binding.allAppsGrid.layoutManager as MyGridLayoutManager
        if (layoutManager.spanCount != context.config.drawerColumnCount) {
            onConfigurationChanged()
            shouldRefresh = true
        }

        // Check 2: Label Visibility
        val currentLabelHideState = context.config.drawerLabelHide
        if (lastLabelHideState != currentLabelHideState) {
            lastLabelHideState = currentLabelHideState
            shouldRefresh = true
        }

        // Check 3: Text Size
        val currentTextSize = context.config.drawerTextSize
        if (lastTextSize != currentTextSize) {
            lastTextSize = currentTextSize
            shouldRefresh = true
        }

        val currentIconScale = context.config.drawerIconSize
        if (lastIconScale != currentIconScale) {
            lastIconScale = currentIconScale
            shouldRefresh = true

            adapter.calculateIconSize()
        }

        val currentShowRecentApps = context.config.showAppSuggestion
        if (lastShowRecentApps != currentShowRecentApps) {
            lastShowRecentApps = currentShowRecentApps
            shouldRefresh = true
        }

        val currentIsDefault = context.isDefaultLauncher()
        val currentHasOverlay = Settings.canDrawOverlays(context)

        if (lastIsDefaultLauncher != currentIsDefault || lastHasOverlayPermission != currentHasOverlay) {
            lastIsDefaultLauncher = currentIsDefault
            lastHasOverlayPermission = currentHasOverlay
            submitList(allAppsList)
        }

        if (shouldRefresh) {
            adapter.notifyDataSetChanged()
        }

        refreshRecentApps()
    }

    private fun refreshRecentApps() {
        ensureBackgroundThread {
            val recentsInDb = context.recentAppsDB.getRecentApps()

            val installedAppsMap = allAppsList.associateBy { it.packageName }

            val recentsAsLaunchers = recentsInDb.mapNotNull { recent ->
                val existingApp = installedAppsMap[recent.packageName]
                existingApp ?: recent.toAppLauncher(context.packageManager)
            }

            activity?.runOnUiThread {
                val shouldScrollToTop = recentAppsList.isEmpty() && recentsAsLaunchers.isNotEmpty()

                recentAppsList = recentsAsLaunchers

                submitList(allAppsList, scrollToTop = shouldScrollToTop)
            }
        }
    }

    fun onConfigurationChanged() {
        binding.allAppsGrid.scrollToPosition(0)
        setupViews()

        val layoutManager = binding.allAppsGrid.layoutManager as MyGridLayoutManager
        layoutManager.spanCount = context.config.drawerColumnCount
        setupAdapter()
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onInterceptTouchEvent(event)
        }

        var shouldIntercept = false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownY = event.y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                if (ignoreTouches) {
                    if (lastTouchCoords.first != event.x || lastTouchCoords.second != event.y) {
                        touchDownY = -1
                        return true
                    }
                }

                // pull the whole fragment down if it is scrolled way to the top and the user pulls it even further
                if (touchDownY != -1) {
                    val distance = event.y.toInt() - touchDownY
                    shouldIntercept =
                        distance > 0 && binding.allAppsGrid.computeVerticalScrollOffset() == 0
                    if (shouldIntercept) {
                        // Hiding is expensive, only do it if focused
                        if (binding.searchBar.hasFocus()) {
                            activity?.hideKeyboard()
                        }
                        activity?.startHandlingTouches(touchDownY)
                        touchDownY = -1
                    }
                }
            }
        }

        lastTouchCoords = Pair(event.x, event.y)
        return shouldIntercept
    }

    fun gotLaunchers(appLaunchers: List<AppLauncher>) {
        allAppsList = appLaunchers.sortedWith(
            compareBy(
                { it.title.normalizeString().lowercase() },
                { it.packageName }
            )
        )

        setupAdapter()

        refreshRecentApps()
    }

    private fun getAdapter() = binding.allAppsGrid.adapter as? LaunchersAdapter

    private fun setupAdapter() {
        activity?.runOnUiThread {
            val layoutManager = binding.allAppsGrid.layoutManager as MyGridLayoutManager
            val spanCount = context.config.drawerColumnCount
            layoutManager.spanCount = spanCount

            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val adapter = getAdapter() ?: return 1
                    val item = adapter.currentList.getOrNull(position)

                    return if (item is LauncherDisplayItem.Separator || item is LauncherDisplayItem.HeaderItem) {
                        spanCount
                    } else {
                        1
                    }
                }
            }

            if (getAdapter() == null) {
                LaunchersAdapter(
                    activity = activity!!,
                    allAppsListener = this,
                    itemClick = { itemClicked ->
                        if (itemClicked is LauncherDisplayItem.AppItem) {
                            val app = itemClicked.appLauncher

                            activity?.launchAppFromDrawer(app)

                            if (activity?.config?.closeAppDrawer == true) {
                                activity?.closeAppDrawer(delayed = true)
                            }
                        }
                        ignoreTouches = false
                        touchDownY = -1
                    },
                    onHeaderClick = { type ->

                        when (type) {
                            HeaderType.SET_DEFAULT_LAUNCHER -> {
                                if (!activity!!.isDefaultLauncher()) {
                                    activity!!.requestHomeRole()
                                }
                            }

                        }
                    }
                ).apply {
                    binding.allAppsGrid.itemAnimator = null
                    binding.allAppsGrid.adapter = this
                }
            }

            submitList(allAppsList, true)
        }
    }

    fun setupViews() {
        if (activity == null) {
            return
        }

        binding.allAppsFastscroller.updateColors(context.getProperPrimaryColor())
        binding.allAppsGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (binding.searchBar.hasFocus() && dy > 0 && binding.allAppsGrid.computeVerticalScrollOffset() > 0) {
                    activity?.hideKeyboard()
                }
            }
        })

        setupDrawerBackground()
        getAdapter()?.updateTextColor(context.getProperTextColor())

        binding.searchBar.beVisibleIf(context.config.showSearchBar)
        binding.searchBar.requireToolbar().beGone()
        binding.searchBar.updateColors()
        binding.searchBar.setupMenu()

        binding.searchBar.onSearchTextChangedListener = {
            submitList(allAppsList)
        }
    }

    private fun showNoResultsPlaceholderIfNeeded() {
        val itemCount = getAdapter()?.itemCount
        binding.noResultsPlaceholder.beVisibleIf(itemCount != null && itemCount == 0)
    }

    override fun onAppLauncherLongPressed(x: Float, y: Float, appLauncher: AppLauncher) {
        val gridItem = HomeScreenGridItem(
            id = null,
            left = -1,
            top = -1,
            right = -1,
            bottom = -1,
            page = 0,
            packageName = appLauncher.packageName,
            activityName = appLauncher.activityName,
            title = appLauncher.title,
            type = ITEM_TYPE_ICON,
            className = "",
            widgetId = -1,
            shortcutId = "",
            icon = null,
            docked = false,
            parentId = null,
            drawable = appLauncher.drawable
        )

        activity?.showHomeIconMenu(x, y, gridItem, true)
        ignoreTouches = true
    }

    fun onBackPressed(): Boolean {
        if (binding.searchBar.isSearchOpen) {
            binding.searchBar.closeSearch()
            return true
        }

        return false
    }

    private fun submitList(items: List<AppLauncher>, scrollToTop: Boolean = false) {
        val searchQuery = binding.searchBar.getCurrentQuery()

        val displayList = ArrayList<LauncherDisplayItem>()

        if (searchQuery.isNotEmpty()) {
            val filtered = items.filter {
                it.title.normalizeString()
                    .contains(searchQuery.normalizeString(), ignoreCase = true)
            }
            displayList.addAll(filtered.map { LauncherDisplayItem.AppItem(it) })
        } else {
            val isDefault = context.isDefaultLauncher()

            if (!isDefault) {
                displayList.add(LauncherDisplayItem.HeaderItem(HeaderType.SET_DEFAULT_LAUNCHER))
            }

            if (context.config.showAppSuggestion && recentAppsList.isNotEmpty()) {
                displayList.addAll(recentAppsList.map { LauncherDisplayItem.AppItem(it) })
                displayList.add(LauncherDisplayItem.Separator("Recent Apps"))
            }

            items.forEach {
                displayList.add(LauncherDisplayItem.AppItem(it))
            }
        }

        getAdapter()?.submitList(displayList) {
            showNoResultsPlaceholderIfNeeded()

            if (scrollToTop) {
                binding.allAppsGrid.scrollToPosition(0)
            }
        }
    }

    // =====================================================================
    //                    Banner / Native lifecycle
    // =====================================================================
    // Drawer expands            -> load banner / native
    // Drawer collapses / onStop / view detach -> unload
    // Activity onStart with drawer expanded   -> load again

    private val bannerLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            Log.d(TAG, "lifecycle onStart, isDrawerExpanded=$isDrawerExpanded")
            if (isDrawerExpanded) loadBanner()
        }

        override fun onStop(owner: LifecycleOwner) {
            Log.d(TAG, "lifecycle onStop")
            unloadBanner()
        }
    }

    private fun findLifecycleOwner(): LifecycleOwner? {
        var c: Context? = context
        while (c is ContextWrapper) {
            if (c is LifecycleOwner) return c
            c = c.baseContext
        }
        return null
    }

    /** Called automatically by position detection (activity may also call it). */
    fun fragmentExpanded() {
        Log.d(TAG, "fragmentExpanded called")
        isDrawerExpanded = true
        loadBanner()
    }

    /** Called automatically by position detection (activity may also call it). */
    fun fragmentCollapsed() {
        Log.d(TAG, "fragmentCollapsed called")
        isDrawerExpanded = false
        unloadBanner()
    }

    private fun isAdsEnabled(): Boolean {
        return try {
            val enabled = adsData().Home_banner_app_onoff
            Log.d(TAG, "Home_banner_app_onoff = $enabled")
            enabled
        } catch (e: Exception) {
            Log.e(TAG, "adsData() failed", e)
            false
        }
    }

    /**
     * NEW: Server flag (App drawer mate).
     * true  -> Banner ad
     * false -> Native ad
     * Tamara adsData() model ma je naam hoy te lakhvu (nichenu naam mara anuman mujab chhe).
     */
    private fun isBannerMode(): Boolean {
        return try {
            adsData().home_app_Bannertrue_Nativefalse
        } catch (e: Exception) {
            true // error aave to default Banner
        }
    }

    /**
     * NEW: Native size.
     * "1" -> LARGE_NATIVE, "2" -> SMALL_NATIVE, bakina -> SMALL_NATIVE_BANNER
     * (HomeScreenGrid ma jem j).
     */
    private fun getNativeType(): NativeType {
        return try {
            when (adsData().home_app_Native_Large_Small_Banner) {
                "1" -> NativeType.LARGE_NATIVE
                "2" -> NativeType.SMALL_NATIVE
                else -> NativeType.SMALL_NATIVE_BANNER
            }
        } catch (e: Exception) {
            NativeType.SMALL_NATIVE_BANNER
        }
    }

    private fun loadBanner() {
        Log.d(TAG, "loadBanner: viewReady=$isViewReady, loaded=$bannerLoaded, expanded=$isDrawerExpanded")

        if (!isViewReady) {
            Log.w(TAG, "loadBanner skipped: view not ready (will retry from setupFragment)")
            return
        }
        if (bannerLoaded) {
            Log.w(TAG, "loadBanner skipped: already loaded")
            return
        }
        if (!isDrawerExpanded) {
            Log.w(TAG, "loadBanner skipped: drawer not expanded")
            return
        }

        // Ads turned off from server -> load nothing
        if (!isAdsEnabled()) {
            binding.bannerContainer.removeAllViews()
            binding.bannerContainer.beGone()
            return
        }

        binding.bannerContainer.beVisible()
        bannerLoaded = true

        if (isBannerMode()) {
            // flag = true -> Banner ad
            Log.d(TAG, "loadBanner: Banner, adId=${adsData().Banner_All_swipe_1}")
            AdsManager.loadAndShowBanner2(
                context = context,
                container = binding.bannerContainer,
                adId = adsData().Banner_All_swipe_1,
            )
        } else {
            // flag = false -> Native ad (same banner ids)
            Log.d(TAG, "loadBanner: Native, type=${getNativeType()}")
            AdsManager.showNative1(
                context = context,
                container = binding.bannerContainer,
                nativeType = getNativeType(),
                uniqueKey = "AllAppsFragment",
                adId = adsData().Banner_All_swipe_1,
                secondAdId = adsData().Banner_All_swipe_2,
            )
        }
    }

    private fun unloadBanner() {
        if (!isViewReady) return
        Log.d(TAG, "unloadBanner")
        bannerLoaded = false
        destroyAdViews(binding.bannerContainer)
        // container keeps its minHeight, so the layout won't jump
        binding.bannerContainer.removeAllViews()
    }

    private fun destroyAdViews(view: View) {
        if (view is AdView) {
            view.pause()
            view.destroy()
            return
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                destroyAdViews(view.getChildAt(i))
            }
        }
    }
}