package com.juko.app.core.data

import com.juko.app.core.model.RecentSearch
import com.juko.app.core.storage.RecentSearchStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Global reactive manager for Recent Searches stored in local device memory/storage.
 */
object RecentSearchesManager {

    private val storage = RecentSearchStorage()

    // Default seeded recent searches matching the design & user interface
    private val defaultSearches = listOf(
        RecentSearch(from = "Seohara", to = "Delhi", passengers = 2, date = "Today"),
        RecentSearch(from = "Seohara", to = "Pune", passengers = 3, date = "Today")
    )

    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    init {
        loadInitialSearches()
    }

    fun loadInitialSearches() {
        val loaded = storage.loadSearches()
        if (loaded != null) {
            _recentSearches.value = loaded
        } else {
            // First launch: initialize with the default searches and save to storage
            _recentSearches.value = defaultSearches
            storage.saveSearches(defaultSearches)
        }
    }

    /**
     * Records a new search or moves an existing search to the top of recent searches.
     */
    fun addSearch(
        from: String,
        to: String,
        passengers: Int = 1,
        date: String = "Today"
    ) {
        val cleanFrom = from.trim()
        val cleanTo = to.trim()
        if (cleanFrom.isBlank() || cleanTo.isBlank()) return

        val now = Clock.System.now().toEpochMilliseconds()
        val newSearch = RecentSearch(
            from = cleanFrom,
            to = cleanTo,
            passengers = passengers.coerceIn(1, 6),
            date = date.ifBlank { "Today" },
            timestamp = now
        )

        // Remove any existing entry for the same route (case-insensitive)
        val filtered = _recentSearches.value.filterNot {
            it.from.equals(cleanFrom, ignoreCase = true) &&
            it.to.equals(cleanTo, ignoreCase = true)
        }

        // Prepend the new search and keep up to 10 most recent
        val updatedList = (listOf(newSearch) + filtered).take(10)
        _recentSearches.value = updatedList
        storage.saveSearches(updatedList)
    }

    /**
     * Removes an individual search from recent searches.
     */
    fun removeSearch(search: RecentSearch) {
        val updated = _recentSearches.value.filterNot {
            it.from.equals(search.from, ignoreCase = true) &&
            it.to.equals(search.to, ignoreCase = true)
        }
        _recentSearches.value = updated
        storage.saveSearches(updated)
    }

    /**
     * Clears all recent searches from local memory and storage.
     */
    fun clearAll() {
        _recentSearches.value = emptyList()
        storage.clearSearches()
    }
}
