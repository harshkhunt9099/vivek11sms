package com.zone.messanger.text.PANDA_Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zone.messanger.text.R
import com.zone.messanger.text.PANDA_Activity.Spidy_BaseSimpleActivity
import com.zone.messanger.text.databinding.ItemWidgetListItemsHolderBinding
import com.zone.messanger.text.databinding.ItemWidgetListSectionBinding
import com.zone.messanger.text.databinding.ItemWidgetPreviewBinding
import com.zone.messanger.text.Data.Smartly_Extension.getProperTextColor
import com.zone.messanger.text.Helper.WIDGET_LIST_ITEMS_HOLDER
import com.zone.messanger.text.Helper.WIDGET_LIST_SECTION
import com.zone.messanger.text.InterFaces.WidgetsFragmentListener
import com.zone.messanger.text.Models.WidgetsListItem
import com.zone.messanger.text.Models.WidgetsListItemsHolder
import com.zone.messanger.text.Models.WidgetsListSection

class WidgetsAdapter(
    val activity: Spidy_BaseSimpleActivity,
    var widgetListItems: ArrayList<WidgetsListItem>,
    val widgetsFragmentListener: WidgetsFragmentListener,
    val itemClick: () -> Unit
) : RecyclerView.Adapter<WidgetsAdapter.ViewHolder>() {

    private var textColor = activity.getProperTextColor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            WIDGET_LIST_SECTION -> ItemWidgetListSectionBinding.inflate(inflater, parent, false)
            else -> ItemWidgetListItemsHolderBinding.inflate(inflater, parent, false)
        }

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val widgetListItem = widgetListItems[position]
        holder.bindView(widgetListItems[position]) { itemView, layoutPosition ->
            when (widgetListItem) {
                is WidgetsListSection -> setupListSection(itemView, widgetListItem)
                is WidgetsListItemsHolder -> setupListItemsHolder(itemView, widgetListItem)
            }
        }
    }

    override fun getItemCount() = widgetListItems.size

    override fun getItemViewType(position: Int) = when {
        widgetListItems[position] is WidgetsListSection -> WIDGET_LIST_SECTION
        else -> WIDGET_LIST_ITEMS_HOLDER
    }

    private fun setupListSection(view: View, section: WidgetsListSection) {
        ItemWidgetListSectionBinding.bind(view).apply {
            widgetAppTitle.text = section.appTitle
            widgetAppTitle.setTextColor(textColor)
            widgetAppIcon.setImageDrawable(section.appIcon)
        }
    }

    private fun setupListItemsHolder(view: View, listItem: WidgetsListItemsHolder) {
        val binding = ItemWidgetListItemsHolderBinding.bind(view)
        binding.widgetListItemsHolder.removeAllViews()
        binding.widgetListItemsScrollView.scrollX = 0
        listItem.widgets.forEachIndexed { index, widget ->
            val imageSize = activity.resources.getDimension(R.dimen.lc_widget_preview_size).toInt()
            val widgetPreview = ItemWidgetPreviewBinding.inflate(LayoutInflater.from(activity))
            binding.widgetListItemsHolder.addView(widgetPreview.root)

            val endMargin = if (index == listItem.widgets.size - 1) {
                activity.resources.getDimension(R.dimen.lc_medium_margin).toInt()
            } else {
                0
            }

            widgetPreview.widgetTitle.apply {
                text = widget.widgetTitle
                setTextColor(textColor)
            }

            widgetPreview.widgetSize.apply {
                text = if (widget.isShortcut) {
                    activity.getString(R.string.lc_shortcut)
                } else {
                    "${widget.widthCells} x ${widget.heightCells}"
                }
                setTextColor(textColor)
            }

            (widgetPreview.widgetImage.layoutParams as RelativeLayout.LayoutParams).apply {
                marginStart = activity.resources.getDimension(R.dimen.lc_activity_margin).toInt()
                marginEnd = endMargin
                width = imageSize
                height = imageSize
            }

            Glide.with(activity)
                .load(widget.widgetPreviewImage)
                .into(widgetPreview.widgetImage)

            widgetPreview.root.setOnClickListener { itemClick() }

            widgetPreview.root.setOnLongClickListener { view ->
                widgetsFragmentListener.onWidgetLongPressed(widget)
                true
            }
        }
    }

    fun updateItems(newItems: ArrayList<WidgetsListItem>) {
        val oldSum = widgetListItems.sumOf { it.getHashToCompare() }
        val newSum = newItems.sumOf { it.getHashToCompare() }
        if (oldSum != newSum) {
            widgetListItems = newItems
            notifyDataSetChanged()
        }
    }

    fun updateTextColor(newTextColor: Int) {
        if (newTextColor != textColor) {
            textColor = newTextColor
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(
            widgetListItem: WidgetsListItem,
            callback: (itemView: View, adapterPosition: Int) -> Unit
        ) {
            itemView.apply {
                callback(this, adapterPosition)
            }
        }
    }
}
