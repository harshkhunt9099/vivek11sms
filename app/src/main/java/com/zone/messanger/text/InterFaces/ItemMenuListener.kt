package com.zone.messanger.text.InterFaces

import android.view.Menu
import com.zone.messanger.text.Models.HomeScreenGridItem

interface ItemMenuListener {
    fun onAnyClick()
    fun rename(gridItem: HomeScreenGridItem)
    fun resize(gridItem: HomeScreenGridItem)
    fun appInfo(gridItem: HomeScreenGridItem)
    fun remove(gridItem: HomeScreenGridItem)
    fun uninstall(gridItem: HomeScreenGridItem)
    fun onDismiss()
    fun beforeShow(menu: Menu)
}
