package com.zone.messanger.text.Models

open class WidgetsListItemsHolder(val widgets: ArrayList<AppWidget>) : WidgetsListItem() {
    override fun getHashToCompare() = widgets.sumOf { it.getHashToCompare() }
}
