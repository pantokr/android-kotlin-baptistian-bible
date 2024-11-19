package com.panto.bible.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.panto.bible.data.local.dao.HistoryDao
import com.panto.bible.data.local.dao.HymnDao
import com.panto.bible.data.local.dao.SaveDao
import com.panto.bible.data.local.dao.VerseDao
import com.panto.bible.data.model.History
import com.panto.bible.data.model.Hymn
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse

@Database(
    entities = [Verse::class, History::class, Save::class, Hymn::class],
    version = 1,
    exportSchema = false
)
abstract class BibleDatabase : RoomDatabase() {
    abstract fun verseDao(): VerseDao
    abstract fun historyDao(): HistoryDao
    abstract fun saveDao(): SaveDao
    abstract fun hymnDao(): HymnDao

    companion object {
        @Volatile
        private var instances = mutableMapOf<String, BibleDatabase?>()

        fun getDatabase(context: Context, version: String): BibleDatabase {
            val dbName = when (version) {
                "han" -> "han.db"
                "gae" -> "gae.db"
                "history" -> "history.db"
                "save" -> "save.db"
                "hymns" -> "hymns.db"
                else -> "default.db"
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
