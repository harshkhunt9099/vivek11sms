package com.zone.messanger.text.PANDA_text.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_text.adapter.EmojiAdapter
import com.zone.messanger.text.PANDA_text.adapter.MessageAdapter
import com.zone.messanger.text.databinding.ActivityChatBinding
import com.zone.messanger.text.PANDA_text.model.Message
import com.zone.messanger.text.Repository.SmsRepository
import com.zone.messanger.text.PANDA_ads.AdsManager
import com.zone.messanger.text.PANDA_ads.AdsManager.adsData
import com.zone.messanger.text.PANDA_ads.NativeType
import com.zone.messanger.text.PANDA_text.util.ContactUtils
import com.zone.messanger.text.PANDA_text.util.EmojiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_THREAD_ID = "extra_thread_id"
        const val EXTRA_ADDRESS = "extra_address"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var repository: SmsRepository
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var emojiAdapter: EmojiAdapter

    private var threadId: Long = -1
    private lateinit var address: String
    private var attachmentUri: Uri? = null
    private var currentEmojiCategory = 0

    private val messageReceivedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadMessages()
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) showAttachment(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = SmsRepository(this)
        threadId = intent.getLongExtra(EXTRA_THREAD_ID, -1)
        address = intent.getStringExtra(EXTRA_ADDRESS) ?: ""

        if (threadId <= 0 && address.isNotBlank()) {
            threadId = repository.getOrCreateThreadId(address)
        }

        setupHeader()
        setupMessageList()
        setupEmojiPicker()
        setupInputBar()

        AdsManager.showNative(
            this,
            container = findViewById(R.id.nativeContainer),
            nativeType = NativeType.SMALL_NATIVE_BANNER,
            adId = adsData().Native_All,
            uniqueKey = this::class.java.simpleName
        )
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("com.zone.messanger.text.text.MESSAGE_RECEIVED")
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
        loadMessages()
        lifecycleScope.launch(Dispatchers.IO) { repository.markThreadRead(threadId) }
    }

    override fun onBackPressed() {
        if (binding.emojiPicker.root.visibility == View.VISIBLE) {
            binding.emojiPicker.root.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun setupHeader() {
        val name = ContactUtils.resolveName(this, address)
        binding.txtContactName.text = name
        binding.txtContactNumber.text = address
        binding.txtAvatar.text = ContactUtils.initialFor(name)
        binding.txtAvatar.background.mutate().setTint(ContactUtils.avatarColorFor(this, name))

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(address)))
            startActivity(dialIntent)
        }

        binding.btnMore.setOnClickListener { view -> showOverflowMenu(view) }
    }

    private fun showOverflowMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.chat_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuViewContact -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(address)))
                    startActivity(intent)
                    true
                }
                R.id.menuDeleteConversation -> {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) { repository.deleteThread(threadId) }
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupMessageList() {
        messageAdapter = MessageAdapter(onRetry = { message -> retrySend(message) })
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.recyclerMessages.layoutManager = layoutManager
        binding.recyclerMessages.adapter = messageAdapter
    }

    private fun setupEmojiPicker() {
        emojiAdapter = EmojiAdapter { emoji -> insertEmoji(emoji) }
        binding.emojiPicker.recyclerEmoji.layoutManager = GridLayoutManager(this, 8)
        binding.emojiPicker.recyclerEmoji.adapter = emojiAdapter
        selectEmojiCategory(0)

        binding.btnEmoji.setOnClickListener {
            val showing = binding.emojiPicker.root.visibility == View.VISIBLE
            binding.emojiPicker.root.visibility = if (showing) View.GONE else View.VISIBLE
        }

        val tabs = listOf(
            binding.emojiPicker.catSmileys, binding.emojiPicker.catPeople, binding.emojiPicker.catAnimals,
            binding.emojiPicker.catFood, binding.emojiPicker.catActivities, binding.emojiPicker.catSymbols
        )
        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener { selectEmojiCategory(index) }
        }
    }

    private fun selectEmojiCategory(index: Int) {
        currentEmojiCategory = index
        emojiAdapter.submitList(EmojiData.forCategory(index))
        val tabs = listOf(
            binding.emojiPicker.catSmileys, binding.emojiPicker.catPeople, binding.emojiPicker.catAnimals,
            binding.emojiPicker.catFood, binding.emojiPicker.catActivities, binding.emojiPicker.catSymbols
        )
        tabs.forEachIndexed { i, tab -> tab.isSelected = i == index }
    }

    private fun insertEmoji(emoji: String) {
        val edit = binding.edtMessage
        val start = edit.selectionStart.coerceAtLeast(0)
        val end = edit.selectionEnd.coerceAtLeast(0)
        edit.text.replace(minOf(start, end), maxOf(start, end), emoji)
        edit.setSelection(minOf(start, end) + emoji.length)
    }

    private fun setupInputBar() {
        binding.btnAttach.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        binding.btnRemoveAttachment.setOnClickListener {
            attachmentUri = null
            binding.attachmentPreviewContainer.visibility = View.GONE
        }
        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun showAttachment(uri: Uri) {
        attachmentUri = uri
        binding.attachmentPreviewContainer.visibility = View.VISIBLE
        binding.imgAttachmentPreview.setImageURI(uri)
    }

    private fun sendMessage() {
        val body = binding.edtMessage.text?.toString()?.trim().orEmpty()
        if (body.isEmpty()) {
            if (attachmentUri != null) {
                Toast.makeText(this, R.string.image_attached, Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (address.isBlank()) {
            Toast.makeText(this, R.string.message_send_failed, Toast.LENGTH_SHORT).show()
            return
        }

        binding.edtMessage.setText("")
        binding.attachmentPreviewContainer.visibility = View.GONE
        attachmentUri = null

        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                repository.sendSms(address, body, threadId)
            }
            if (!success) {
                Toast.makeText(this@ChatActivity, R.string.message_send_failed, Toast.LENGTH_SHORT).show()
            }
            if (threadId <= 0) {
                threadId = withContext(Dispatchers.IO) { repository.getOrCreateThreadId(address) }
            }
            loadMessages()
        }
    }

    private fun retrySend(message: Message) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { repository.sendSms(address, message.body, threadId) }
            loadMessages()
        }
    }

    private fun loadMessages() {
        if (threadId <= 0) return
        lifecycleScope.launch {
            val messages = withContext(Dispatchers.IO) { repository.getMessages(threadId) }
            messageAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerMessages.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }
    }
}
