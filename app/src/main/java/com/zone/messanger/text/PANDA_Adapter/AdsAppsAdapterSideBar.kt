package com.zone.messanger.text.PANDA_Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zone.messanger.text.PANDA_ads.LauncherAppUiModel
import com.zone.messanger.text.databinding.ItemAdsAppsSidebarBinding
import com.zone.messanger.text.Data.Smartly_Extension.isPackageInstalled

class AdsAppsAdapterSideBar(
    private val items: List<LauncherAppUiModel>,
    private val onItemClick: (LauncherAppUiModel) -> Unit,
) : RecyclerView.Adapter<AdsAppsAdapterSideBar.AdAppViewHolder>() {

    class AdAppViewHolder(
        val binding: ItemAdsAppsSidebarBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdAppViewHolder {
        val binding = ItemAdsAppsSidebarBinding.inflate(
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
        return if (items.size >= 4) 4 else items.size
    }

}