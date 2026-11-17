package com.zone.messanger.text.Models

import android.net.Uri
import androidx.annotation.Keep

@Keep
enum class SearchEngine(val baseUrl: String, val displayName: String) {
    GOOGLE(
        "https://www.google.com/search?q=",
        "Google (Default)"
    ),
    BING(
        "https://www.bing.com/search?q=",
        "Bing"
    ),
    DUCKDUCKGO(
        "https://duckduckgo.com/?q=",
        "DuckDuckGo"
    );

    fun buildSearchUrl(query: String): String {
        return baseUrl + Uri.encode(query)
    }
}