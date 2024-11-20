package com.panto.bible.data.local

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.opencsv.CSVReader
import com.panto.bible.data.local.BibleConstant.BOOK_CHAPTER_COUNT_SUM_LIST
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG_SHORT
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR_SHORT
import com.panto.bible.data.local.BibleConstant.LANGUAGE_LIST
import com.panto.bible.data.local.BibleConstant.TAG
import com.panto.bible.data.local.BibleConstant.VERSION_LIST
import com.panto.bible.data.model.History
import com.panto.bible.data.model.Hymn
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class LocalDataSource(private val context: Context) {

    private fun hymnDao() = BibleDatabase.getDatabase(context, "hymns").hymnDao()
    private fun verseDao(version: Int) =
        BibleDatabase.getDatabase(context, VERSION_LIST[version]).verseDao()

    private fun saveDao() = BibleDatabase.getDatabase(context, "save").saveDao()
    private fun historyDao() = BibleDatabase.getDatabase(context, "history").historyDao()

    suspend fun loadVersesFromCSV(fileName: String, version: Int): Boolean =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, VERSION_LIST[version])
            val verseDao = database.verseDao()

            val verseList = mutableListOf<Verse>()
            val inputStream = context.assets.open(fileName)
            val reader = CSVReader(InputStreamReader(inputStream, "UTF-8"))

            val bookList =
                if (LANGUAGE_LIST[0].contains(version)) BOOK_LIST_KOR else BOOK_LIST_ENG
            val bookListShort =
                if (LANGUAGE_LIST[0].contains(version)) BOOK_LIST_KOR_SHORT else BOOK_LIST_ENG_SHORT

            return@withContext try {
                reader.use { csvReader ->
                    csvReader.forEach { values ->
                        try {
                            val p =
                                BOOK_CHAPTER_COUNT_SUM_LIST[values[0].toInt()] + values[1].toInt()
                            val s =
                                "${bookList[values[0].toInt()]} ${bookListShort[values[0].toInt()]} ${values[1].toInt() + 1}장 ${values[3]}절 ${values[5]}"

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

                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "Error processing row: ${values.joinToString(", ")} - ${e.message}"
                            )
                            throw e
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
        withContext(Dispatchers.IO) { verseDao(version).getVerseByPageAndVerse(page, verse) }

    suspend fun getVersesByPage(version: Int, page: Int): List<Verse> =
        withContext(Dispatchers.IO) {
            if (version == -1) emptyList() else verseDao(version).getVersesByPage(
                page
            )
        }


    suspend fun getVersesByBookAndChapter(version: Int, book: Int, chapter: Int): List<Verse> =
        withContext(Dispatchers.IO) {
            if (version == -1) emptyList() else verseDao(version).getVersesByBookAndChapter(
                book,
                chapter
            )
        }

    suspend fun getVersesCount(version: Int): Int =
        withContext(Dispatchers.IO) { verseDao(version).getVersesCount() }

    suspend fun searchVerses(version: Int, query: String): List<Verse> =
        withContext(Dispatchers.IO) {
            val queries = query.split(" ")
            val queryConditions = queries.joinToString(" AND ") { "searcher LIKE ?" }
            val sql = "SELECT * FROM verses WHERE $queryConditions"
            val queryArgs = queries.map { "%$it%" }.toTypedArray()
            val dynamicQuery = SimpleSQLiteQuery(sql, queryArgs)
            verseDao(version).searchVerses(dynamicQuery)
        }

    // 기록
    suspend fun insertHistory(time: Int, page: Int, verse: Int, query: String) =
        withContext(Dispatchers.IO) {
            val history = History(time = time, page = page, verse = verse, query = query)
            historyDao().insertHistory(history)
        }

    suspend fun getRecentHistories(): List<History> =
        withContext(Dispatchers.IO) { historyDao().getRecentHistories() }

    suspend fun deleteHistoryByQuery(verse: Verse, query: String) {
        withContext(Dispatchers.IO) {
            historyDao().deleteHistoryByQuery(verse.page, verse.verse, query)
        }
    }

    suspend fun deleteAllHistory() = withContext(Dispatchers.IO) { historyDao().deleteAllHistory() }


    // 저장
    suspend fun insertSave(time: String, page: Int, verse: Int, title: String) =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, "save")
            val saveDao = database.saveDao()

            val save = Save(
                time = time, page = page, verse = verse, color = -1, title = title
            )
            saveDao.insertSave(save)
        }

    suspend fun insertHighlight(time: String, page: Int, verse: Int, color: Int) =
        withContext(Dispatchers.IO) {
            val database = BibleDatabase.getDatabase(context, "save")
            val saveDao = database.saveDao()

            val save = Save(
                time = time, page = page, verse = verse, color = color, title = ""
            )
            saveDao.insertSave(save)
        }

    suspend fun getSavesByPage(page: Int): List<Save> =
        withContext(Dispatchers.IO) {
            saveDao().getSavesByPage(page)
        }

    suspend fun getHighlightsByPage(page: Int): List<Save> =
        withContext(Dispatchers.IO) {
            saveDao().getHighlightsByPage(page)
        }

    suspend fun getAllSaves(): List<Save> = withContext(Dispatchers.IO) { saveDao().getAllSaves() }

    suspend fun deleteSave(page: Int, verse: Int, isHighlight: Boolean = false) {
        withContext(Dispatchers.IO) {
            saveDao().deleteSave(page, verse)
        }
    }

    suspend fun deleteAllSaves() {
        withContext(Dispatchers.IO) {
            saveDao().deleteAllSaves()
        }
    }

    suspend fun deleteHighlight(page: Int, verse: Int) {
        withContext(Dispatchers.IO) {
            saveDao().deleteHighlight(page, verse)
        }
    }

    // 찬송가
    suspend fun loadHymnsFromCSV(): Boolean = withContext(Dispatchers.IO) {
        val hymnList = mutableListOf<Hymn>()
        val inputStream = context.assets.open("hymns.csv")
        val reader = CSVReader(InputStreamReader(inputStream))

        return@withContext try {
            reader.readAll().forEach { values ->
                val cleanedValues = values.map { it.trim().replace("\uFEFF", "") }
                val searcher = buildSearcher(cleanedValues)

                val hymn = Hymn(
                    sae = cleanedValues[0].toInt(),
                    tongil = cleanedValues.getOrElse(1) { "-1" }.takeIf { it.isNotBlank() }
                        ?.toInt() ?: -1,
                    title = cleanedValues[2],
                    theme = cleanedValues[3],
                    searcher = searcher,
                    file = buildFilePath(cleanedValues[0])
                )
                hymnList.add(hymn)
            }
            hymnDao().insertHymns(hymns = hymnList)
            true
        } catch (e: Exception) {
            Log.e(TAG, "CSV 로드 중 오류 발생: ${e.message}")
            false
        } finally {
            reader.close()
        }
    }


    private fun buildSearcher(values: List<String>): String {
        return "새찬송가 ${values[0]}장 ${values[2]} ${values[3]}" +
                if (values[1].isNotBlank()) "통일찬송가 ${values[1]}장" else ""
    }

    private fun buildFilePath(sae: String): String {
        return "hymn/${sae.padStart(3, '0')}.jpg"
    }

    suspend fun getHymnsCount(): Int = withContext(Dispatchers.IO) {
        hymnDao().getHymnsCount()
    }

    suspend fun searchHymns(query: String): List<Hymn> =
        withContext(Dispatchers.IO) {
            val queries = query.split(" ").map { "%$it%" }
            val sql = if (queries.isNotEmpty()) {
                val queryConditions = queries.joinToString(" AND ") { "searcher LIKE ?" }
                "SELECT * FROM hymns WHERE $queryConditions"
            } else {
                "SELECT * FROM hymns"
            }

            val dynamicQuery = SimpleSQLiteQuery(sql, queries.toTypedArray())
            hymnDao().searchHymns(dynamicQuery)
        }
}
