package com.panto.bible.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verses")
data class Verse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val verseNumber: String,
    val textOriginal: String,
    val textRaw: String,
    val commentary: String?,
    val subTitle: String?,
    val page: Int,
    val searcher: String
)
