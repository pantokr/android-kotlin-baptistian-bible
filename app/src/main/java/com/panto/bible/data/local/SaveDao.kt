package com.panto.bible.data.local

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

    @Query("SELECT * FROM save WHERE page = :page")
    suspend fun getSavesByPage(page: Int): List<Save>

    @Query("SELECT * FROM save ORDER BY time DESC")
    suspend fun getAllSaves(): List<Save>

    @Query("DELETE FROM save WHERE page = :page AND verse = :verse")
    suspend fun deleteSaves(page: Int, verse: Int)

    @Query("DELETE FROM save")
    suspend fun deleteAllSaves()
}