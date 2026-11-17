package com.zone.messanger.text.PANDA_text.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zone.messanger.text.databinding.ItemConversationBinding
import com.zone.messanger.text.PANDA_text.model.Conversation
import com.zone.messanger.text.PANDA_text.util.ContactUtils
import com.zone.messanger.text.PANDA_text.util.DateTimeUtils

class ConversationAdapter(
    private val onClick: (Conversation) -> Unit,
    private val onLongClick: (Conversation) -> Boolean
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val items = ArrayList<Conversation>()

    fun submitList(newItems: List<Conversation>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            val context = binding.root.context
            binding.txtName.text = conversation.displayName
            binding.txtPreview.text = conversation.snippet
            binding.txtTime.text = DateTimeUtils.formatListTimestamp(conversation.timestamp)

            val avatarText: TextView = binding.txtAvatar
            avatarText.text = ContactUtils.initialFor(conversation.displayName)
            avatarText.background.mutate().setTint(
                ContactUtils.avatarColorFor(context, conversation.displayName)
            )

            if (conversation.unreadCount > 0) {
                binding.txtUnreadCount.visibility = android.view.View.VISIBLE
                binding.txtUnreadCount.text =
                    if (conversation.unreadCount > 9) "9+" else conversation.unreadCount.toString()
                binding.txtName.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                binding.txtUnreadCount.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onClick(conversation) }
            binding.root.setOnLongClickListener { onLongClick(conversation) }
        }
    }
}
