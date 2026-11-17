package com.zone.messanger.text.PANDA_Activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zone.messanger.text.Helper.WebSearchHistoryManager

class WebSearchViewModel(
    private val manager: WebSearchHistoryManager
) : ViewModel() {

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>> get() = _history

    private var fullHistory: List<String> = emptyList()

    fun loadHistory() {
        fullHistory = manager.load()
        _history.value = fullHistory
    }

    fun addQuery(query: String) {
        manager.saveQuery(query)
        loadHistory()
    }

    fun filter(query: String) {
        if (query.isBlank()) {
            _history.value = fullHistory
        } else {
            _history.value = fullHistory.filter {
                it.contains(query, ignoreCase = true)
            }
        }
    }

    fun deleteQuery(query: String) {
        manager.deleteQuery(query)
        loadHistory()
    }

}