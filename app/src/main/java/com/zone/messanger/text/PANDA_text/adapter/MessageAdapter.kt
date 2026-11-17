package com.zone.messanger.text.PANDA_text.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zone.messanger.text.databinding.ItemDateHeaderBinding
import com.zone.messanger.text.databinding.ItemMessageReceivedBinding
import com.zone.messanger.text.databinding.ItemMessageSentBinding
import com.zone.messanger.text.PANDA_text.model.Message
import com.zone.messanger.text.PANDA_text.util.DateTimeUtils

private const val TYPE_DATE_HEADER = 0
private const val TYPE_SENT = 1
private const val TYPE_RECEIVED = 2

private sealed class Row {
    data class DateHeader(val dateLabel: String) : Row()
    data class MessageRow(val message: Message) : Row()
}

class MessageAdapter(
    private val onRetry: (Message) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val rows = ArrayList<Row>()

    fun submitList(messages: List<Message>) {
        rows.clear()
        var lastDate: Long? = null
        for (message in messages) {
            if (lastDate == null || !DateTimeUtils.isSameDayMillis(lastDate, message.date)) {
                rows.add(Row.DateHeader(DateTimeUtils.formatDateHeader(message.date)))
                lastDate = message.date
            }
            rows.add(Row.MessageRow(message))
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (rows[position]) {
        is Row.DateHeader -> TYPE_DATE_HEADER
        is Row.MessageRow -> {
            val message = (rows[position] as Row.MessageRow).message
            if (message.isOutgoing) TYPE_SENT else TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_DATE_HEADER -> DateHeaderHolder(
                ItemDateHeaderBinding.inflate(inflater, parent, false)
            )
            TYPE_SENT -> SentHolder(
                ItemMessageSentBinding.inflate(inflater, parent, false)
            )
            else -> ReceivedHolder(
                ItemMessageReceivedBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is Row.DateHeader -> (holder as DateHeaderHolder).bind(row.dateLabel)
            is Row.MessageRow -> {
                if (holder is SentHolder) holder.bind(row.message)
                if (holder is ReceivedHolder) holder.bind(row.message)
            }
        }
    }

    override fun getItemCount(): Int = rows.size

    inner class DateHeaderHolder(private val binding: ItemDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: String) {
            binding.txtDate.text = label
        }
    }

    inner class SentHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.txtMessageBody.text = message.body
            binding.txtMessageTime.text = DateTimeUtils.formatTime(message.date)
            val statusIcon = if (message.isFailed) {
                com.zone.messanger.text.R.drawable.ic_close
            } else {
                com.zone.messanger.text.R.drawable.ic_check_double
            }
            binding.imgStatus.setImageResource(statusIcon)
            binding.root.setOnClickListener {
                if (message.isFailed) onRetry(message)
            }
        }
    }

    inner class ReceivedHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.txtMessageBody.text = message.body
            binding.txtMessageTime.text = DateTimeUtils.formatTime(message.date)
        }
    }
}
