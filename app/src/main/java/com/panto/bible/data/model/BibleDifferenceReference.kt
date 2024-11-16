package com.panto.bible.data.model

data class BibleDifferenceReference(
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val offset: Int
)
