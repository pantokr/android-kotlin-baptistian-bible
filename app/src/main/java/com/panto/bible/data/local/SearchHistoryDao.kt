package com.panto.bible.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.panto.bible.data.model.SearchHistory

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history ORDER BY time DESC LIMIT 10")
    suspend fun getRecentSearchHistories(): List<SearchHistory>
}