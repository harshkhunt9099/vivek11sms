package com.zone.messanger.text.PANDA_Adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zone.messanger.text.PANDA_ads.WallpaperItem
import com.zone.messanger.text.databinding.ItemWallpaperBinding

class WallpaperAdapter(
    private val onClick: (WallpaperItem) -> Unit
) : ListAdapter<WallpaperItem, WallpaperAdapter.WallpaperVH>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WallpaperItem>() {

            override fun areItemsTheSame(
                oldItem: WallpaperItem,
                newItem: WallpaperItem
            ): Boolean {
                return oldItem.imageUrl == newItem.imageUrl
            }

            override fun areContentsTheSame(
                oldItem: WallpaperItem,
                newItem: WallpaperItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class WallpaperVH(
        val binding: ItemWallpaperBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperVH {
        val binding = ItemWallpaperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WallpaperVH(binding)
    }

    override fun onBindViewHolder(holder: WallpaperVH, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            shimmerWallpaper.visibility = View.VISIBLE
            shimmerWallpaper.startShimmer()

            ivWallpaper.visibility = View.INVISIBLE

            // 2️⃣ Load image
            Glide.with(ivWallpaper)
                .load(item.thumbUrl.ifEmpty { item.imageUrl })
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        shimmerWallpaper.stopShimmer()
                        shimmerWallpaper.visibility = View.GONE
                        ivWallpaper.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        shimmerWallpaper.stopShimmer()
                        shimmerWallpaper.visibility = View.GONE
                        ivWallpaper.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(ivWallpaper)

            root.setOnClickListener {
                onClick(item)
            }
        }
    }

}