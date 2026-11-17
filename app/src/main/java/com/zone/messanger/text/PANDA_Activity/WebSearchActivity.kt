package com.zone.messanger.text.PANDA_Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_Adapter.WebSearchHistoryAdapter
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.databinding.ActivityWebSearchBinding
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.hideKeyboard
import com.zone.messanger.text.Helper.WebSearchHistoryManager

class WebSearchActivity : Messanger__BaseActivity() {
    lateinit var binding: ActivityWebSearchBinding
    private lateinit var viewModel: WebSearchViewModel
    private lateinit var historyAdapter: WebSearchHistoryAdapter

    private val voiceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = matches?.firstOrNull() ?: ""

                viewModel.addQuery(spokenText)
                openWebSearch(this, spokenText)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWebSearchBinding.inflate(layoutInflater)
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
        if (intent.getBooleanExtra("isVoiceIconClick", false)) {
            openGoogleAssistant()
        }

        updateSearchIcons(binding.etSearch.text)

        val manager = WebSearchHistoryManager(this)
        viewModel = WebSearchViewModel(manager)

        historyAdapter = WebSearchHistoryAdapter(
            emptyList(),
            onClick = { query ->
                viewModel.addQuery(query)
                openWebSearch(this, query)
            },

            onDelete = { query ->
                viewModel.deleteQuery(query)   // 👈 call delete
            }
        )

        binding.rcvSearchHistory.adapter = historyAdapter

        viewModel.history.observe(this) { list ->
            historyAdapter.submitList(list)

            val isEmpty = list.isEmpty()
            val isTyping = binding.etSearch.text?.isNotBlank() == true

            binding.rcvSearchHistory.visibility =
                if (isEmpty && isTyping) View.GONE else View.VISIBLE

            binding.tvNoItemFound.visibility =
                if (isEmpty && isTyping) View.VISIBLE else View.GONE
        }

        viewModel.loadHistory()


        binding.etSearch.showKeyboard()

        binding.etSearch.doAfterTextChanged { text ->
            updateSearchIcons(text)
            viewModel.filter(text?.toString().orEmpty())
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.addQuery(query)
                    openWebSearch(this, query)
                }
                true
            } else {
                false
            }
        }

    }

    private fun initClick() {


        onBackPressedDispatcher.addCallback {
            AdsManager.showInterstitialLauncher(this@WebSearchActivity, false) {
                if (binding.etSearch.text?.isNotEmpty() == true) {
                    binding.etSearch.text?.clear()
                    binding.etSearch.clearFocus()
                    viewModel.loadHistory()
                    updateSearchIcons(null)
                    finish()
                } else {
                    finish()
                }

            }
        }

        binding.ivMic.setOnClickListener {
            openGoogleAssistant()
        }

        binding.ivSearch.setOnClickListener {
            binding.etSearch.showKeyboard()
        }

        binding.ivClose.setOnClickListener {
            binding.etSearch.text?.clear()
            binding.etSearch.clearFocus()
            viewModel.loadHistory()
            updateSearchIcons(null)
            hideKeyboard()
        }
    }

    private fun openWebSearch(context: Context, query: String) {
        val searchEngine = config.searchEngine
        val url = searchEngine.buildSearchUrl(query)
        val uri = url.toUri()

        try {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.intent.setPackage("com.android.chrome")
            customTabsIntent.launchUrl(context, uri)
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun openGoogleAssistant() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
            }

            voiceResultLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun EditText.showKeyboard() {
        requestFocus()
        post {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun updateSearchIcons(text: CharSequence?) {
        val hasText = text?.isNotBlank() == true

        binding.ivMic.visibility = if (hasText) View.GONE else View.VISIBLE
        binding.ivClose.visibility = if (hasText) View.VISIBLE else View.GONE
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

}