package com.panto.bible.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.panto.bible.data.model.Verse

@Dao
interface VerseDao {
    @Insert
    suspend fun insertVerses(verses: List<Verse>): List<Long>

    @Query("SELECT * FROM verses WHERE page = :page AND verse = :verse")
    suspend fun getVerseByPageAndVerse(page: Int, verse: Int): Verse

    @Query("SELECT * FROM verses WHERE book = :book AND chapter = :chapter")
    suspend fun getVersesByBookAndChapter(book: Int, chapter: Int): List<Verse>

    @Query("SELECT * FROM verses WHERE page = :page")
    suspend fun getVersesByPage(page: Int): List<Verse>

    @Query("SELECT * FROM verses")
    suspend fun getAllVerses(): List<Verse>

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun getVersesCount(): Int

    @RawQuery
    suspend fun searchVerses(query: SimpleSQLiteQuery): List<Verse>
}
