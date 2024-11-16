package com.panto.bible.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.panto.bible.data.model.History
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse

@Database(
    entities = [Verse::class, History::class, Save::class],
    version = 1,
    exportSchema = false
)
abstract class BibleDatabase : RoomDatabase() {
    abstract fun verseDao(): VerseDao
    abstract fun historyDao(): HistoryDao
    abstract fun saveDao(): SaveDao

    companion object {
        @Volatile
        private var instances = mutableMapOf<String, BibleDatabase?>()

        fun getDatabase(context: Context, version: String): BibleDatabase {
            val dbName = when (version) {
                "han" -> "han_database.db"
                "gae" -> "gae_database.db"
                "history" -> "history_database.db"
                "save" -> "save_database.db"
                else -> "default_database.db"
            }

            return instances.getOrPut(dbName) {
                synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        BibleDatabase::class.java,
                        dbName
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            } ?: throw IllegalStateException("Database not initialized")
        }
    }
}
