package com.zone.messanger.text.Repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import com.zone.messanger.text.PANDA_text.model.Conversation
import com.zone.messanger.text.PANDA_text.model.Message
import com.zone.messanger.text.PANDA_text.util.ContactUtils


class SmsRepository(private val context: Context) {

    companion object {
        private const val TAG = "SmsRepository"
    }

    /** One entry per thread, newest first, using the latest message as the preview. */
    fun getConversations(): List<Conversation> {
        val conversations = ArrayList<Conversation>()
        val seenThreads = LinkedHashSet<Long>()
        val unreadCounts = HashMap<Long, Int>()

        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.THREAD_ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.READ,
            Telephony.Sms.TYPE
        )

        try {
            context.contentResolver.query(
                Telephony.Sms.CONTENT_URI, projection, null, null,
                Telephony.Sms.DATE + " DESC"
            )?.use { cursor ->
                val threadIdIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID)
                val addressIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                val readIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.READ)
                val typeIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)

                while (cursor.moveToNext()) {
                    val threadId = cursor.getLong(threadIdIdx)
                    val read = cursor.getInt(readIdx) == 1
                    val type = cursor.getInt(typeIdx)

                    if (!read && type == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                        unreadCounts[threadId] = (unreadCounts[threadId] ?: 0) + 1
                    }

                    if (seenThreads.add(threadId)) {
                        val address = cursor.getString(addressIdx) ?: ""
                        val body = cursor.getString(bodyIdx) ?: ""
                        val date = cursor.getLong(dateIdx)
                        val name = ContactUtils.resolveName(context, address)
                        conversations.add(
                            Conversation(threadId, address, name, body, date, 0, read)
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "No SMS permission yet", e)
        }

        return conversations.map { c ->
            val unread = unreadCounts[c.threadId] ?: 0
            if (unread == c.unreadCount) c else Conversation(
                c.threadId, c.address, c.displayName, c.snippet,
                c.timestamp, unread, unread == 0
            )
        }
    }

    fun getMessages(threadId: Long): List<Message> {
        val messages = ArrayList<Message>()
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.THREAD_ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE,
            Telephony.Sms.READ
        )
        try {
            context.contentResolver.query(
                Telephony.Sms.CONTENT_URI, projection,
                "${Telephony.Sms.THREAD_ID} = ?", arrayOf(threadId.toString()),
                Telephony.Sms.DATE + " ASC"
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
                val addressIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
                val typeIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)
                val readIdx = cursor.getColumnIndexOrThrow(Telephony.Sms.READ)

                while (cursor.moveToNext()) {
                    messages.add(
                        Message(
                            cursor.getLong(idIdx),
                            threadId,
                            cursor.getString(addressIdx) ?: "",
                            cursor.getString(bodyIdx) ?: "",
                            cursor.getLong(dateIdx),
                            cursor.getInt(typeIdx),
                            cursor.getInt(readIdx) == 1
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "No SMS permission yet", e)
        }
        return messages
    }

    /** Resolves (creating if needed) the thread id used to group messages with [address]. */
    fun getOrCreateThreadId(address: String): Long =
        try {
            Telephony.Threads.getOrCreateThreadId(context, address)
        } catch (e: Exception) {
            Log.w(TAG, "Could not resolve thread id", e)
            -1L
        }

    /**
     * Sends [body] as one or more SMS parts to [address], then (best effort)
     * records it in the SMS provider so it shows up immediately in the
     * thread. Returns true if the message was handed to the radio.
     */
    fun sendSms(address: String, body: String, threadId: Long): Boolean {
        if (address.isBlank() || body.isBlank()) return false
        return try {
            val smsManager: SmsManager =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }

            val parts = smsManager.divideMessage(body)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(address, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(address, null, body, null, null)
            }

            insertSentMessage(address, body, threadId)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS", e)
            false
        }
    }

    private fun insertSentMessage(address: String, body: String, threadId: Long) {
        try {
            val values = ContentValues().apply {
                put(Telephony.Sms.ADDRESS, address)
                put(Telephony.Sms.BODY, body)
                put(Telephony.Sms.DATE, System.currentTimeMillis())
                put(Telephony.Sms.READ, 1)
                put(Telephony.Sms.SEEN, 1)
                put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT)
                if (threadId > 0) put(Telephony.Sms.THREAD_ID, threadId)
            }
            context.contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
        } catch (e: SecurityException) {
            // Only the default SMS app may write the provider; the message was
            // still handed to SmsManager above, so delivery itself is unaffected.
            Log.w(TAG, "Not default SMS app — sent message won't persist locally", e)
        }
    }

    fun markThreadRead(threadId: Long) {
        try {
            val values = ContentValues().apply {
                put(Telephony.Sms.READ, 1)
                put(Telephony.Sms.SEEN, 1)
            }
            context.contentResolver.update(
                Telephony.Sms.CONTENT_URI, values,
                "${Telephony.Sms.THREAD_ID} = ? AND ${Telephony.Sms.READ} = 0",
                arrayOf(threadId.toString())
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "Could not mark thread read", e)
        }
    }

    fun deleteThread(threadId: Long) {
        try {
            val uri: Uri = Telephony.Sms.CONTENT_URI
            context.contentResolver.delete(
                uri, "${Telephony.Sms.THREAD_ID} = ?", arrayOf(threadId.toString())
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "Could not delete thread", e)
        }
    }

    fun isDefaultSmsApp(): Boolean =
        context.packageName == Telephony.Sms.getDefaultSmsPackage(context)
}
