package com.panto.bible.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.panto.bible.data.model.Hymn

@Dao
interface HymnDao {
    @Insert
    suspend fun insertHymns(hymns: List<Hymn>): List<Long>

    @RawQuery
    suspend fun searchHymns(query: SimpleSQLiteQuery): List<Hymn>

    @Query("SELECT COUNT(*) FROM hymns")
    suspend fun getHymnsCount(): Int
}