package com.zone.messanger.text.PANDA_Activity


import android.app.AppOpsManager
import android.content.BroadcastReceiver
import android.content.Context
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.GoogleMobileAdsConsentManager
import com.zone.messanger.text.databinding.ActivityMainBinding

import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.isXiaomiDevice


import android.content.Intent;
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.zone.messanger.text.R;
import com.zone.messanger.text.Repository.SmsRepository

import com.zone.messanger.text.PANDA_ads.NativeType


import com.zone.messanger.text.PANDA_text.activity.ChatActivity
import com.zone.messanger.text.PANDA_text.activity.NewChatActivity
import com.zone.messanger.text.PANDA_text.activity.SettingsActivity
import com.zone.messanger.text.PANDA_text.adapter.ConversationAdapter
import com.zone.messanger.text.PANDA_text.model.Conversation
import com.zone.messanger.text.PANDA_text.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Spidy_MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    lateinit var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    var permissionCount = 0

    private lateinit var rfClient: InstallReferrerClient

    private val onOpChangedListener =
        object : AppOpsManager.OnOpChangedListener {
            override fun onOpChanged(
                op: String,
                pkg: String,
            ) {
                val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
                val mode =
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
                        android.os.Process.myUid(),
                        this@Spidy_MainActivity.packageName,
                    )
                if (mode == AppOpsManager.MODE_ALLOWED) {
                    appOps.stopWatchingMode(this)
                    runOnUiThread {

                        val intent =
                            Intent(this@Spidy_MainActivity, Spidy_MainActivity::class.java)

                        if (!isXiaomiDevice()) {
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
        }













    //############ TExt


    private lateinit var repository: SmsRepository
    private lateinit var adapter: ConversationAdapter

    private var allConversations: List<Conversation> = emptyList()
    private var currentFilterId: Int = R.id.chipAll
    private var currentQuery: String = ""

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            updatePermissionBanner()
            loadConversations()
        }

    private val messageReceivedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadConversations()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = SmsRepository(this)

        Log.e("#sdasdasdadasq","asdas")



        rfClient =
            InstallReferrerClient.newBuilder(this).build()

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _: ActivityResult ->

            }

        onBackPressedDispatcher.addCallback(this) {
            Thread {
                getInsRClient(rfClient)
            }.start()
        }
        Log.e("#sdasdasdadasq","111")

        if (AdsManager.isNetworkAvailable(this)) {
            checkForUpdate()
            if (!config.isConsentChecked) {
                requestConsent()
            }
        }

        Log.e("#sdasdasdadasq","22222")


        AdsManager.showNative(
            this,
            container = findViewById(R.id.nativeContainer),
            nativeType = NativeType.SMALL_NATIVE_BANNER,
            adId = adsData().Native_All_Left_Side_id,
            uniqueKey = this::class.java.simpleName
        )



        ////////////########## Text
        Log.e("#sdasdasdadasq","3333")

        setupRecyclerView()
        Log.e("#sdasdasdadasq","666")

        setupSearch()
        Log.e("#sdasdasdadasq","777")

        setupFilters()
        Log.e("#sdasdasdadasq","8888")

        setupClicks()
        Log.e("#sdasdasdadasq","55555")

        if (!PermissionUtils.hasCorePermissions(this)) {
            permissionLauncher.launch(PermissionUtils.SMS_PERMISSIONS)
        }
        Log.e("#sdasdasdadasq","4444")

    } override fun onStart() {
        super.onStart()
        val filter = IntentFilter("com.first.sms.sms.MESSAGE_RECEIVED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(messageReceivedReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(messageReceivedReceiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        runCatching { unregisterReceiver(messageReceivedReceiver) }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionBanner()
        loadConversations()
    }

    private fun setupRecyclerView() {
        adapter = ConversationAdapter(
            onClick = { conversation -> openChat(conversation.threadId, conversation.address) },
            onLongClick = { conversation -> confirmDelete(conversation); true }
        )
        binding.recyclerConversations.layoutManager = LinearLayoutManager(this)
        binding.recyclerConversations.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener {
            loadConversations()
        }
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            val visible = binding.searchBarContainer.visibility == android.view.View.VISIBLE
            binding.searchBarContainer.visibility =
                if (visible) android.view.View.GONE else android.view.View.VISIBLE
            if (!visible) binding.edtSearch.requestFocus()
        }
        binding.edtSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString().orEmpty()
                applyFilters()
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilterId = checkedIds.firstOrNull() ?: R.id.chipAll
            applyFilters()
        }
    }

    private fun setupClicks() {
        binding.btnSettings.setOnClickListener {
            AdsManager.showInterstitialLauncher(this, false) {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        binding.fabNewChat.setOnClickListener {
            AdsManager.showInterstitialLauncher(this, false) {
                startActivity(Intent(this, NewChatActivity::class.java))
            }
        }
        binding.btnGrantPermissions.setOnClickListener {
            permissionLauncher.launch(PermissionUtils.SMS_PERMISSIONS)
        }
    }

    private fun updatePermissionBanner() {
        binding.permissionBanner.visibility =
            if (PermissionUtils.hasCorePermissions(this)) android.view.View.GONE
            else android.view.View.VISIBLE
    }

    private fun loadConversations() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            val conversations = withContext(Dispatchers.IO) { repository.getConversations() }
            allConversations = conversations
            applyFilters()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun applyFilters() {
        var list = allConversations

        list = when (currentFilterId) {
            R.id.chipPersonal -> list.filter { !it.looksTransactional() }
            R.id.chipTransaction -> list.filter { it.looksTransactional() }
            R.id.chipUnread -> list.filter { it.unreadCount > 0 }
            else -> list
        }

        if (currentQuery.isNotBlank()) {
            val q = currentQuery.trim().lowercase()
            list = list.filter {
                it.displayName.lowercase().contains(q) ||
                        it.address.lowercase().contains(q) ||
                        it.snippet.lowercase().contains(q)
            }
        }

        adapter.submitList(list)
        binding.emptyState.visibility =
            if (list.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun openChat(threadId: Long, address: String) {

        AdsManager.showInterstitialLauncher(this, false) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_THREAD_ID, threadId)
            intent.putExtra(ChatActivity.EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }

    private fun confirmDelete(conversation: Conversation) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(conversation.displayName)
            .setMessage(R.string.delete_conversation)
            .setPositiveButton(R.string.delete_conversation) { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { repository.deleteThread(conversation.threadId) }
                    loadConversations()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }



    override fun onDestroy() {
        super.onDestroy()
        try {
            val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            appOps.stopWatchingMode(onOpChangedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



























    private fun getInsRClient(rfClient: InstallReferrerClient) {
        if (adsData().Organic_Marketing_Both) {
            rfClient.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {

                    when (responseCode) {

                        InstallReferrerClient.InstallReferrerResponse.OK -> {

                            try {

                                val response: ReferrerDetails =
                                    rfClient.installReferrer

                                val rUrltemp =
                                    response.installReferrer

                                Log.e("ReferrerData3", rUrltemp)

                                // =========================
                                // ORGANIC / MARKETING CHECK
                                // =========================

                                var isMarketingUser = false

                                if (
                                    rUrltemp.isNotEmpty() &&
                                    (
                                            rUrltemp.contains("gclid", true) ||
                                                    rUrltemp.contains("fbclid", true) ||
                                                    rUrltemp.contains("utm_campaign", true)
                                            )
                                ) {

                                    isMarketingUser = true
                                    Log.e("USER_TYPE", "MARKETING USER")

                                } else {

                                    isMarketingUser = false
                                    Log.e("USER_TYPE", "ORGANIC USER")
                                }

                                // =========================
                                // REDIRECT
                                // =========================

                                if (isMarketingUser) {

                                    startActivity(
                                        Intent(
                                            this@Spidy_MainActivity,
                                            MainLauncherActivity::class.java
                                        ).apply {
                                            flags = 335544320
                                        }
                                    )
                                    overridePendingTransition(
                                        R.anim.lc_slide_in_right,
                                        R.anim.lc_slide_out_left
                                    )
                                    finish()

                                } else {

                                    finishAffinity()
                                }

                                finish()

                                rfClient.endConnection()

                            } catch (e: Exception) {

                                e.printStackTrace()

                                startActivity(
                                    Intent(
                                        this@Spidy_MainActivity,
                                        MainLauncherActivity::class.java
                                    ).apply {
                                        flags = 335544320
                                    }
                                )
                                overridePendingTransition(
                                    R.anim.lc_slide_in_right,
                                    R.anim.lc_slide_out_left
                                )
                                finish()
                            }
                        }

                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {

                            startActivity(
                                Intent(
                                    this@Spidy_MainActivity,
                                    MainLauncherActivity::class.java
                                ).apply {
                                    flags = 335544320
                                }
                            )
                            overridePendingTransition(
                                R.anim.lc_slide_in_right,
                                R.anim.lc_slide_out_left
                            )
                            finish()
                        }

                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {

                            startActivity(
                                Intent(
                                    this@Spidy_MainActivity,
                                    MainLauncherActivity::class.java
                                ).apply {
                                    flags = 335544320
                                }
                            )
                            overridePendingTransition(
                                R.anim.lc_slide_in_right,
                                R.anim.lc_slide_out_left
                            )
                            finish()
                        }
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {

                }
            })
        } else {
            startActivity(
                Intent(
                    this@Spidy_MainActivity,
                    MainLauncherActivity::class.java
                ).apply {
                    flags = 335544320
                }
            )
            overridePendingTransition(
                R.anim.lc_slide_in_right,
                R.anim.lc_slide_out_left
            )
            finish()
        }
    }

    private fun checkForUpdate() {

        try {

            val appUpdateManager =
                AppUpdateManagerFactory.create(this)

            val appUpdateInfoTask =
                appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

                if (
                    appUpdateInfo.updateAvailability()
                    == UpdateAvailability.UPDATE_AVAILABLE
                    &&
                    appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE
                    )
                ) {

                    try {

                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            activityResultLauncher,
                            AppUpdateOptions
                                .newBuilder(
                                    AppUpdateType.IMMEDIATE
                                )
                                .setAllowAssetPackDeletion(true)
                                .build()
                        )

                    } catch (e: Exception) {

                        e.printStackTrace()

                    }
                }
            }

        } catch (e: Exception) {

            Log.d(
                "TAG_ERROR",
                "checkForUpdate: ${e.message}"
            )
        }
    }

    private fun requestConsent() {

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
            config.isConsentChecked = true
        }
    }



}
