package com.zone.messanger.text.PANDA_text.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.PANDA_text.adapter.ContactAdapter
import com.zone.messanger.text.databinding.ActivityNewChatBinding
import com.zone.messanger.text.PANDA_text.model.SmsContact
import com.zone.messanger.text.PANDA_text.util.ContactUtils
import com.zone.messanger.text.PANDA_text.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChatBinding
    private lateinit var adapter: ContactAdapter
    private var allContacts: List<SmsContact> = emptyList()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) loadContacts()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ContactAdapter { contact -> openChatWith(contact.number) }
        binding.recyclerContacts.layoutManager = LinearLayoutManager(this)
        binding.recyclerContacts.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnStartChat.setOnClickListener { startFromTypedNumber() }
        binding.edtNumber.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        if (PermissionUtils.hasContactsPermission(this)) {
            loadContacts()
        } else {
            permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
        AdsManager.showNative(
            this,
            container = findViewById(R.id.nativeContainer),
            nativeType = NativeType.SMALL_NATIVE_BANNER,
            adId = adsData().Native_All,
            uniqueKey = this::class.java.simpleName
        )
    }

    private fun loadContacts() {
        lifecycleScope.launch {
            val contacts = withContext(Dispatchers.IO) { ContactUtils.listContacts(this@NewChatActivity) }
            allContacts = contacts
            adapter.submitList(contacts)
            binding.txtNoContacts.visibility =
                if (contacts.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun filterContacts(query: String) {
        val filtered = if (query.isBlank()) {
            allContacts
        } else {
            allContacts.filter {
                it.name.contains(query, ignoreCase = true) || it.number.contains(query)
            }
        }
        adapter.submitList(filtered)
        binding.txtNoContacts.visibility =
            if (filtered.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun startFromTypedNumber() {
        val typed = binding.edtNumber.text?.toString()?.trim().orEmpty()
        if (typed.isBlank()) return
        openChatWith(typed)
    }

    private fun openChatWith(number: String) {
        AdsManager.showInterstitialLauncher(this, false) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_ADDRESS, number)
            startActivity(intent)
            finish()
        }
    }
}
