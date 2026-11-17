package com.zone.messanger.text.PANDA_Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zone.messanger.text.R
import com.zone.messanger.text.databinding.ItemLanguageBinding
import com.zone.messanger.text.Views.LanguageModel

class LanguageAdapter(private val list: ArrayList<LanguageModel>) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = list[position]
        val context = holder.binding.root.context

        holder.binding.tvLang.text = model.name
        holder.binding.ivFlag.setImageResource(model.flagRes)

        // Background comes from the selector drawable via isSelected
        holder.binding.root.isSelected = model.isSelected

        // ✅ Text color manually toggle (selector bg alag che, text color separate set karvi padse)
        if (model.isSelected) {
            holder.binding.tvLang.setTextColor(
                ContextCompat.getColor(context, android.R.color.white)
            )
        } else {
            holder.binding.tvLang.setTextColor(
                ContextCompat.getColor(context, R.color.lc_txt_color)
            )
        }

        holder.binding.root.setOnClickListener {

            selectedPosition = position

            list.forEachIndexed { index, item ->
                item.isSelected = index == position
            }

            notifyDataSetChanged()
        }
    }

    fun getSelectedLanguage(): String {
        return list[selectedPosition].name
    }
}