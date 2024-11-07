package com.panto.bible.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.panto.bible.data.model.SearchHistory
import com.panto.bible.data.model.Verse

@Database(entities = [Verse::class, SearchHistory::class], version = 1, exportSchema = false)
abstract class VerseDatabase : RoomDatabase() {
    abstract fun verseDao(): VerseDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var instances = mutableMapOf<String, VerseDatabase?>()

        fun getDatabase(context: Context, version: String): VerseDatabase {
            val dbName = when (version) {
                "han" -> "han_database.db"
                "gae" -> "gae_database.db"
                "history" -> "history_database.db"
                else -> "default_database.db"
            }

            return instances.getOrPut(dbName) {
                synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        VerseDatabase::class.java,
                        dbName
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            } ?: throw IllegalStateException("Database not initialized")
        }
    }
}
