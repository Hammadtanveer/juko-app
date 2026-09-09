package com.juko.app.core.storage

import com.juko.app.core.model.RecentSearch
import platform.Foundation.NSUserDefaults

actual class RecentSearchStorage actual constructor() {
    actual fun saveSearches(searches: List<RecentSearch>) {
        val serialized = searches.joinToString(";") {
            "${it.from}|${it.to}|${it.passengers}|${it.date}|${it.timestamp}"
        }
        NSUserDefaults.standardUserDefaults.setObject(serialized, forKey = KEY_SEARCHES)
    }

    actual fun loadSearches(): List<RecentSearch>? {
        val raw = NSUserDefaults.standardUserDefaults.stringForKey(KEY_SEARCHES) ?: return null
        if (raw.isBlank()) return emptyList()
        return try {
            raw.split(";").filter { it.isNotBlank() }.map { part ->
                val tokens = part.split("|")
                RecentSearch(
                    from = tokens.getOrElse(0) { "" },
                    to = tokens.getOrElse(1) { "" },
                    passengers = tokens.getOrElse(2) { "1" }.toIntOrNull() ?: 1,
                    date = tokens.getOrElse(3) { "Today" },
                    timestamp = tokens.getOrElse(4) { "0" }.toLongOrNull() ?: 0L
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    actual fun clearSearches() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_SEARCHES)
    }

    companion object {
        private const val KEY_SEARCHES = "juko_recent_searches"
    }
}
