package com.zone.messanger.text.PANDA_Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.zone.messanger.text.PANDA_Activity.Spidy_BaseSimpleActivity
import com.zone.messanger.text.databinding.ItemDividerBinding
import com.zone.messanger.text.databinding.ItemHeaderDefaultLauncherBinding
import com.zone.messanger.text.databinding.ItemLauncherLabelBinding
import com.zone.messanger.text.Data.Smartly_Extension.animateScale
import com.zone.messanger.text.Data.Smartly_Extension.beVisibleIf
import com.zone.messanger.text.Data.Smartly_Extension.config
import com.zone.messanger.text.Data.Smartly_Extension.getProperTextColor
import com.zone.messanger.text.Data.Smartly_Extension.realScreenSize
import com.zone.messanger.text.InterFaces.AllAppsListener
import com.zone.messanger.text.Models.AppLauncher
import com.zone.messanger.text.Models.HeaderType
import com.zone.messanger.text.Models.LauncherDisplayItem

class LaunchersAdapter(
    val activity: Spidy_BaseSimpleActivity,
    val allAppsListener: AllAppsListener,
    val itemClick: (Any) -> Unit,
    val onHeaderClick: (HeaderType) -> Unit,
) : ListAdapter<LauncherDisplayItem, RecyclerView.ViewHolder>(LauncherDiffCallback()),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    private var textColor = activity.getProperTextColor()
    private var iconPadding = 0
    private var iconSizePx = 0

    private val TYPE_APP = 0
    private val TYPE_DIVIDER = 1
    private val TYPE_HEADER_DEFAULT = 2

    init {
        setHasStableIds(true)
        calculateIconSize()
    }

    fun calculateIconSize() {
        val referenceColumnCount = activity.config.homeColumnCount
        if (referenceColumnCount <= 0) return

        val screenWidth = activity.realScreenSize.x
        val referenceCellWidth = screenWidth / referenceColumnCount
        val baseConstant = 20.0f
        val multiplier = 5.0f
        val density = activity.resources.displayMetrics.density
        val userSizeSetting = activity.config.drawerIconSize * 9.0f
        val differenceVal = baseConstant - userSizeSetting
        val differencePx = differenceVal * density
        val calculatedMargin = (differencePx * multiplier) / referenceColumnCount
        val minimumIconSizePx = (30 * density).toInt()
        val maxAllowedMargin = (referenceCellWidth - minimumIconSizePx) / 2
        val finalMargin = calculatedMargin.toInt().coerceIn(0, maxAllowedMargin)

        iconSizePx = referenceCellWidth - (2 * finalMargin)
        if (iconSizePx < 0) iconSizePx = 0
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is LauncherDisplayItem.AppItem -> TYPE_APP
            is LauncherDisplayItem.Separator -> TYPE_DIVIDER
            is LauncherDisplayItem.HeaderItem -> {
                when (item.type) {
                    HeaderType.SET_DEFAULT_LAUNCHER -> TYPE_HEADER_DEFAULT
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return when (val item = getItem(position)) {
            is LauncherDisplayItem.AppItem -> item.appLauncher.getLauncherIdentifier().hashCode()
                .toLong()

            is LauncherDisplayItem.Separator -> Long.MIN_VALUE

            is LauncherDisplayItem.HeaderItem -> ("header_" + item.type.name).hashCode().toLong()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER_DEFAULT -> {
                val binding = ItemHeaderDefaultLauncherBinding.inflate(inflater, parent, false)
                DefaultLauncherHeaderViewHolder(binding)
            }


            TYPE_DIVIDER -> {
                val binding = ItemDividerBinding.inflate(inflater, parent, false)
                object : RecyclerView.ViewHolder(binding.root) {}
            }

            else -> {
                val binding = ItemLauncherLabelBinding.inflate(inflater, parent, false)
                AppViewHolder(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is AppViewHolder -> {
                if (item is LauncherDisplayItem.AppItem) holder.bindView(item.appLauncher)
            }

            is DefaultLauncherHeaderViewHolder -> {
                holder.bindView()
            }

        }
    }

    override fun submitList(list: MutableList<LauncherDisplayItem>?) {
        calculateIconWidth()
        super.submitList(list)
    }

    private fun calculateIconWidth() {
        val currentColumnCount = activity.config.drawerColumnCount
        val iconWidth = activity.realScreenSize.x / currentColumnCount
        iconPadding = (iconWidth * 0.1f).toInt()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTextColor(newTextColor: Int) {
        if (newTextColor != textColor) {
            textColor = newTextColor
            notifyDataSetChanged()
        }
    }


    inner class DefaultLauncherHeaderViewHolder(private val binding: ItemHeaderDefaultLauncherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView() {
            binding.btnSetNow.setOnClickListener {
                onHeaderClick(HeaderType.SET_DEFAULT_LAUNCHER)
            }
        }
    }

    inner class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("ClickableViewAccessibility")
        fun bindView(launcher: AppLauncher) {

            val binding = ItemLauncherLabelBinding.bind(itemView)
            itemView.apply {

                binding.launcherLabel.text = launcher.title
                binding.launcherLabel.setTextColor(textColor)
                binding.launcherLabel.textSize = activity.config.drawerTextSize
                binding.launcherLabel.beVisibleIf(!activity.config.drawerLabelHide)

                val params = binding.launcherIcon.layoutParams
                if (params.width != iconSizePx || params.height != iconSizePx) {
                    params.width = iconSizePx
                    params.height = iconSizePx
                    binding.launcherIcon.layoutParams = params
                }

                Glide.with(context).load(launcher.drawable).into(binding.launcherIcon)

                setOnClickListener { itemClick(LauncherDisplayItem.AppItem(launcher)) }

                setOnLongClickListener {
                    val location = IntArray(2)
                    getLocationOnScreen(location)
                    allAppsListener.onAppLauncherLongPressed(
                        x = (location[0] + width / 2).toFloat(),
                        y = location[1].toFloat(),
                        appLauncher = launcher
                    )
                    true
                }

                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            binding.launcherIcon.drawable?.alpha = LAUNCHER_ALPHA_PRESSED
                            animateScale(
                                from = LAUNCHER_SCALE_NORMAL,
                                to = LAUNCHER_SCALE_PRESSED,
                                duration = LAUNCHER_SCALE_UP_DURATION
                            )
                        }

                        MotionEvent.ACTION_UP,
                        MotionEvent.ACTION_CANCEL,
                            -> {
                            binding.launcherIcon.drawable?.alpha = LAUNCHER_ALPHA_NORMAL
                            animateScale(
                                from = LAUNCHER_SCALE_PRESSED,
                                to = LAUNCHER_SCALE_NORMAL,
                                duration = LAUNCHER_SCALE_DOWN_DURATION
                            )
                        }
                    }
                    false
                }
            }
        }
    }

    override fun onChange(position: Int): String {
        return when (val item = currentList.getOrNull(position)) {
            is LauncherDisplayItem.AppItem -> item.appLauncher.getBubbleText() ?: ""
            else -> ""
        }
    }

    companion object {
        private const val LAUNCHER_SCALE_NORMAL = 1f
        private const val LAUNCHER_SCALE_PRESSED = 1.15f
        private const val LAUNCHER_SCALE_UP_DURATION = 100L
        private const val LAUNCHER_SCALE_DOWN_DURATION = 50L
        private const val LAUNCHER_ALPHA_NORMAL = 255
        private const val LAUNCHER_ALPHA_PRESSED = 220
    }
}

