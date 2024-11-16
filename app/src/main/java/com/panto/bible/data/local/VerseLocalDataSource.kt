package com.panto.bible.data.local

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.panto.bible.data.local.BibleConstant.BOOK_CHAPTER_COUNT_SUM_LIST
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG_SHORT
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR_SHORT
import com.panto.bible.data.local.BibleConstant.LANGUAGE_LIST
import com.panto.bible.data.local.BibleConstant.TAG
import com.panto.bible.data.local.BibleConstant.VERSION_LIST
import com.panto.bible.data.model.History
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class VerseLocalDataSource(private val context: Context) {

    suspend fun loadVersesFromCSV(fileName: String, version: Int): Boolean =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
            val verseDao = database.verseDao()

            val verseList = mutableListOf<Verse>()
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val csvRegex = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")

            return@withContext try {
                reader.useLines { lines ->
                    lines.forEach { line ->
                        val values = line.split(csvRegex).map { it.trim('"') }
                        val bookList =
                            if (LANGUAGE_LIST[0].contains(version)) BOOK_LIST_KOR else BOOK_LIST_ENG
                        val bookListShort =
                            if (LANGUAGE_LIST[0].contains(version)) BOOK_LIST_KOR_SHORT else BOOK_LIST_ENG_SHORT

                        val p = BOOK_CHAPTER_COUNT_SUM_LIST[values[0].toInt()] + values[1].toInt()
                        val s =
                            "${bookList[values[0].toInt()]} ${bookListShort[values[0].toInt()]} ${values[1].toInt() + 1}장 ${values[3]}절 ${values[5]}"
                        if (values.size >= 8) {
                            val verse = Verse(
                                book = values[0].toInt(),
                                chapter = values[1].toInt(),
                                verse = values[2].toInt(),
                                verseNumber = values[3],
                                textOriginal = values[4],
                                textRaw = values[5],
                                commentary = values.getOrNull(6),
                                subTitle = values.getOrNull(7),
                                page = p,
                                searcher = s,
                            )
                            verseList.add(verse)
                        }
                    }
                }
                verseDao.insertVerses(verseList)
                true
            } catch (e: Exception) {
                Log.e(TAG, "CSV 로드 중 오류 발생: ${e.message}")
                false
            }
        }

    suspend fun getVerseByPageAndVerse(version: Int, page: Int, verse: Int): Verse =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
            val verseDao = database.verseDao()
            verseDao.getVerseByPageAndVerse(page, verse)
        }

    suspend fun getVersesByPage(version: Int, page: Int): List<Verse> =
        withContext(Dispatchers.IO) {
            if (version == -1) {
                listOf<Verse>()
            } else {
                val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
                val verseDao = database.verseDao()
                verseDao.getVersesByPage(page)
            }
        }

    suspend fun getVersesByBookAndChapter(version: Int, book: Int, chapter: Int): List<Verse> =
        withContext(Dispatchers.IO) {
            if (version == -1) {
                listOf<Verse>()
            } else {
                val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
                val verseDao = database.verseDao()
                verseDao.getVersesByBookAndChapter(book, chapter)
            }
        }

    suspend fun getVersesCount(version: Int): Int = withContext(Dispatchers.IO) {
        val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
        val verseDao = database.verseDao()
        verseDao.getVersesCount()
    }

    suspend fun searchVerses(version: Int, query: String): List<Verse> =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
            val verseDao = database.verseDao()

            val queries = query.split(" ")

            val queryConditions = queries.joinToString(" AND ") { "searcher LIKE '%' || ? || '%'" }
            val sql = "SELECT * FROM verses WHERE $queryConditions"

            val queryArgs = queries.toTypedArray()
            val dynamicQuery = SimpleSQLiteQuery(sql, queryArgs)

            verseDao.searchVerses(dynamicQuery)
        }

    suspend fun insertHistory(time: Int, page: Int, verse: Int, query: String) =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, "history")
            val historyDao = database.historyDao()

            val history = History(
                time = time, page = page, verse = verse, query = query
            )

            historyDao.insertHistory(history)
        }

    suspend fun getRecentHistories(): List<History> = withContext(Dispatchers.IO) {
        val database = BibleDatabase.getDatabase(context, "history")
        val historyDao = database.historyDao()
        historyDao.getRecentHistories()
    }

    suspend fun deleteHistoryByQuery(verse: Verse, query: String) {
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, "history")
            val historyDao = database.historyDao()
            historyDao.deleteHistoryByQuery(verse.page, verse.verse, query)
        }
    }

    suspend fun deleteAllHistory() = withContext(Dispatchers.IO) {
        val database = BibleDatabase.getDatabase(context, "history")
        val historyDao = database.historyDao()
        historyDao.deleteAllHistory()
    }

    suspend fun insertSave(time: Int, page: Int, verse: Int, color: Int) =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, "save")
            val saveDao = database.saveDao()

            val save = Save(
                time = time, page = page, verse = verse, color = color
            )
            saveDao.insertSave(save)
        }

    suspend fun getSavesByPage(page: Int): List<Save> = withContext(Dispatchers.IO) {
        val database = BibleDatabase.getDatabase(context, "save")
        val saveDao = database.saveDao()
        saveDao.getSavesByPage(page)
    }

    suspend fun getAllSaves(): List<Save> = withContext(Dispatchers.IO) {
        val database = BibleDatabase.getDatabase(context, "save")
        val saveDao = database.saveDao()
        saveDao.getAllSaves()
    }

    suspend fun deleteSaves(page: Int, verse: Int) {
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, "save")
            val saveDao = database.saveDao()
            saveDao.deleteSaves(page, verse)
        }
    }
}
