package com.zone.messanger.text.Models

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zone.messanger.text.Helper.ITEM_TYPE_ICON

@Keep
@Entity(tableName = "home_screen_grid_items", indices = [(Index(value = ["id"], unique = true))])
data class HomeScreenGridItem(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "left") var left: Int,
    @ColumnInfo(name = "top") var top: Int,
    @ColumnInfo(name = "right") var right: Int,
    @ColumnInfo(name = "bottom") var bottom: Int,
    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "package_name") var packageName: String,
    @ColumnInfo(name = "activity_name") var activityName: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "class_name") var className: String,
    @ColumnInfo(name = "widget_id") var widgetId: Int,
    @ColumnInfo(name = "shortcut_id") var shortcutId: String,
    @ColumnInfo(name = "icon") var icon: Bitmap? = null,
    @ColumnInfo(name = "docked") var docked: Boolean = false,
    @ColumnInfo(name = "parent_id") var parentId: Long? = null,

    @Ignore var drawable: Drawable? = null,
    @Ignore var providerInfo: AppWidgetProviderInfo? = null,
    @Ignore var activityInfo: ActivityInfo? = null,
    @Ignore var widthCells: Int = 1,
    @Ignore var heightCells: Int = 1
) {
    companion object {
        const val FOLDER_MAX_CAPACITY = 16
    }

    constructor() : this(null, -1, -1, -1, -1, 0, "", "", "", ITEM_TYPE_ICON, "", -1, "", null, false, null, null, null, null, 1, 1)

    fun getWidthInCells() = if (right == -1 || left == -1) {
        widthCells
    } else {
        right - left + 1
    }

    fun getHeightInCells() = if (bottom == -1 || top == -1) {
        heightCells
    } else {
        bottom - top + 1
    }

    fun getDockAdjustedTop(rowCount: Int): Int {
        return if (!docked) {
            top
        } else {
            rowCount - 2
        }
    }

    fun getDockAdjustedBottom(rowCount: Int): Int {
        return if (!docked) {
            bottom
        } else {
            rowCount - 2
        }
    }

    fun getItemIdentifier() = "$packageName/$activityName"

    fun getTopLeft(rowCount: Int) = Point(left, getDockAdjustedTop(rowCount))

    fun toRecentApps(): RecentApps {
        return RecentApps(
            id = 0,
            title = title,
            packageName = packageName,
            activityName = activityName,
            order = 0,
            thumbnailColor = 0,
        )
    }

}