private class LauncherDiffCallback : DiffUtil.ItemCallback<LauncherDisplayItem>() {
    override fun areItemsTheSame(
        oldItem: LauncherDisplayItem,
        newItem: LauncherDisplayItem,
    ): Boolean {
        return when {
            oldItem is LauncherDisplayItem.AppItem && newItem is LauncherDisplayItem.AppItem ->
                oldItem.appLauncher.getLauncherIdentifier().hashCode()
                    .toLong() == newItem.appLauncher.getLauncherIdentifier().hashCode().toLong()

            oldItem is LauncherDisplayItem.Separator && newItem is LauncherDisplayItem.Separator -> true

            // Check Header Types
            oldItem is LauncherDisplayItem.HeaderItem && newItem is LauncherDisplayItem.HeaderItem ->
                oldItem.type == newItem.type

            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: LauncherDisplayItem,
        newItem: LauncherDisplayItem,
    ): Boolean {
        return when {
            oldItem is LauncherDisplayItem.AppItem && newItem is LauncherDisplayItem.AppItem -> {
                val oldApp = oldItem.appLauncher
                val newApp = newItem.appLauncher
                oldApp.title == newApp.title &&
                        oldApp.order == newApp.order &&
                        oldApp.thumbnailColor == newApp.thumbnailColor &&
                        oldApp.drawable != null &&
                        newApp.drawable != null
            }

            oldItem is LauncherDisplayItem.Separator && newItem is LauncherDisplayItem.Separator -> true

            oldItem is LauncherDisplayItem.HeaderItem && newItem is LauncherDisplayItem.HeaderItem ->
                oldItem.type == newItem.type

            else -> false
        }
    }
}
