package com.zone.messanger.text.PANDA_Activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.role.RoleManager
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Telephony
import android.telecom.TelecomManager
import android.view.ContextThemeWrapper
import android.view.GestureDetector
import android.view.Gravity
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.DecelerateInterpolator
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_Adapter.AdFolderAdapter
import com.zone.messanger.text.PANDA_ads.AdCycleController
import com.zone.messanger.text.PANDA_ads.AdsConfigManager
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.AdsManager.isNativeLoaded
import com.zone.messanger.text.PANDA_ads.AdsManager.isNetworkAvailable
import com.zone.messanger.text.PANDA_ads.AdsManager.shouldShowAd
import com.zone.messanger.text.PANDA_ads.FullscreenNativeDialog
import com.zone.messanger.text.PANDA_ads.GAds
import com.zone.messanger.text.PANDA_ads.GAppOpen
import com.zone.messanger.text.databinding.ActivityMainLauncherBinding
import com.zone.messanger.text.databinding.FragmentAllAppsBinding
import com.zone.messanger.text.databinding.FragmentWidgetsBinding
import com.zone.messanger.text.Data.RenameItemDialog
import com.zone.messanger.text.Data.getLabel
import com.zone.messanger.text.Data.Smartly_Extension.beVisible
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.getContrastColor
import com.zone.messanger.text.Data.Smartly_Extension.getLauncherActivityName
import com.zone.messanger.text.Data.Smartly_Extension.getPopupMenuTheme
import com.zone.messanger.text.Data.Smartly_Extension.getProperBackgroundColor
import com.zone.messanger.text.Data.Smartly_Extension.handleGridItemPopupMenu
import com.zone.messanger.text.Data.Smartly_Extension.hideKeyboard
import com.zone.messanger.text.Data.Smartly_Extension.homeScreenGridItemsDB
import com.zone.messanger.text.Data.Smartly_Extension.isDefaultLauncher
import com.zone.messanger.text.Data.Smartly_Extension.isPackageInstalled
import com.zone.messanger.text.Data.Smartly_Extension.launchApp
import com.zone.messanger.text.Data.Smartly_Extension.launchAppInfo
import com.zone.messanger.text.Data.Smartly_Extension.launchersDB
import com.zone.messanger.text.Data.Smartly_Extension.onGlobalLayout
import com.zone.messanger.text.Data.Smartly_Extension.openStore
import com.zone.messanger.text.Data.Smartly_Extension.performHapticFeedback
import com.zone.messanger.text.Data.Smartly_Extension.realScreenSize
import com.zone.messanger.text.Data.Smartly_Extension.recentAppsDB
import com.zone.messanger.text.Data.Smartly_Extension.roleManager
import com.zone.messanger.text.Data.Smartly_Extension.showErrorToast
import com.zone.messanger.text.Data.Smartly_Extension.showKeyboard
import com.zone.messanger.text.Data.Smartly_Extension.toast
import com.zone.messanger.text.Data.Smartly_Extension.uninstallApp
import com.zone.messanger.text.Data.Smartly_Extension.viewBinding
import com.zone.messanger.text.Fragments.MyFragment
import com.zone.messanger.text.Helper.DARK_GREY
import com.zone.messanger.text.Helper.ITEM_TYPE_FOLDER
import com.zone.messanger.text.Helper.ITEM_TYPE_ICON
import com.zone.messanger.text.Helper.ITEM_TYPE_SHORTCUT
import com.zone.messanger.text.Helper.ITEM_TYPE_WIDGET
import com.zone.messanger.text.Helper.IconCache
import com.zone.messanger.text.Helper.REQUEST_ALLOW_BINDING_WIDGET
import com.zone.messanger.text.Helper.REQUEST_CONFIGURE_WIDGET
import com.zone.messanger.text.Helper.REQUEST_CREATE_SHORTCUT
import com.zone.messanger.text.Helper.REQUEST_SET_DEFAULT
import com.zone.messanger.text.Helper.UNINSTALL_APP_REQUEST_CODE
import com.zone.messanger.text.Helper.ensureBackgroundThread
import com.zone.messanger.text.Helper.isQPlus
import com.zone.messanger.text.Helper.toUiItem
import com.zone.messanger.text.InterFaces.FlingListener
import com.zone.messanger.text.InterFaces.ItemMenuListener
import com.zone.messanger.text.Models.AppLauncher
import com.zone.messanger.text.Models.GestureAction
import com.zone.messanger.text.Models.GestureType
import com.zone.messanger.text.Models.HomeScreenGridItem
import com.zone.messanger.text.Data.LauncherHomePressWatcher
import com.launcher.launcherpackage.Codebizz_activity.Codebizz_InstallUninstallActivity
import com.launcher.launcherpackage.Codebizz_helper.Codebizz_PackageMonitorPrefManager
import com.launcher.launcherpackage.Codebizz_receiver.Codebizz_PackageChangeReceiver
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

import android.content.Intent;
import android.telephony.TelephonyManager
import android.util.Log
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_ads.AdsManager.INTER_LINKED_COUNT
import com.zone.messanger.text.PANDA_ads.AdsManager.INTER_LINKED_COUNT_RECYCLE

import com.zone.messanger.text.PANDA_ads.AdsManager.load_next_ad
import com.zone.messanger.text.PANDA_ads.AdsManager.openCustomTabIntent1
import com.zone.messanger.text.PANDA_text.activity.DefaultSmsActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.jvm.java

class MainLauncherActivity : Spidy_BaseSimpleActivity(), FlingListener {

    private var mTouchDownX = -1
    private var mTouchDownY = -1
    private var mAllAppsFragmentY = 0
    private var mWidgetsFragmentY = 0
    private var mScreenHeight = 0
    private var mMoveGestureThreshold = 0
    private var mIgnoreUpEvent = false
    private var mIgnoreMoveEvents = false
    private var mIgnoreXMoveEvents = false
    private var mIgnoreYMoveEvents = false
    private var mLongPressedIcon: HomeScreenGridItem? = null
    private var mOpenPopupMenu: PopupMenu? = null
    private var mLastTouchCoords = Pair(-1f, -1f)
    private var mActionOnCanBindWidget: ((granted: Boolean) -> Unit)? = null
    private var mActionOnWidgetConfiguredWidget: ((granted: Boolean) -> Unit)? = null
    private var mActionOnAddShortcut:
            ((shortcutId: String, label: String, icon: Drawable) -> Unit)? = null
    private var wasJustPaused: Boolean = false

    private lateinit var mDetector: GestureDetectorCompat
    private val binding by viewBinding(ActivityMainLauncherBinding::inflate)
    private var launcherHomeWatcher: LauncherHomePressWatcher? = null
    private var isActivityVisible = false

    companion object {
        private var mLastUpEvent = 0L
        private const val ANIMATION_DURATION = 150L
        private const val APP_DRAWER_CLOSE_DELAY = 300L
        private const val APP_DRAWER_STATE = "app_drawer_state"
    }

    private var pendingGridItem: HomeScreenGridItem? = null

