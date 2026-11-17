package com.zone.messanger.text.Helper

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class WebSearchHistoryManager(context: Context) {

    private val prefs = context.getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "search_history"

    fun saveQuery(query: String) {
        val list = load().toMutableList()

        list.remove(query)
        list.add(0, query)

        val trimmedList = list.take(20)

        prefs.edit() { putString(key, gson.toJson(trimmedList)) }
    }

    fun load(): List<String> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun deleteQuery(query: String) {
        val list = load().toMutableList()
        list.remove(query)

        prefs.edit() {
            putString(key, gson.toJson(list))
        }
    }

}