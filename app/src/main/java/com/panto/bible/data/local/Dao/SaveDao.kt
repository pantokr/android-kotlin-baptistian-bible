package com.panto.bible.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.panto.bible.data.model.Save

@Dao
interface SaveDao {
    @Insert
    suspend fun insertSave(save: Save)

    @Query("SELECT COUNT(*) FROM save WHERE page = :page AND verse = :verse")
    suspend fun isSaveExist(page: Int, verse: Int): Int

    @Query("SELECT * FROM save WHERE page = :page AND color = -1")
    suspend fun getSavesByPage(page: Int): List<Save>

    @Query("SELECT * FROM save WHERE page = :page AND color != -1")
    suspend fun getHighlightsByPage(page: Int): List<Save>

    @Query("SELECT * FROM save WHERE color = -1 ORDER BY time DESC")
    suspend fun getAllSaves(): List<Save>

    @Query("DELETE FROM save WHERE page = :page AND verse = :verse AND color = -1")
    suspend fun deleteSave(page: Int, verse: Int)

    @Query("DELETE FROM save WHERE page = :page AND verse = :verse AND color != -1")
    suspend fun deleteHighlight(page: Int, verse: Int)

    @Query("DELETE FROM save")
    suspend fun deleteAllSaves()
}