package com.panto.bible.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.panto.bible.data.model.History

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertHistory(history: History)

    @Query("SELECT COUNT(*) FROM history WHERE page = :page AND verse = :verse AND query = :query")
    suspend fun isHistoryExist(page: Int, verse: Int, query: String): Int

    @Query("SELECT * FROM history  ORDER BY time DESC LIMIT 100")
    suspend fun getRecentHistories(): List<History>

    @Query("DELETE FROM history WHERE page = :page AND verse = :verse AND query = :query")
    suspend fun deleteHistoryByQuery(page: Int, verse: Int, query: String)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()
}