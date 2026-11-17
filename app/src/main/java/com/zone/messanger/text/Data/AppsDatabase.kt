package com.zone.messanger.text.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zone.messanger.text.Helper.Converters
import com.zone.messanger.text.InterFaces.AppLaunchersDao
import com.zone.messanger.text.InterFaces.HomeScreenGridItemsDao
import com.zone.messanger.text.InterFaces.RecentAppsDao
import com.zone.messanger.text.Models.AppLauncher
import com.zone.messanger.text.Models.HomeScreenGridItem
import com.zone.messanger.text.Models.RecentApps

@Database(
    entities = [AppLauncher::class, HomeScreenGridItem::class, RecentApps::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppsDatabase : RoomDatabase() {

    abstract fun AppLaunchersDao(): AppLaunchersDao

    abstract fun HomeScreenGridItemsDao(): HomeScreenGridItemsDao

    abstract fun RecentAppsDao(): RecentAppsDao

    companion object {
        private var db: AppsDatabase? = null

        fun getInstance(context: Context): AppsDatabase {
            if (db == null) {
                synchronized(AppsDatabase::class) {
                    if (db == null) {
                        db = Room.databaseBuilder(
                            context.applicationContext,
                            AppsDatabase::class.java,
                            "apps.db"
                        ).build()
                    }
                }
            }
            return db!!
        }
    }
}