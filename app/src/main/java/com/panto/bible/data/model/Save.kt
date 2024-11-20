package com.panto.bible.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save")
data class Save(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String,
    val page: Int,
    val verse: Int,
    val color: Int,
    val title: String
)