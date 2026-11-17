package com.zone.messanger.text.PANDA_Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zone.messanger.text.PANDA_ads.LauncherAppUiModel
import com.zone.messanger.text.databinding.ItemAppAdFolderBinding
import com.zone.messanger.text.Data.Smartly_Extension.isPackageInstalled

class AdFolderAdapter(
    private val items: List<LauncherAppUiModel>,
    private val onItemClick: (LauncherAppUiModel) -> Unit,
) : RecyclerView.Adapter<AdFolderAdapter.AdAppViewHolder>() {

    class AdAppViewHolder(val binding: ItemAppAdFolderBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.appLabel.isSelected = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdAppViewHolder {
        val binding = ItemAppAdFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdAppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdAppViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        with(holder.binding) {

            appLabel.text = item.title

            when {
                item.iconDrawable != null -> {
                    appIcon.setImageDrawable(item.iconDrawable)
                }

                item.iconUrl != null -> {
                    Glide.with(appIcon)
                        .load(item.iconUrl)
                        .into(appIcon)
                }
            }

            val isInstalled =
                item.packageName?.let { context.isPackageInstalled(it) } == true

            downloadArrow.visibility =
                if (!isInstalled) View.VISIBLE else View.GONE

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}