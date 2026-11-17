package com.zone.messanger.text.PANDA_Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zone.messanger.text.databinding.ItemSearchHistoryBinding

class WebSearchHistoryAdapter(
    private var items: List<String>,
    private val onClick: (String) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<WebSearchHistoryAdapter.Holder>() {

    inner class Holder(val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val text = items[position]
        holder.binding.tvQuery.text = text

        holder.itemView.setOnClickListener { onClick(text) }

        holder.binding.ivDelete.setOnClickListener {
            onDelete(text)
        }
    }

    override fun getItemCount() = items.size

    fun submitList(list: List<String>) {
        items = list
        notifyDataSetChanged()
    }
}