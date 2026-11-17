package com.zone.messanger.text.InterFaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zone.messanger.text.Models.RecentApps

@Dao
interface RecentAppsDao {

    @Query("SELECT * FROM recent_apps ORDER BY app_order ASC")
    fun getRecentApps(): List<RecentApps>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: RecentApps)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<RecentApps>)

    @Query("DELETE FROM recent_apps WHERE package_name = :packageName")
    fun deleteApp(packageName: String)

    @Query("DELETE FROM recent_apps")
    fun deleteAll()

    @Transaction
    fun onAppOpened(newApp: RecentApps) {
        val currentList = getRecentApps().toMutableList()

        val iterator = currentList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().packageName == newApp.packageName) {
                iterator.remove()
            }
        }

        currentList.add(0, newApp.copy(order = 1))

        val limitedList = currentList.take(4)

        val orderedList = limitedList.mapIndexed { index, app ->
            app.copy(order = index + 1)
        }

        deleteAll()
        insertAll(orderedList)
    }
}