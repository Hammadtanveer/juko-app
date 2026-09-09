package com.juko.app.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.juko.app.core.model.RecentSearch
import org.json.JSONArray
import org.json.JSONObject

actual class RecentSearchStorage actual constructor() {

    private fun getPrefs(): SharedPreferences? {
        return AndroidPlatformContext.context?.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    actual fun saveSearches(searches: List<RecentSearch>) {
        try {
            val prefs = getPrefs() ?: return
            val jsonArray = JSONArray()
            for (search in searches) {
                val obj = JSONObject().apply {
                    put("from", search.from)
                    put("to", search.to)
                    put("passengers", search.passengers)
                    put("date", search.date)
                    put("timestamp", search.timestamp)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_SEARCHES, jsonArray.toString()).apply()
        } catch (e: Exception) {
            // Ignore failure
        }
    }

    actual fun loadSearches(): List<RecentSearch>? {
        val prefs = getPrefs() ?: return null
        val raw = prefs.getString(KEY_SEARCHES, null) ?: return null
        return try {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<RecentSearch>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    RecentSearch(
                        from = obj.optString("from"),
                        to = obj.optString("to"),
                        passengers = obj.optInt("passengers", 1),
                        date = obj.optString("date", "Today"),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
            list
        } catch (e: Exception) {
            null
        }
    }

    actual fun clearSearches() {
        try {
            getPrefs()?.edit()?.remove(KEY_SEARCHES)?.apply()
        } catch (e: Exception) {
            // Ignore failure
        }
    }

    companion object {
        private const val PREFS_NAME = "juko_local_storage"
        private const val KEY_SEARCHES = "recent_searches"
    }
}