    private val customTabLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (pendingGridItem != null) {
                executePendingAppLaunch()
                return@registerForActivityResult
            } else {
                startActivity(Intent(this, Spidy_MainActivity::class.java).apply {
                    flags = 335544320
                })
                overridePendingTransition(R.anim.lc_slide_in_left, R.anim.lc_slide_out_right)
            }
        }

    private fun executePendingAppLaunch() {
        pendingGridItem?.let { item ->
            launchApp(item.packageName, item.activityName)

            ensureBackgroundThread {
                recentAppsDB.onAppOpened(item.toRecentApps())
            }

            pendingGridItem = null
        }
    }

    private var packageChangeReceiver: Codebizz_PackageChangeReceiver? = null

    private lateinit var prefs: android.content.SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        try {
            setupEdgeToEdge(
                padTopSystem = listOf(
                    binding.allAppsFragment.root,
                    binding.widgetsFragment.root,
                    binding.rightSideFragment.nested
                ),
                padBottomImeAndSystem = listOf(
                    binding.allAppsFragment.root,
                    binding.widgetsFragment.root,
                    binding.rightSideFragment.root
                ),
                padBottomSystem = listOf(binding.homeScreenGrid.root)
            )

Log.e("#sdasdasdadasq","2222")


            mDetector = GestureDetectorCompat(this, MyGestureListener(this))

            mScreenHeight = realScreenSize.y
            mAllAppsFragmentY = mScreenHeight
            mWidgetsFragmentY = mScreenHeight
            mMoveGestureThreshold = resources.getDimensionPixelSize(R.dimen.lc_move_gesture_threshold)

            arrayOf(
                binding.allAppsFragment.root as MyFragment<*>,
                binding.widgetsFragment.root as MyFragment<*>,
            ).forEach { fragment ->
                fragment.setupFragment(this)
                fragment.y = mScreenHeight.toFloat()
                fragment.beVisible()
            }

            (binding.rightSideFragment.root as MyFragment<*>).let { fragment ->
                fragment.setupFragment(this)
                fragment.y = 0f
                fragment.x = realScreenSize.x.toFloat()
                fragment.beVisible()
            }

            if (INTER_LINKED_COUNT != 0) {
                INTER_LINKED_COUNT_RECYCLE = 0
                load_next_ad(INTER_LINKED_COUNT_RECYCLE, this)
            }

            setupHomePressWatcher()
            handleIntentAction(intent)

            binding.homeScreenGrid.root.itemClickListener = {
                performItemClick(it)
            }

            binding.homeScreenGrid.root.itemLongClickListener = {
                performItemLongClick(
                    x = binding.homeScreenGrid.root.getClickableRect(it).left.toFloat(),
                    clickedGridItem = it
                )
            }

            binding.allAppsFragment.btnUpdate.setOnClickListener {
                if (isNetworkAvailable(this)) {
                    updateApp()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.lc_check_your_internet_connection), Toast.LENGTH_SHORT
                    ).show()
                }
            }

            startPackageMonitoring(this)

            if (isNetworkAvailable(this)) {
                checkForUpdate()
            }

            prefs = getSharedPreferences("launcher_prefs", MODE_PRIVATE)




        } catch (e: Exception) {
            e.printStackTrace()
        }



    }

    private fun showRightSwipeHint() {
        binding.ivSwipeHintre.apply {
            visibility = View.VISIBLE

        }

        prefs.edit().putBoolean("first_time_swipee", false).apply()
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val wasAnyFragmentOpen =
            isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded() || isRightSideFragmentExpanded()
        if (wasJustPaused) {
            if (isAllAppsFragmentExpanded()) {
                hideFragment(binding.allAppsFragment)
            }
            if (isWidgetsFragmentExpanded()) {
                hideFragment(binding.widgetsFragment)
            }
            if (isRightSideFragmentExpanded()) {
                hideRightSideFragment()
            }

        } else {
            closeAppDrawer()
            closeWidgetsFragment()

            if (isRightSideFragmentExpanded()) {
                hideRightSideFragment()
            }
        }

        binding.allAppsFragment.searchBar.closeSearch()

        // scroll to first page when home button is pressed
        val alreadyOnHome = intent.flags and FLAG_ACTIVITY_BROUGHT_TO_FRONT == 0
        if (alreadyOnHome && !wasAnyFragmentOpen) {
            binding.homeScreenGrid.root.skipToPage(0)
        }

        handleIntentAction(intent)
    }

    override fun onStart() {
        super.onStart()
        binding.homeScreenGrid.root.appWidgetHost.startListening()
    }

    override fun onResume() {
        super.onResume()




        checkAndMakeIntentToMain()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if (isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded()) {
                when {
                    config.isDarkModeEnabled -> {
                        setNavigationBarColor(true)
                    }

                    else -> {
                        setNavigationBarColor(false)
                    }
                }
            } else {
                setNavigationBarColor(true)
            }

        }, 100)

        AdsConfigManager.tryFetch(this) {}

        wasJustPaused = false
        isActivityVisible = true

        with(binding.mainHolder) {
            onGlobalLayout {
                binding.allAppsFragment.root.setupViews()
                binding.widgetsFragment.root.setupViews()
            }
        }

        ensureBackgroundThread {
            if (IconCache.launchers.isEmpty()) {
                IconCache.launchers =
                    launchersDB.getAppLaunchers().toMutableList() as ArrayList<AppLauncher>
            }

            binding.allAppsFragment.root.gotLaunchers(IconCache.launchers)
            refreshLaunchers()
        }

        binding.homeScreenGrid.root.resizeGrid(
            newRowCount = config.homeRowCount,
            newColumnCount = config.homeColumnCount
        )

        binding.homeScreenGrid.root.updateColors()
        binding.allAppsFragment.root.onResume()
    }

    override fun onStop() {
        super.onStop()
        try {
            binding.homeScreenGrid.root.appWidgetHost.stopListening()
        } catch (_: Exception) {
        }

        wasJustPaused = false
    }

    override fun onPause() {
        super.onPause()
        wasJustPaused = true
        isActivityVisible = false
    }

    private var isHomePopupShowing = false

    override fun onBackPressedCompat(): Boolean {
        return if (isAllAppsFragmentExpanded()) {
            if (!binding.allAppsFragment.root.onBackPressed()) {
                hideFragment(binding.allAppsFragment)
                true
            } else {
                true
            }
        } else if (isWidgetsFragmentExpanded()) {
            hideFragment(binding.widgetsFragment)
            true
        } else if (isRightSideFragmentExpanded()) {
            hideRightSideFragment()
            true
        } else if (binding.homeScreenGrid.resizeFrame.isVisible) {
            binding.homeScreenGrid.root.hideResizeLines()
            true
        } else {
            manageBackPressAction()
            true
        }
    }


    private fun openGlobalSearch() {
        startActivity(Intent(this, WebSearchActivity::class.java))
    }

    private fun showHomeScreenPopup() {
        showMainLongPressMenu(position = PopupPosition.CENTER)
    }


    private fun openLauncherSetting() {
        startActivity(Intent(this, Spidy_LauncherSettingsActivity::class.java))
    }

    private fun openAppDrawer() {
        if (!isWidgetsFragmentExpanded()) {
            mIgnoreUpEvent = true
            showFragment(binding.allAppsFragment)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        when (requestCode) {
            UNINSTALL_APP_REQUEST_CODE -> {
                ensureBackgroundThread {
                    refreshLaunchers()
                }
            }

            REQUEST_ALLOW_BINDING_WIDGET -> mActionOnCanBindWidget?.invoke(resultCode == RESULT_OK)
            REQUEST_CONFIGURE_WIDGET -> mActionOnWidgetConfiguredWidget?.invoke(resultCode == RESULT_OK)
            REQUEST_CREATE_SHORTCUT -> {
                if (resultCode == RESULT_OK && resultData != null) {
                    val launcherApps =
                        applicationContext.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
                    if (launcherApps.hasShortcutHostPermission()) {
                        val item = launcherApps.getPinItemRequest(resultData)
                        val shortcutInfo = item?.shortcutInfo ?: return
                        if (item.accept()) {
                            val shortcutId = shortcutInfo.id
                            val label = shortcutInfo.getLabel()
                            val icon = launcherApps.getShortcutBadgedIconDrawable(
                                shortcutInfo,
                                resources.displayMetrics.densityDpi
                            )
                            mActionOnAddShortcut?.invoke(shortcutId, label, icon)
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.allAppsFragment.root.onConfigurationChanged()
        binding.widgetsFragment.root.onConfigurationChanged()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        if (mLongPressedIcon != null && event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            mLastUpEvent = System.currentTimeMillis()
        }

        try {
            mDetector.onTouchEvent(event)
        } catch (_: Exception) {
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mTouchDownX = event.x.toInt()
                mTouchDownY = event.y.toInt()
                mAllAppsFragmentY = binding.allAppsFragment.root.y.toInt()
                mWidgetsFragmentY = binding.widgetsFragment.root.y.toInt()
                mIgnoreUpEvent = false
            }

            MotionEvent.ACTION_MOVE -> {
                // if the initial gesture was handled by some other view, fix the Down values
                val hasFingerMoved = if (mTouchDownX == -1 || mTouchDownY == -1) {
                    mTouchDownX = event.x.toInt()
                    mTouchDownY = event.y.toInt()
                    false
                } else {
                    hasFingerMoved(event)
                }

                if (mLongPressedIcon != null && (mOpenPopupMenu != null) && hasFingerMoved) {
                    mOpenPopupMenu?.dismiss()
                    mOpenPopupMenu = null
                    binding.homeScreenGrid.root.itemDraggingStarted(mLongPressedIcon!!)
                    hideFragment(binding.allAppsFragment)
                }

                if (mLongPressedIcon != null && hasFingerMoved) {
                    binding.homeScreenGrid.root.draggedItemMoved(event.x.toInt(), event.y.toInt())
                }

                if (hasFingerMoved && !mIgnoreMoveEvents) {
                    val diffY = mTouchDownY - event.y
                    val diffX = mTouchDownX - event.x

                    if (abs(diffY) > abs(diffX) && !mIgnoreYMoveEvents) {
                        mIgnoreXMoveEvents = true
                        if (isWidgetsFragmentExpanded()) {
                            val newY = mWidgetsFragmentY - diffY
                            binding.widgetsFragment.root.y = min(
                                a = max(0f, newY), b = mScreenHeight.toFloat()
                            )
                        } else if (mLongPressedIcon == null) {
                            val newY = mAllAppsFragmentY - diffY
                            binding.allAppsFragment.root.y = min(
                                a = max(0f, newY), b = mScreenHeight.toFloat()
                            )
                        }
                    }/* else if (abs(diffX) > abs(diffY) && !mIgnoreXMoveEvents) {
                        mIgnoreYMoveEvents = true
                        binding.homeScreenGrid.root.setSwipeMovement(diffX)
                    }*/
                    else if (abs(diffX) > abs(diffY) && !mIgnoreXMoveEvents) {

                        if (isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded()) {
                            return true
                        }

                        mIgnoreYMoveEvents = true

                        val currentPage = binding.homeScreenGrid.root.currentPage
                        val lastPageIndex =
                            (binding.homeScreenGrid.root.maxPages - 1).coerceAtLeast(0)
                        val screenWidth = realScreenSize.x.toFloat()


                        if (currentPage >= lastPageIndex && diffX > 0) {

                            val newX = screenWidth - diffX

                            binding.rightSideFragment.root.x = newX.coerceIn(0f, screenWidth)
                        } else if (binding.rightSideFragment.root.x < screenWidth && diffX < 0) {
                            val newX = -diffX
                            binding.rightSideFragment.root.x = newX.coerceIn(0f, screenWidth)
                        } else {
                            binding.homeScreenGrid.root.setSwipeMovement(diffX)
                        }
                    }
                }

                mLastTouchCoords = Pair(event.x, event.y)
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP,
                -> {
                mTouchDownX = -1
                mTouchDownY = -1
                mIgnoreMoveEvents = false
                mLongPressedIcon = null
                mLastTouchCoords = Pair(-1f, -1f)
                resetFragmentTouches()
                binding.homeScreenGrid.root.itemDraggingStopped()

                if (!mIgnoreUpEvent) {
                    if (!mIgnoreYMoveEvents) {
                        if (binding.allAppsFragment.root.y < mScreenHeight * 0.5) {
                            showFragment(binding.allAppsFragment)
                        } else if (isAllAppsFragmentExpanded()) {
                            hideFragment(binding.allAppsFragment)
                            hideKeyboard()
                        }

                        if (binding.widgetsFragment.root.y < mScreenHeight * 0.5) {
                            showFragment(binding.widgetsFragment)
                        } else if (isWidgetsFragmentExpanded()) {
                            hideFragment(binding.widgetsFragment)
                        }
                    }

                    if (!mIgnoreXMoveEvents) {
                        val screenWidth = realScreenSize.x.toFloat()
                        val fragmentX = binding.rightSideFragment.root.x

                        if (fragmentX < screenWidth) {
                            mIgnoreUpEvent = true

                            if (fragmentX < (screenWidth * 0.8f)) {
                                showRightSideFragment()
                            } else {
                                // Otherwise snap CLOSED
                                hideRightSideFragment()
                            }
                        } else {
                            binding.homeScreenGrid.root.finalizeSwipe()
                        }
                    }

                }

                mIgnoreXMoveEvents = false
                mIgnoreYMoveEvents = false
            }
        }

        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(APP_DRAWER_STATE, isAllAppsFragmentExpanded())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.getBoolean(APP_DRAWER_STATE)) {
            showFragment(binding.allAppsFragment, 0L)
        }
    }

    private fun handleIntentAction(intent: Intent) {
        if (intent.action == LauncherApps.ACTION_CONFIRM_PIN_SHORTCUT) {
            val launcherApps =
                applicationContext.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
            val item = launcherApps.getPinItemRequest(intent)
            val shortcutInfo = item?.shortcutInfo ?: return

            ensureBackgroundThread {
                val shortcutId = shortcutInfo.id
                val label = shortcutInfo.getLabel()
                val icon = launcherApps.getShortcutBadgedIconDrawable(
                    shortcutInfo,
                    resources.displayMetrics.densityDpi
                )
                val (page, rect) = findFirstEmptyCell()
                val gridItem = HomeScreenGridItem(
                    id = null,
                    left = rect.left,
                    top = rect.top,
                    right = rect.right,
                    bottom = rect.bottom,
                    page = page,
                    packageName = shortcutInfo.`package`,
                    activityName = "",
                    title = label,
                    type = ITEM_TYPE_SHORTCUT,
                    className = "",
                    widgetId = -1,
                    shortcutId = shortcutId,
                    icon = icon.toBitmap(),
                    docked = false,
                    parentId = null,
                    drawable = icon
                )

                runOnUiThread {
                    binding.homeScreenGrid.root.skipToPage(page)
                }
                // delay showing the shortcut both to let the user see adding it in realtime and hackily avoid concurrent modification exception at HomeScreenGrid
                Thread.sleep(2000)

                try {
                    item.accept()
                    binding.homeScreenGrid.root.storeAndShowGridItem(gridItem)
                } catch (_: IllegalStateException) {
                }
            }
        }
    }

    private fun findFirstEmptyCell(): Pair<Int, Rect> {
        val gridItems =
            homeScreenGridItemsDB.getAllItems() as ArrayList<HomeScreenGridItem>
        val maxPage = gridItems.maxOf { it.page }
        val occupiedCells = ArrayList<Triple<Int, Int, Int>>()
        gridItems.toImmutableList().filter { it.parentId == null }.forEach { item ->
            for (xCell in item.left..item.right) {
                for (yCell in item.top..item.bottom) {
                    occupiedCells.add(Triple(item.page, xCell, yCell))
                }
            }
        }

        for (page in 0 until maxPage) {
            for (checkedYCell in 0 until config.homeColumnCount) {
                for (checkedXCell in 0 until config.homeRowCount - 1) {
                    val wantedCell = Triple(page, checkedXCell, checkedYCell)
                    if (!occupiedCells.contains(wantedCell)) {
                        return Pair(
                            first = page,
                            second = Rect(
                                wantedCell.second,
                                wantedCell.third,
                                wantedCell.second,
                                wantedCell.third
                            )
                        )
                    }
                }
            }
        }

        return Pair(maxPage + 1, Rect(0, 0, 0, 0))
    }

    private fun hasFingerMoved(event: MotionEvent): Boolean {
        return mTouchDownX != -1 && mTouchDownY != -1 &&
                (abs(mTouchDownX - event.x) > mMoveGestureThreshold || abs(mTouchDownY - event.y) > mMoveGestureThreshold)
    }

    private fun refreshLaunchers() {
        try {


            val currentLaunchers = getAllAppLaunchers()

            val currentPackageNames = currentLaunchers.map { it.packageName }.toHashSet()

            IconCache.launchers.forEach { oldLauncher ->
                if (!currentPackageNames.contains(oldLauncher.packageName)) {
                    launchersDB.deleteApp(oldLauncher.packageName)
                    recentAppsDB.deleteApp(oldLauncher.packageName)
                    homeScreenGridItemsDB.deleteByPackageName(oldLauncher.packageName)
                }
            }

            IconCache.launchers = currentLaunchers

            runOnUiThread {
                binding.allAppsFragment.root.gotLaunchers(currentLaunchers)
            }

            binding.widgetsFragment.root.getAppWidgets()

            if (!config.wasHomeScreenInit) {
                ensureBackgroundThread {
                    getDefaultAppPackages(currentLaunchers)
                    config.wasHomeScreenInit = true
                    binding.homeScreenGrid.root.fetchGridItems()
                }
            } else {
                binding.homeScreenGrid.root.fetchGridItems()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isAllAppsFragmentExpanded() = binding.allAppsFragment.root.y != mScreenHeight.toFloat()

    fun isRightSideFragmentExpanded(): Boolean {
        return binding.rightSideFragment.root.x < realScreenSize.x.toFloat()
    }

    private fun isWidgetsFragmentExpanded() =
        binding.widgetsFragment.root.y != mScreenHeight.toFloat()

    fun startHandlingTouches(touchDownY: Int) {
        mLongPressedIcon = null
        mTouchDownY = touchDownY
        mAllAppsFragmentY = binding.allAppsFragment.root.y.toInt()
        mWidgetsFragmentY = binding.widgetsFragment.root.y.toInt()
        mIgnoreUpEvent = false
    }

    private fun showFragment(fragment: ViewBinding, animationDuration: Long = ANIMATION_DURATION) {
        ObjectAnimator.ofFloat(fragment.root, "y", 0f).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            start()
        }

        window.navigationBarColor = resources.getColor(R.color.lc_semitransparent_navigation)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            when {
                config.isDarkModeEnabled -> {
                    setNavigationBarColor(true)
                }

                else -> {
                    setNavigationBarColor(false)
                }
            }
        }, 150)




        binding.homeScreenGrid.root.fragmentExpanded()
        binding.homeScreenGrid.root.hideResizeLines()

        @SuppressLint("AccessibilityFocus")
        fragment.root.performAccessibilityAction(
            AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,
            null
        )

        if (fragment is FragmentAllAppsBinding
            && config.showSearchBar
            && config.autoShowKeyboardInAppDrawer
        ) {
            fragment.root.postDelayed({
                showKeyboard(fragment.searchBar.binding.topToolbarSearch)
            }, animationDuration)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            updateStatusBarIcons()
        }, animationDuration)
    }

    private fun hideFragment(fragment: ViewBinding, animationDuration: Long = ANIMATION_DURATION) {
        ObjectAnimator.ofFloat(fragment.root, "y", mScreenHeight.toFloat()).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            start()
        }

        window.navigationBarColor = Color.TRANSPARENT
        setNavigationBarColor(true)

        binding.homeScreenGrid.root.fragmentCollapsed()
        updateStatusBarIcons(Color.TRANSPARENT)
        Handler(Looper.getMainLooper()).postDelayed({
            if (fragment is FragmentAllAppsBinding) {
                fragment.allAppsGrid.scrollToPosition(0)
                fragment.root.touchDownY = -1
            } else if (fragment is FragmentWidgetsBinding) {
                fragment.widgetsList.scrollToPosition(0)
                fragment.root.touchDownY = -1
            }
        }, animationDuration)
    }

    fun homeScreenLongPressed(eventX: Float, eventY: Float) {
        if (isAllAppsFragmentExpanded()) {
            return
        }

        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        mIgnoreMoveEvents = true
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            performItemLongClick(x, clickedGridItem)
            return
        }

        binding.mainHolder.performHapticFeedback()

        manageLongPressAction(x, y)
    }

    fun homeScreenClicked(eventX: Float, eventY: Float) {
        binding.homeScreenGrid.root.hideResizeLines()
        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            performItemClick(clickedGridItem)
        }
        if (clickedGridItem?.type != ITEM_TYPE_FOLDER) {
            binding.homeScreenGrid.root.closeFolder(redraw = true)
        }
    }

    fun homeScreenDoubleTapped(eventX: Float, eventY: Float) {
        val (x, y) = binding.homeScreenGrid.root.intoViewSpaceCoords(eventX, eventY)
        val clickedGridItem = binding.homeScreenGrid.root.isClickingGridItem(x.toInt(), y.toInt())
        if (clickedGridItem != null) {
            return
        }

        manageDoublePressAction(x, y)
    }

    fun closeAppDrawer(delayed: Boolean = false) {
        if (isAllAppsFragmentExpanded()) {
            val close = {
                binding.allAppsFragment.root.y = mScreenHeight.toFloat()
                binding.allAppsFragment.allAppsGrid.scrollToPosition(0)
                binding.allAppsFragment.root.touchDownY = -1
                binding.homeScreenGrid.root.fragmentCollapsed()
                updateStatusBarIcons(Color.TRANSPARENT)
            }
            if (delayed) {
                Handler(Looper.getMainLooper()).postDelayed(close, APP_DRAWER_CLOSE_DELAY)
            } else {
                close()
            }
        }
    }

    fun closeWidgetsFragment(delayed: Boolean = false) {
        if (isWidgetsFragmentExpanded()) {
            val close = {
                binding.widgetsFragment.root.y = mScreenHeight.toFloat()
                binding.widgetsFragment.widgetsList.scrollToPosition(0)
                binding.widgetsFragment.root.touchDownY = -1
                binding.homeScreenGrid.root.fragmentCollapsed()
                updateStatusBarIcons(Color.TRANSPARENT)
            }
            if (delayed) {
                Handler(Looper.getMainLooper()).postDelayed(close, APP_DRAWER_CLOSE_DELAY)
            } else {
                close()
            }
        }
    }

    private fun performItemClick(clickedGridItem: HomeScreenGridItem) {
        val n = clickedGridItem.type
        if (n == ITEM_TYPE_ICON) {

            this.pendingGridItem = clickedGridItem

            if (adsData().APP_CLICK_SHOW_AD) {
                showInterstitialLauncher(this, false) {
                    executePendingAppLaunch()
                }
            } else {
                executePendingAppLaunch()
            }

        } else if (n == ITEM_TYPE_FOLDER) {
            if (clickedGridItem.packageName == "ads_folder") {
                openAdsFolder()
            } else {
                openFolder(clickedGridItem)
            }
        } else if (n == ITEM_TYPE_SHORTCUT) {
            val id = clickedGridItem.shortcutId
            val packageName = clickedGridItem.packageName
            val userHandle = android.os.Process.myUserHandle()
            val shortcutBounds = binding.homeScreenGrid.root.getClickableRect(clickedGridItem)
            val launcherApps =
                applicationContext.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
            launcherApps.startShortcut(packageName, id, shortcutBounds, null, userHandle)
        }
    }

    private fun openAdsFolder() {

        binding.homeScreenGrid.homeScreenGrid.visibility = View.GONE

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.setContentView(R.layout.dialog_folder_ad)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.8f)
            setGravity(Gravity.CENTER)
        }

        val recycler = dialog.findViewById<RecyclerView>(R.id.rcvAdsApps)

        val uiItems =
            adsData().Launcher_CustomApps_Array
                .shuffled()
                .map { it.toUiItem() }


        recycler.adapter = AdFolderAdapter(uiItems) { item ->
            dialog.dismiss()

            if (isPackageInstalled(item.packageName!!)) {
                launchApp(item.packageName, getLauncherActivityName(item.packageName)!!)
            } else {
                openStore(item.packageName)
            }
        }

        dialog.setOnDismissListener {
            binding.homeScreenGrid.homeScreenGrid.visibility = View.VISIBLE
        }

        dialog.show()
    }


    private fun openFolder(folder: HomeScreenGridItem) {
        binding.homeScreenGrid.root.openFolder(folder)
    }

    private fun performItemLongClick(x: Float, clickedGridItem: HomeScreenGridItem) {
        if (clickedGridItem.type == ITEM_TYPE_ICON || clickedGridItem.type == ITEM_TYPE_SHORTCUT || clickedGridItem.type == ITEM_TYPE_FOLDER) {
            binding.mainHolder.performHapticFeedback()
        }

        if (clickedGridItem.packageName == "ads_folder") {
            return
        }

        val anchorY =
            binding.homeScreenGrid.root.sideMargins.top + (clickedGridItem.top * binding.homeScreenGrid.root.cellHeight.toFloat())
        showHomeIconMenu(x, anchorY, clickedGridItem, false)
    }

    fun showHomeIconMenu(
        x: Float,
        y: Float,
        gridItem: HomeScreenGridItem,
        isOnAllAppsFragment: Boolean,
    ) {
        binding.homeScreenGrid.root.hideResizeLines()
        mLongPressedIcon = gridItem
        val anchorY = if (isOnAllAppsFragment || gridItem.type == ITEM_TYPE_WIDGET) {
            val iconSize = realScreenSize.x / config.drawerColumnCount
            y - iconSize / 2f
        } else {
            val clickableRect = binding.homeScreenGrid.root.getClickableRect(gridItem)
            clickableRect.top.toFloat() - binding.homeScreenGrid.root.getCurrentIconSize() / 2f
        }

        binding.homeScreenPopupMenuAnchor.x = x
        binding.homeScreenPopupMenuAnchor.y = anchorY

        if (mOpenPopupMenu == null) {
            mOpenPopupMenu = handleGridItemPopupMenu(
                anchorView = binding.homeScreenPopupMenuAnchor,
                gridItem = gridItem,
                isOnAllAppsFragment = isOnAllAppsFragment,
                listener = menuListener
            )
        }
    }

    fun widgetLongPressedOnList(gridItem: HomeScreenGridItem) {
        mLongPressedIcon = gridItem
        hideFragment(binding.widgetsFragment)
        binding.homeScreenGrid.root.itemDraggingStarted(mLongPressedIcon!!)
    }

    enum class PopupPosition {
        TOUCH,
        CENTER
    }

    private fun showMainLongPressMenu(
        x: Float = 0f,
        y: Float = 0f,
        position: PopupPosition = PopupPosition.TOUCH,
    ) {
        if (isHomePopupShowing) {
            return
        }

        isHomePopupShowing = true
        binding.homeScreenGrid.root.hideResizeLines()

        binding.homeScreenPopupMenuAnchor.post {
            when (position) {
                PopupPosition.TOUCH -> {
                    binding.homeScreenPopupMenuAnchor.x = x
                    binding.homeScreenPopupMenuAnchor.y =
                        y - resources.getDimension(R.dimen.lc_long_press_anchor_button_offset_y) * 2
                }

                PopupPosition.CENTER -> {
                    val metrics = resources.displayMetrics
                    binding.homeScreenPopupMenuAnchor.x =
                        metrics.widthPixels / 2f - binding.homeScreenPopupMenuAnchor.width / 2f
                    binding.homeScreenPopupMenuAnchor.y =
                        metrics.heightPixels / 2f - binding.homeScreenPopupMenuAnchor.height / 2f
                }
            }

            val contextTheme = ContextThemeWrapper(this, getPopupMenuTheme())

            PopupMenu(
                contextTheme,
                binding.homeScreenPopupMenuAnchor,
                Gravity.TOP or Gravity.END
            ).apply {
                inflate(R.menu.menu_home_screen)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.widgets -> showWidgetsFragment()
                        R.id.wallpapers -> launchWallpapersIntent()
                    }
                    true
                }

                setOnDismissListener {
                    isHomePopupShowing = false
                }
                show()
            }
        }
    }


    private fun resetFragmentTouches() {

        binding.widgetsFragment.root.apply {
            touchDownY = -1
            ignoreTouches = false
        }

        binding.allAppsFragment.root.apply {
            touchDownY = -1
            ignoreTouches = false
        }
    }

    private fun showRightSideFragment() {
        if (isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded()) {
            return
        }

        prefs.edit().putBoolean("user_swiped_right", true).apply()

        binding.homeScreenGrid.homeScreenGrid.isVisible = false
        binding.rightSideFragment.root.refreshContent()

        val fragmentView = binding.rightSideFragment.root

        ObjectAnimator.ofFloat(fragmentView, "x", 0f).apply {
            duration = 300L
            interpolator = DecelerateInterpolator()
            start()
        }

        window.navigationBarColor = resources.getColor(R.color.lc_semitransparent_navigation)
        binding.homeScreenGrid.root.fragmentExpanded()
        binding.homeScreenGrid.root.hideResizeLines()
    }

    fun hideRightSideFragment() {


        (binding.rightSideFragment.bannerContainer.getChildAt(0) as? AdView)?.destroy()
        binding.rightSideFragment.bannerContainer.removeAllViews()

        binding.homeScreenGrid.homeScreenGrid.isVisible = true
        val fragmentView = binding.rightSideFragment.root
        val screenWidth = realScreenSize.x.toFloat()

        ObjectAnimator.ofFloat(fragmentView, "x", screenWidth).apply {
            duration = 300L
            interpolator = DecelerateInterpolator()
            start()
        }

        window.navigationBarColor = Color.TRANSPARENT
        binding.homeScreenGrid.root.fragmentCollapsed()

        updateStatusBarIcons(Color.TRANSPARENT)
    }


    private fun showWidgetsFragment() {
        showFragment(binding.widgetsFragment)
    }

    private fun renameItem(homeScreenGridItem: HomeScreenGridItem) {
        RenameItemDialog(this, homeScreenGridItem) {
            binding.homeScreenGrid.root.fetchGridItems()
        }
    }

    private fun launchWallpapersIntent() {
        try {
            if (!isNetworkAvailable(this@MainLauncherActivity) || adsData().Wallpaper_Array.isEmpty()) {
                Intent(Intent.ACTION_SET_WALLPAPER).apply {
                    startActivity(this)
                }
            } else {
                startActivity(
                    Intent(
                        this@MainLauncherActivity,
                        WallpaperListActivity::class.java
                    )
                )
            }
        } catch (_: ActivityNotFoundException) {
            toast(R.string.lc_no_app_found)
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }


    fun requestHomeRole() {
        if (isQPlus()) {
            startActivityForResult(
                roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME),
                REQUEST_SET_DEFAULT
            )
        }
    }

    val menuListener: ItemMenuListener = object : ItemMenuListener {
        override fun onAnyClick() {
            resetFragmentTouches()
        }

        override fun rename(gridItem: HomeScreenGridItem) {
            renameItem(gridItem)
        }

        override fun resize(gridItem: HomeScreenGridItem) {
            binding.homeScreenGrid.root.widgetLongPressed(gridItem)
        }

        override fun appInfo(gridItem: HomeScreenGridItem) {
            launchAppInfo(gridItem.packageName)
        }

        override fun remove(gridItem: HomeScreenGridItem) {
            binding.homeScreenGrid.root.removeAppIcon(gridItem)
        }

        override fun uninstall(gridItem: HomeScreenGridItem) {
            uninstallApp(gridItem.packageName)
        }

        override fun onDismiss() {
            mOpenPopupMenu = null
            resetFragmentTouches()
        }

        override fun beforeShow(menu: Menu) {
            var visibleMenuItems = 0
            for (item in menu.iterator()) {
                if (item.isVisible) {
                    visibleMenuItems++
                }
            }
            val yOffset =
                resources.getDimension(R.dimen.lc_long_press_anchor_button_offset_y) * (visibleMenuItems - 1)
            binding.homeScreenPopupMenuAnchor.y -= yOffset
        }
    }

    private class MyGestureListener(
        private val flingListener: FlingListener,
    ) : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(event: MotionEvent): Boolean {

            (flingListener as MainLauncherActivity).homeScreenClicked(event.x, event.y)
            return super.onSingleTapUp(event)
        }

        override fun onDoubleTap(event: MotionEvent): Boolean {
            (flingListener as MainLauncherActivity).homeScreenDoubleTapped(
                event.x,
                event.y
            )
            return super.onDoubleTap(event)
        }

        override fun onFling(
            event1: MotionEvent?,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float,
        ): Boolean {

            // ignore fling events just after releasing an icon from dragging
            if (System.currentTimeMillis() - mLastUpEvent < 500L) {

                return true
            }

            if (abs(velocityY) > abs(velocityX)) {
                if (velocityY > 0) {
                    flingListener.onFlingDown()
                } else {
                    flingListener.onFlingUp()
                }
            } else if (abs(velocityX) > abs(velocityY)) {
                if (velocityX > 0) {
                    flingListener.onFlingRight()
                } else {
                    flingListener.onFlingLeft()
                }
            }

            return true
        }

        override fun onLongPress(event: MotionEvent) {
            (flingListener as MainLauncherActivity).homeScreenLongPressed(event.x, event.y)
        }
    }

    override fun onFlingUp() {

        binding.ivSwipeHintre.apply {
            visibility = View.GONE

        }
        if (mIgnoreYMoveEvents) {
            return
        }

        if (!isWidgetsFragmentExpanded()) {
            mIgnoreUpEvent = true
            showFragment(binding.allAppsFragment)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onFlingDown() {
        if (mIgnoreYMoveEvents) {
            return
        }

        mIgnoreUpEvent = true
        if (isAllAppsFragmentExpanded()) {
            hideFragment(binding.allAppsFragment)
        } else if (isWidgetsFragmentExpanded()) {
            hideFragment(binding.widgetsFragment)
        } else {
            try {
                Class.forName("android.app.StatusBarManager")
                    .getMethod("expandNotificationsPanel")
                    .invoke(getSystemService("statusbar"))
            } catch (_: Exception) {
            }
        }
    }

    override fun onFlingRight() {
        if (mIgnoreXMoveEvents) {
            return
        }

        if (isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded()) {
            return
        }

        if (isRightSideFragmentExpanded()) {
            mIgnoreUpEvent = true
            hideRightSideFragment()
            return
        }

        val currentPage = binding.homeScreenGrid.root.currentPage
        if (currentPage == 0) {
            mIgnoreUpEvent = true
            binding.ivSwipeHintre.apply {
                visibility = View.GONE

            }
            AdsManager.showInterstitialLauncher(this@MainLauncherActivity, false) {
                val repository = SmsRepository(this@MainLauncherActivity)
                if (repository.isDefaultSmsApp()) {

                    startActivity(Intent(this, Spidy_MainActivity::class.java).apply {
                        flags = 335544320
                    })
                    overridePendingTransition(R.anim.lc_slide_in_left, R.anim.lc_slide_out_right)
                } else {

                    startActivity(Intent(this, DefaultSmsActivity::class.java).apply {
                        flags = 335544320
                    })
                    overridePendingTransition(R.anim.lc_slide_in_left, R.anim.lc_slide_out_right)
                }

            }

        } else {
            mIgnoreUpEvent = true
            binding.homeScreenGrid.root.prevPage(redraw = true)
        }
    }


    override fun onFlingLeft() {

        binding.ivSwipeHintre.apply {
            visibility = View.GONE

        }
        if (mIgnoreXMoveEvents) return

        if (isAllAppsFragmentExpanded() || isWidgetsFragmentExpanded()) {
            return
        }

        if (binding.rightSideFragment.root.x < realScreenSize.x) {
            return
        }

        if (isRightSideFragmentExpanded()) return


        val currentPage = binding.homeScreenGrid.root.currentPage
        val lastPageIndex = (binding.homeScreenGrid.root.maxPages - 1).coerceAtLeast(0)

        if (currentPage >= lastPageIndex) {
            mIgnoreUpEvent = true
            showRightSideFragment()
        } else {
            mIgnoreUpEvent = true
            binding.homeScreenGrid.root.nextPage(redraw = true)
        }
    }


    @SuppressLint("WrongConstant")
    fun getAllAppLaunchers(): ArrayList<AppLauncher> {

        val allApps = ArrayList<AppLauncher>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val simpleLauncher = applicationContext.packageName
        val microG = "com.google.android.gms"
        val list = packageManager.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED)
        for (info in list) {
            val componentInfo = info.activityInfo.applicationInfo
            val packageName = componentInfo.packageName
            if (packageName == simpleLauncher || packageName == microG) {
                continue
            }

            val activityName = info.activityInfo.name

            val label = info.loadLabel(packageManager).toString()
            val drawable =
                info.loadIcon(packageManager)?.constantState
                    ?.newDrawable()
                    ?.mutate()
                    ?: continue
//            val bitmap = drawable.toBitmap(
//                width = max(drawable.intrinsicWidth, 1),
//                height = max(drawable.intrinsicHeight, 1),
//                config = Bitmap.Config.ARGB_8888
//            )
//            val placeholderColor = calculateAverageColor(drawable)
            val placeholderColor = Color.WHITE
            allApps.add(
                AppLauncher(
                    id = null,
                    title = label,
                    packageName = packageName,
                    activityName = activityName,
                    order = 0,
                    thumbnailColor = placeholderColor,
//                    drawable = bitmap.toDrawable(resources)
                    drawable = drawable.constantState?.newDrawable()?.mutate() ?: drawable
                )
            )
        }

        launchersDB.insertAll(allApps)

        if (recentAppsDB.getRecentApps().isEmpty()) {
            val recentApps = allApps
                .take(4)
                .mapIndexed { index, app ->
                    app.toRecentApps().copy(order = index + 1)
                }
            recentAppsDB.insertAll(recentApps)
        }

        return allApps
    }


    private val POPULAR_APPS_PRIORITY = listOf(
        listOf("com.whatsapp"),
        listOf("com.facebook.katana"),
        listOf("com.instagram.android"),
        listOf("com.facebook.orca"),
        listOf("com.android.chrome", "com.google.android.chrome"),
        listOf("com.google.android.youtube", "com.google.android.apps.youtube.music"),
        listOf("com.twitter.android", "com.zhiliaoapp.musically")
    )

    private fun resolveAppByIntent(intent: Intent): String? {
        return packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .firstOrNull()
            ?.activityInfo
            ?.packageName
    }

    private fun getBrowserPackage(): String? {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "http://www.example.com".toUri()
        ).addCategory(Intent.CATEGORY_BROWSABLE)

        return resolveAppByIntent(intent)
    }

    private fun getCameraPackage(): String? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        return resolveAppByIntent(intent)
    }

    private fun resolvePopularOrIntentApp(
        appLaunchers: List<AppLauncher>,
        preferredPackages: List<String>,
        intentFallback: () -> String?,
    ): AppLauncher? {

        preferredPackages.forEach { pkg ->
            appLaunchers.firstOrNull { it.packageName == pkg }?.let {
                return it
            }
        }

        val intentPkg = intentFallback()
        return appLaunchers.firstOrNull { it.packageName == intentPkg }
    }

    private fun resolveTopRowApps(
        appLaunchers: List<AppLauncher>,
    ): List<AppLauncher> {

        val resolved = mutableListOf<AppLauncher>()

        // WhatsApp / FB / Insta / Messenger
        POPULAR_APPS_PRIORITY.forEach { pkgGroup ->
            val app = appLaunchers.firstOrNull { it.packageName in pkgGroup }
            if (app != null) {
                resolved.add(app)
            }
            if (resolved.size == 4) return resolved
        }

        // Browser fallback
        if (resolved.size < 4) {
            resolvePopularOrIntentApp(
                appLaunchers,
                listOf("com.android.chrome", "com.google.android.chrome"),
                ::getBrowserPackage
            )?.let { resolved.add(it) }
        }

        return resolved.distinctBy { it.packageName }.take(4)
    }

    private fun getDefaultAppPackages(appLaunchers: ArrayList<AppLauncher>) {

        val homeScreenGridItems = mutableListOf<HomeScreenGridItem>()

        val dockRow = config.homeRowCount - 2
        val topRow = config.homeRowCount - 3

        /* -------------------- DOCK APPS -------------------- */

        fun addDockApp(position: Int, packageName: String?) {
            if (packageName == null) return
            val launcher = appLaunchers.firstOrNull { it.packageName == packageName } ?: return

            homeScreenGridItems.add(
                HomeScreenGridItem(
                    id = null,
                    left = position,
                    top = dockRow,
                    right = position,
                    bottom = dockRow,
                    page = 0,
                    packageName = packageName,
                    activityName = "",
                    title = launcher.title,
                    type = ITEM_TYPE_ICON,
                    className = "",
                    widgetId = -1,
                    shortcutId = "",
                    icon = null,
                    docked = true,
                    parentId = null
                )
            )
        }

        //TODO BOTTOM DOCKED LINE
        //TODO PHONE APP
        var phoneAppLauncher: AppLauncher? = null

        try {
            try {
                val telecomManager = getSystemService(TELECOM_SERVICE) as? TelecomManager
                val defaultDialerPackage = telecomManager?.defaultDialerPackage
                if (!defaultDialerPackage.isNullOrEmpty()) {
                    phoneAppLauncher =
                        appLaunchers.firstOrNull { it.packageName == defaultDialerPackage }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (phoneAppLauncher == null) {
                val phonePackages = listOf(
                    "com.android.dialer",
                    "com.google.android.dialer",
                    "com.samsung.android.dialer"
                )

                phoneAppLauncher = appLaunchers.find {
                    it.packageName in phonePackages || it.title.contains("Phone", true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addDockApp(0, phoneAppLauncher?.packageName)


        //TODO SMS APP
        var messageAppLauncher: AppLauncher? = null

        try {
            try {
                val defaultSmsPackage =
                    Telephony.Sms.getDefaultSmsPackage(this)

                if (!defaultSmsPackage.isNullOrEmpty()) {
                    messageAppLauncher =
                        appLaunchers.firstOrNull { it.packageName == defaultSmsPackage }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (messageAppLauncher == null) {
                val messagePackages = listOf(
                    "com.google.android.apps.messaging",
                    "com.android.messaging",
                    "com.samsung.android.messaging"
                )

                messageAppLauncher = appLaunchers.find {
                    it.packageName in messagePackages ||
                            it.title.contains("Message", true) ||
                            it.title.contains("SMS", true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addDockApp(1, messageAppLauncher?.packageName)


        //TODO GALLERY APP
        var galleryAppLauncher: AppLauncher? = null

        try {
            // 1️⃣ Check an expanded list of popular Gallery packages (Covers 95% of modern devices)
            val knownGalleryPackages = listOf(
                "com.google.android.apps.photos",     // Google Photos (Default on Pixel, Nothing, Moto)
                "com.sec.android.gallery3d",          // Samsung
                "com.miui.gallery",                   // Xiaomi / POCO / Redmi
                "com.coloros.gallery3d",              // Oppo / Realme
                "com.oplus.gallery",                  // OnePlus (Newer OS)
                "com.oneplus.gallery",                // OnePlus (Older OS)
                "com.vivo.gallery",                   // Vivo / iQOO
                "com.huawei.gallery",                 // Huawei / Honor
                "com.motorola.gallery",               // Motorola (Custom)
                "com.sonyericsson.album",             // Sony
                "com.asus.gallery",                   // Asus
                "com.lge.gallery",                    // LG
                "com.android.gallery3d"               // AOSP Fallback
            )

            galleryAppLauncher = appLaunchers.find { it.packageName in knownGalleryPackages }

            // 2️⃣ DYNAMIC FALLBACK: Ask the OS for apps that handle Images (Crucial for unknown OEMs)
            if (galleryAppLauncher == null) {
                // Requires a Context. Assuming 'context' is available here.
                val pm = packageManager

                // Create an intent that views images
                val imageIntent = Intent(Intent.ACTION_VIEW).apply { type = "image/*" }

                // Query the OS for all apps that can handle this intent
                val resolveInfos = pm.queryIntentActivities(imageIntent, 0)

                // Sort results to prioritize Built-in System Apps over downloaded 3rd party apps
                val dynamicallyFoundPackages = resolveInfos.sortedByDescending {
                    it.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM
                }.map { it.activityInfo.packageName }

                // Find the highest priority system image viewer in your appLaunchers list
                galleryAppLauncher =
                    appLaunchers.find { it.packageName in dynamicallyFoundPackages }
            }

            // 3️⃣ LAST RESORT FALLBACK: Check by title (Beware: this fails on non-English devices)
            if (galleryAppLauncher == null) {
                galleryAppLauncher = appLaunchers.find { launcher ->
                    val title = launcher.title.lowercase()
                    title.contains("gallery") || title.contains("photos") || title.contains("album")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        // 3️⃣ Safe call (won’t crash)
        addDockApp(2, galleryAppLauncher?.packageName)


        //TODO CAMERA APP
        var cameraAppLauncher: AppLauncher? = null

        try {
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val resolveInfo = packageManager.resolveActivity(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                val defaultCameraPackage = resolveInfo?.activityInfo?.packageName

                if (!defaultCameraPackage.isNullOrEmpty()) {
                    cameraAppLauncher =
                        appLaunchers.firstOrNull { it.packageName == defaultCameraPackage }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (cameraAppLauncher == null) {
                val cameraPackages = listOf(
                    "com.google.android.GoogleCamera",
                    "com.sec.android.app.camera",
                    "com.android.camera"
                )

                cameraAppLauncher = appLaunchers.firstOrNull {
                    it.packageName in cameraPackages
                }
            }

            if (cameraAppLauncher == null) {
                cameraAppLauncher = appLaunchers.find {
                    it.title.contains("Camera", true)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        addDockApp(3, cameraAppLauncher?.packageName)


        //TODO Ads Folder
        try {
            homeScreenGridItemsDB.insert(
                HomeScreenGridItem(
                    id = null,
                    left = 4,
                    top = dockRow,
                    right = 4,
                    bottom = dockRow,
                    page = 0,
                    packageName = "ads_folder",
                    activityName = "",
                    title = "Tools",
                    type = ITEM_TYPE_FOLDER,
                    className = "",
                    widgetId = -1,
                    shortcutId = "",
                    icon = null,
                    docked = true,
                    parentId = null
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


        //TODO Top ROW
        /* -------------------- TOP ROW POPULAR APPS -------------------- */
        try {
            var hasGoogleFolder = false

            /* -------------------- GOOGLE FOLDER (FIXED POSITION) -------------------- */

            val googleApps = appLaunchers
                .filter {
                    it.packageName.startsWith("com.google.") &&
                            it.packageName !in listOf(
                        "com.google.android.gms",
                        "com.google.android.gsf"
                    )
                }
                .sortedBy { it.title }

            if (googleApps.isNotEmpty()) {
                hasGoogleFolder = true

                val googleFolderId = homeScreenGridItemsDB.insert(
                    HomeScreenGridItem(
                        id = null,
                        left = 3,
                        top = topRow,
                        right = 3,
                        bottom = topRow,
                        page = 0,
                        packageName = "google_folder",
                        activityName = "",
                        title = "Google",
                        type = ITEM_TYPE_FOLDER,
                        className = "",
                        widgetId = -1,
                        shortcutId = "",
                        icon = null,
                        docked = false,
                        parentId = null
                    )
                )

                homeScreenGridItemsDB.insertAll(
                    googleApps.take(9).mapIndexed { index, app ->
                        HomeScreenGridItem(
                            id = null,
                            left = index,
                            top = 0,
                            right = index,
                            bottom = 0,
                            page = 0,
                            packageName = app.packageName,
                            activityName = app.activityName,
                            title = app.title,
                            type = ITEM_TYPE_ICON,
                            className = "",
                            widgetId = -1,
                            shortcutId = "",
                            icon = null,
                            docked = false,
                            parentId = googleFolderId
                        )
                    }
                )
            }

            /* -------------------- FINAL INSERT -------------------- */

            val topApps = resolveTopRowApps(appLaunchers)

            topApps.forEachIndexed { index, app ->
                // If index is 0, 1, 2 -> keep as is.
                // If index is 3 -> move to 4 (jump over Google folder).
                val gridPosition = if (index >= 3 && hasGoogleFolder) index + 1 else index

                // Safety check to ensure we don't go out of bounds if config is small
                if (gridPosition < 5) {
                    homeScreenGridItems.add(
                        HomeScreenGridItem(
                            id = null,
                            left = gridPosition,
                            top = topRow,
                            right = gridPosition,
                            bottom = topRow,
                            page = 0,
                            packageName = app.packageName,
                            activityName = app.activityName,
                            title = app.title,
                            type = ITEM_TYPE_ICON,
                            className = "",
                            widgetId = -1,
                            shortcutId = "",
                            icon = null,
                            docked = false,
                            parentId = null
                        )
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        homeScreenGridItemsDB.insertAll(homeScreenGridItems)
    }

    fun handleWidgetBinding(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        appWidgetInfo: AppWidgetProviderInfo,
        callback: (canBind: Boolean) -> Unit,
    ) {
        mActionOnCanBindWidget = null
        val canCreateWidget =
            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, appWidgetInfo.provider)
        if (canCreateWidget) {
            callback(true)
        } else {
            mActionOnCanBindWidget = callback
            Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, appWidgetInfo.provider)
                startActivityForResult(this, REQUEST_ALLOW_BINDING_WIDGET)
            }
        }
    }

    fun handleWidgetConfigureScreen(
        appWidgetHost: AppWidgetHost,
        appWidgetId: Int,
        callback: (canBind: Boolean) -> Unit,
    ) {
        mActionOnWidgetConfiguredWidget = callback
        appWidgetHost.startAppWidgetConfigureActivityForResult(
            this,
            appWidgetId,
            0,
            REQUEST_CONFIGURE_WIDGET,
            null
        )
    }

    fun handleShorcutCreation(
        activityInfo: ActivityInfo,
        callback: (shortcutId: String, label: String, icon: Drawable) -> Unit,
    ) {
        mActionOnAddShortcut = callback
        val componentName = ComponentName(activityInfo.packageName, activityInfo.name)
        Intent(Intent.ACTION_CREATE_SHORTCUT).apply {
            component = componentName
            startActivityForResult(this, REQUEST_CREATE_SHORTCUT)
        }
    }

    private fun updateStatusBarIcons(backgroundColor: Int = getProperBackgroundColor()) {
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars =
            backgroundColor.getContrastColor() == DARK_GREY
    }

//    private fun calculateAverageColor(bitmap: Bitmap): Int {
//        var red = 0
//        var green = 0
//        var blue = 0
//        val height = bitmap.height
//        val width = bitmap.width
//        var n = 0
//        val pixels = IntArray(width * height)
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
//        var i = 0
//        while (i < pixels.size) {
//            val color = pixels[i]
//            red += Color.red(color)
//            green += Color.green(color)
//            blue += Color.blue(color)
//            n++
//            i += 1
//        }
//
//        return Color.rgb(red / n, green / n, blue / n)
//    }

    private fun calculateAverageColor(drawable: Drawable): Int {
        val size = 16 // small = fast + safe
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        var red = 0
        var green = 0
        var blue = 0
        var count = 0

        val pixels = IntArray(size * size)
        bitmap.getPixels(pixels, 0, size, 0, 0, size, size)

        for (color in pixels) {
            val alpha = Color.alpha(color)
            if (alpha < 50) continue // skip transparent pixels

            red += Color.red(color)
            green += Color.green(color)
            blue += Color.blue(color)
            count++
        }

        bitmap.recycle()

        if (count == 0) return Color.GRAY

        return Color.rgb(
            red / count,
            green / count,
            blue / count
        )
    }


    private fun manageLongPressAction(x: Float = 0f, y: Float = 0f) {
        when (config.getGestureAction(GestureType.TAP_HOLD)) {
            GestureAction.NONE -> Unit
            GestureAction.OPEN_APP_DRAWER -> openAppDrawer()
            GestureAction.OPEN_LAUNCHER_SETTING -> openLauncherSetting()
            GestureAction.OPEN_HOME_POPUP -> showMainLongPressMenu(
                x = x,
                y = y,
                position = PopupPosition.TOUCH
            )

            GestureAction.OPEN_GLOBAL_SEARCH -> openGlobalSearch()
        }
    }

    private fun manageBackPressAction() {
        when (config.getGestureAction(GestureType.BACK_BUTTON)) {
            GestureAction.NONE -> Unit
            GestureAction.OPEN_APP_DRAWER -> openAppDrawer()
            GestureAction.OPEN_LAUNCHER_SETTING -> openLauncherSetting()
            GestureAction.OPEN_HOME_POPUP -> showHomeScreenPopup()
            GestureAction.OPEN_GLOBAL_SEARCH -> openGlobalSearch()
        }
    }

    private fun manageDoublePressAction(x: Float = 0f, y: Float = 0f) {
        when (config.getGestureAction(GestureType.DOUBLE_TAP)) {
            GestureAction.NONE -> Unit
            GestureAction.OPEN_APP_DRAWER -> openAppDrawer()
            GestureAction.OPEN_LAUNCHER_SETTING -> openLauncherSetting()
            GestureAction.OPEN_HOME_POPUP -> showMainLongPressMenu(
                x = x,
                y = y,
                position = PopupPosition.TOUCH
            )

            GestureAction.OPEN_GLOBAL_SEARCH -> openGlobalSearch()
        }
    }

    private fun manageHomePressAction() {
        when (config.getGestureAction(GestureType.HOME_BUTTON)) {
            GestureAction.NONE -> Unit
            GestureAction.OPEN_APP_DRAWER -> openAppDrawer()
            GestureAction.OPEN_LAUNCHER_SETTING -> openLauncherSetting()
            GestureAction.OPEN_HOME_POPUP -> showHomeScreenPopup()
            GestureAction.OPEN_GLOBAL_SEARCH -> openGlobalSearch()
        }
    }

    private fun setupHomePressWatcher() {
        launcherHomeWatcher = LauncherHomePressWatcher(this)
        launcherHomeWatcher?.setOnHomePressedListener {
            if (isActivityVisible) {
                manageHomePressAction()
            }
        }
        launcherHomeWatcher?.startWatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        launcherHomeWatcher?.stopWatch()
    }


    fun showInterstitialLauncher(
        activity: Activity,
        showLoader: Boolean = false,
        onComplete: () -> Unit,
    ) {
        if (adsData().INTER_LINKED_SHOW.isNullOrEmpty()) {

            adsData().INTER_LINKED_SHOW = arrayListOf(
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L",
                "A",
                "L",
                "L",
                "L",
                "I",
                "L",
                "L",
                "L",
                "N",
                "L",
                "L",
                "L"
            )
        }
        try {
            if (adsData().Ads_status && isNetworkAvailable(activity)) {

                if (!shouldShowAd()) {
                    onComplete()
                    return
                }
                if (adsData().INTER_LINKED_SHOW != null && adsData().INTER_LINKED_SHOW.size > 0) {

                    if (INTER_LINKED_COUNT_RECYCLE >= adsData().INTER_LINKED_SHOW.size) {
                        INTER_LINKED_COUNT_RECYCLE = 0   // global
                    }
                }
                Log.e("xxxxxlllladd11", adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE])

                if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "L",
                        ignoreCase = true
                    )
                ) {

                    onComplete()

                    adsData().Interstitial_All_Fail_Web_Array
                        .randomOrNull()
                        ?.let { openCustomTabIntent1(activity, it) }


                    if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                        INTER_LINKED_COUNT_RECYCLE = 0
                        load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                    } else {
                        INTER_LINKED_COUNT_RECYCLE++
                        load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                    }

                    return
                } else if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "I",
                        ignoreCase = true
                    )
                ) {

                    if (adsData().Interstitial_All.isEmpty()) {


                        onComplete()

                        adsData().Interstitial_All_Fail_Web_Array
                            .randomOrNull()
                            ?.let { openCustomTabIntent1(activity, it) }



                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        }

                        return
                    }


                    GAds.showInterstitialLauncher(
                        activity,
                        adsData(),
                        object : GAds.OnAdAction1 {
                            override fun onResult(success: Boolean) {
                                if (adsData().INTER_LINKED_SHOW.isNullOrEmpty()) {
                                    onComplete()
                                    return
                                }


                                onComplete()

                                if (!success) {
                                    adsData().Interstitial_All_Fail_Web_Array
                                        .randomOrNull()
                                        ?.let { openCustomTabIntent1(activity, it) }
                                }



                                if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                                    INTER_LINKED_COUNT_RECYCLE = 0
                                    load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                                } else {
                                    INTER_LINKED_COUNT_RECYCLE++
                                    load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                                }
                                return
                            }
                        },
                        showLoader = showLoader
                    )
                } else if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "A",
                        ignoreCase = true
                    )
                ) {


                    if (adsData().Appopen_All.isEmpty() || GAds.appOpen?.isAdAvailable() == false) {
                        onComplete()

                        adsData().Interstitial_All_Fail_Web_Array
                            .randomOrNull()
                            ?.let { openCustomTabIntent1(activity, it) }

                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        }

                        AdCycleController.nextStep()
                        return
                    }

                    GAds.appOpen?.showAppOpenAd(object : GAppOpen.AppOpenResult {
                        override fun isAppOpenAdShown(shown: Boolean) {

                            onComplete()

                            if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                                INTER_LINKED_COUNT_RECYCLE = 0
                                load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                            } else {
                                INTER_LINKED_COUNT_RECYCLE++
                                load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                            }

                            AdCycleController.nextStep()
                            return
                        }
                    })
                } else if (adsData().INTER_LINKED_SHOW[INTER_LINKED_COUNT_RECYCLE].equals(
                        "N",
                        ignoreCase = true
                    )
                ) {


                    if (adsData().Native_Interstitial.isEmpty() || !isNativeLoaded("FullScreenNative")) {
                        onComplete()

                        adsData().Interstitial_All_Fail_Web_Array
                            .randomOrNull()
                            ?.let { openCustomTabIntent1(activity, it) }

                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        }

                        return
                    }


                    FullscreenNativeDialog(activity) { success ->
                        onComplete()

                        if (!success) {
                            adsData().Interstitial_All_Fail_Web_Array
                                .randomOrNull()
                                ?.let { openCustomTabIntent1(activity, it) }
                        }

                        if (INTER_LINKED_COUNT_RECYCLE == INTER_LINKED_COUNT - 1) {
                            INTER_LINKED_COUNT_RECYCLE = 0
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        } else {
                            INTER_LINKED_COUNT_RECYCLE++
                            load_next_ad(INTER_LINKED_COUNT_RECYCLE, activity)

                        }
                        AdCycleController.nextStep()
                    }.show()

                    return
                } else {
                    onComplete()
                    return
                }
            } else {
                onComplete()
                return
            }
        } catch (e: Exception) {
            onComplete()
        }

    }

    fun launchAppFromDrawer(app: AppLauncher) {
        val gridItem = HomeScreenGridItem(
            id = null,
            left = -1,
            top = -1,
            right = -1,
            bottom = -1,
            page = 0,
            packageName = app.packageName,
            activityName = app.activityName,
            title = app.title,
            type = ITEM_TYPE_ICON,
            className = "",
            widgetId = -1,
            shortcutId = "",








            icon = null,
            docked = false,
            parentId = null,
            drawable = app.drawable
        )

        this.pendingGridItem = gridItem
        Log.e("#APP_CLICK_SHOW_AD", adsData().APP_CLICK_SHOW_AD.toString())
        if (adsData().APP_CLICK_SHOW_AD) {
            showInterstitialLauncher(
                activity = this,
                showLoader = false,
                onComplete = {
                    executePendingAppLaunch()
                }
            )
        } else {
            executePendingAppLaunch()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun checkAndMakeIntentToMain() {
        if (isDefaultLauncher()) {
            if (!config.tutorialShow) {
                AdCycleController.reset()


                config.tutorialShow = true


                if (adsData().INTRO_SCREEN) {
                    AdsManager.showInterstitialLauncher(this, false) {
                        startActivity(Intent(this, Spidy_LanguageActivity::class.java).apply {
                            putExtra("isFromSplash", true)
                            flags = 335544320
                        })
                    }
                } else {
                    val repository = SmsRepository(this@MainLauncherActivity)
                    if (repository.isDefaultSmsApp()) {
                        startActivity(Intent(this, Spidy_MainActivity::class.java).apply {
                            putExtra("isFromSplash", true)
                            flags = 335544320
                        })
                    } else {

                        startActivity(Intent(this, DefaultSmsActivity::class.java).apply {
                            putExtra("isFromSplash", true)
                            flags = 335544320
                        })
                    }
                }

                return
            } else {
                return
            }
        } else {

            if (adsData().Launcher_ON_OFF_ADDRESS.isEmpty()) {
                if (adsData().Organic_Marketing_Both) {
                    if (adsData().INTRO_SCREEN) {
                        startActivity(Intent(this, Spidy_LanguageActivity::class.java).apply {
                            putExtra("isFromSplash", true)
                            flags = 335544320
                        })
                    } else {

                        val repository = SmsRepository(this@MainLauncherActivity)
                        if (repository.isDefaultSmsApp()) {
                            startActivity(Intent(this, Spidy_MainActivity::class.java).apply {
                                putExtra("isFromSplash", true)
                                flags = 335544320
                            })
                        } else {
                            startActivity(Intent(this, DefaultSmsActivity::class.java).apply {
                                putExtra("isFromSplash", true)
                                flags = 335544320
                            })
                        }
                    }
                } else {
                    startActivity(
                        Intent(
                            this@MainLauncherActivity,
                            SetDefaultLauncherActivity::class.java
                        )
                    )
                }
            } else {
                getLocationFromIP()
            }
        }
    }
    
    
    fun getLocationFromIP() {
        Thread {
            try {
                val url = URL("http://ip-api.com/json")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                val state = json.getString("regionName")

                runOnUiThread {
                    val telephonyManager =
                        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    val countryCode = telephonyManager.simCountryIso
                    val locale = Locale("", countryCode)
                    val countryName = locale.displayCountry


                    val target = adsData().Launcher_ON_OFF_ADDRESS?.trim()?.lowercase()
                    val stateValue = state?.trim()?.lowercase()
                    val countryValue = countryName?.trim()?.lowercase()

                    if (stateValue == target || countryValue == target) {
                        if (adsData().INTRO_SCREEN) {
                            startActivity(Intent(this, Spidy_LanguageActivity::class.java))
                            finish()
                        } else {

                            val repository = SmsRepository(this@MainLauncherActivity)
                            if (repository.isDefaultSmsApp()) {
                                startActivity(Intent(this, Spidy_MainActivity::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this, DefaultSmsActivity::class.java))
                                finish()
                            }
                        }

                    } else {
                        if (adsData().Organic_Marketing_Both) {
                            if (adsData().INTRO_SCREEN) {
                                startActivity(
                                    Intent(
                                        this,
                                        Spidy_LanguageActivity::class.java
                                    ).apply {
                                        putExtra("isFromSplash", true)
                                        flags = 335544320
                                    })
                            } else {
                                startActivity(
                                    Intent(
                                        this,
                                        Spidy_MainActivity::class.java
                                    ).apply {
                                        putExtra("isFromSplash", true)
                                        flags = 335544320
                                    })
                            }
                        } else {
                            startActivity(
                                Intent(
                                    this@MainLauncherActivity,
                                    SetDefaultLauncherActivity::class.java
                                )
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }



    private fun setNavigationBarColor(isDark: Boolean) {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val insetsController = WindowCompat.getInsetsController(this, decorView)
            insetsController.isAppearanceLightNavigationBars = !isDark
            insetsController.isAppearanceLightStatusBars = !isDark
        }
    }


    private fun startPackageMonitoring(context: Context) {
        packageChangeReceiver = Codebizz_PackageChangeReceiver()
        // Add listeners if needed
        packageChangeReceiver!!.addListener(object :
            Codebizz_PackageChangeReceiver.PackageChangeListener {
            override fun onPackageInstalled(packageName: String) {
                handlePackageInstalled(context, packageName)
            }

            override fun onPackageUninstalled(packageName: String) {
                handlePackageUninstalled(context, packageName)
            }

            override fun onPackageUpdated(packageName: String) {
                handlePackageUpdated(context, packageName)
            }
        })


        registerPackageChangeReceiver()
    }

    private fun handlePackageInstalled(context: Context, packageName: String) {
        if (!Codebizz_PackageMonitorPrefManager.installScanEnabled) {
            return
        }

        launchInstallUninstallActivity(context, packageName, true)
    }


    private fun handlePackageUninstalled(context: Context, packageName: String) {
        if (!Codebizz_PackageMonitorPrefManager.uninstallScanEnabled) {
            return
        }

        // Your logic here

        launchInstallUninstallActivity(context, packageName, false)
    }


    private fun handlePackageUpdated(context: Context, packageName: String) {
        if (!Codebizz_PackageMonitorPrefManager.installScanEnabled) {
            return
        }

        // Your logic here
        launchInstallUninstallActivity(context, packageName, true)
    }

    fun launchInstallUninstallActivity(
        context: Context,
        packageName: String,
        isFromInstall: Boolean,
    ) {
        try {
            finishInstallUninstallActivity()
            val intent = Intent(context, Codebizz_InstallUninstallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("packageName", packageName)
                putExtra("isFromInstall", isFromInstall)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
        }
    }


    fun finishInstallUninstallActivity() {
        Codebizz_InstallUninstallActivity.installUninstallActivity?.finish()
    }

    private fun registerPackageChangeReceiver() {

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ requires explicit permission
                registerReceiver(
                    packageChangeReceiver,
                    filter,
                    RECEIVER_EXPORTED
                )
            } else {
                registerReceiver(packageChangeReceiver, filter)
            }
        } catch (e: Exception) {
        }
    }


    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _: ActivityResult ->
            binding.allAppsFragment.llUpdate.isVisible = false
        }


    private fun checkForUpdate() {
        try {
            val appUpdateManager = AppUpdateManagerFactory.create(this)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                binding.allAppsFragment.llUpdate.isVisible =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) && appUpdateInfo.availableVersionCode() > config.currentVersionCode
            }
        } catch (e: Exception) {
        }
    }

    private fun updateApp() {

        try {

            val appUpdateManager = AppUpdateManagerFactory.create(this)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE,
                    )
                ) {
                    appUpdateInfo?.let {
                        appUpdateManager.startUpdateFlowForResult(
                            it,
                            activityResultLauncher,
                            AppUpdateOptions
                                .newBuilder(AppUpdateType.IMMEDIATE)
                                .setAllowAssetPackDeletion(true)
                                .build(),
                        )

                        config.currentVersionCode = it.availableVersionCode()
                    } ?: run {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

}