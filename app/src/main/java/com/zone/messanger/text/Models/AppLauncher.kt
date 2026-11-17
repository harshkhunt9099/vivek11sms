package com.zone.messanger.text.Models

import android.graphics.drawable.Drawable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zone.messanger.text.Helper.SORT_BY_TITLE
import com.zone.messanger.text.Helper.SORT_DESCENDING
import com.zone.messanger.text.Helper.normalizeString

@Keep
@Entity(tableName = "apps", indices = [(Index(value = ["package_name"], unique = true))])
data class AppLauncher(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "package_name") var packageName: String,
    @ColumnInfo(name = "activity_name") var activityName: String,
    @ColumnInfo(name = "order") var order: Int,
    @ColumnInfo(name = "thumbnail_color") var thumbnailColor: Int,

    @Ignore var drawable: Drawable?
) : Comparable<AppLauncher> {

    constructor() : this(null, "", "", "", 0, 0, null)

    companion object {
        var sorting = 0
    }

    override fun equals(other: Any?) = packageName.equals((other as AppLauncher).packageName, true)

    override fun hashCode() = super.hashCode()

    fun getBubbleText() = title

    fun getLauncherIdentifier() = "$packageName/$activityName"


    fun toRecentApps(): RecentApps {
        return RecentApps(
            id = 0,
            title = title,
            packageName = packageName,
            activityName = activityName,
            order = 0,
            thumbnailColor = thumbnailColor,
        )
    }

    override fun compareTo(other: AppLauncher): Int {
        var result = when {
            sorting and SORT_BY_TITLE != 0 -> title.normalizeString().lowercase().compareTo(other.title.normalizeString().lowercase())
            else -> {
                if (order > 0 && other.order == 0) {
                    -1
                } else if (order == 0 && other.order > 0) {
                    1
                } else if (order > 0 && other.order > 0) {
                    order.compareTo(other.order)
                } else {
                    title.lowercase().compareTo(other.title.lowercase())
                }
            }
        }

        if (sorting and SORT_DESCENDING != 0) {
            result *= -1
        }

        return result
    }


}