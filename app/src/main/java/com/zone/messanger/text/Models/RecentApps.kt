package com.zone.messanger.text.Models

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "recent_apps",
    indices = [Index(value = ["package_name"], unique = true)]
)
data class RecentApps(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "activity_name")
    val activityName: String,

    @ColumnInfo(name = "app_order")
    val order: Int, // 1, 2, 3, 4

    @ColumnInfo(name = "thumbnail_color")
    val thumbnailColor: Int
) {
    @Ignore
    var drawable: Drawable? = null

    // Helper to update order easily
    fun copyWithOrder(newOrder: Int): RecentApps {
        return this.copy(order = newOrder)
    }

}