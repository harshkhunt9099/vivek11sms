package com.zone.messanger.text.PANDA_Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zone.messanger.text.databinding.ItemRecentAppRightPageBinding
import com.zone.messanger.text.Models.RecentApps

class RecentAppsAdapterRightPage(
    private val apps: List<RecentApps>,
    private val onAppClick: (RecentApps) -> Unit,
) : RecyclerView.Adapter<RecentAppsAdapterRightPage.AppViewHolder>() {

    class AppViewHolder(
        val binding: ItemRecentAppRightPageBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemRecentAppRightPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        val context = holder.itemView.context

        with(holder.binding) {
            val icon = try {
                context.packageManager.getApplicationIcon(app.packageName)
            } catch (e: Exception) {
                ContextCompat.getDrawable(context, android.R.drawable.sym_def_app_icon)
            }

            appIcon.setImageDrawable(icon)

            root.setOnClickListener {
                onAppClick(app)
            }
        }
    }

    override fun getItemCount(): Int = apps.size
}