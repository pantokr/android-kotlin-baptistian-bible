package com.panto.bible.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.panto.bible.data.model.SearchHistory

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history WHERE page = :page AND verse = :verse AND query = :query")
    suspend fun deleteSearchHistory(page: Int, verse: Int, query: String)

    @Query("SELECT COUNT(*) FROM search_history WHERE page = :page AND verse = :verse AND query = :query")
    suspend fun isSearchHistoryExist(page: Int, verse: Int, query: String): Int

    @Query("SELECT * FROM search_history ORDER BY time DESC LIMIT 100")
    suspend fun getRecentSearchHistories(): List<SearchHistory>
}