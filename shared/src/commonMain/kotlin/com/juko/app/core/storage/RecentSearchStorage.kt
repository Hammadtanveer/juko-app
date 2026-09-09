package com.juko.app.core.storage

import com.juko.app.core.model.RecentSearch

expect class RecentSearchStorage() {
    fun saveSearches(searches: List<RecentSearch>)
    fun loadSearches(): List<RecentSearch>?
    fun clearSearches()
}

