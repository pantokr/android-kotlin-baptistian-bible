package com.panto.bible.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hymns")
data class Hymn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sae: Int,
    val tongil: Int?,
    val title: String,
    val theme: String,
    val searcher: String,
    val file: String
)