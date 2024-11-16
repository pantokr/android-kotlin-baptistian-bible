package com.panto.bible.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Int,
    val page: Int,
    val verse: Int,
    val query: String
)