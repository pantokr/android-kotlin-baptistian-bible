package com.panto.bible.data.local

import android.content.Context
import android.util.Log
import com.panto.bible.data.local.BibleConstant.TAG
import com.panto.bible.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class VerseLocalDataSource(private val context: Context) {

    suspend fun loadVersesFromCSV(fileName: String, version: String): Boolean =
        withContext(Dispatchers.IO) {
            val database = VerseDatabase.getDatabase(context, version)
            val verseDao = database.verseDao()

            val verseList = mutableListOf<Verse>()
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))

            val csvRegex = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")

            return@withContext try {
                reader.useLines { lines ->
                    lines.forEach { line ->
                        val values = line.split(csvRegex).map { it.trim('"') }

                        if (values.size >= 8) {
                            val verse = Verse(
                                book = values[0].toInt(),
                                chapter = values[1].toInt(),
                                page = values[2].toInt(),
                                verse = values[3].toInt(),
                                verseNumber = values[4],
                                textOriginal = values[5],
                                textRaw = values[6],
                                commentary = values.getOrNull(7),
                                subTitle = values.getOrNull(8),
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

    suspend fun getVerseByPageAndVerse(version: String, page: Int, verse: Int): Verse =
        withContext(Dispatchers.IO) {
            val database = VerseDatabase.getDatabase(context, version)
            val verseDao = database.verseDao()
            verseDao.getVerseByPageAndVerse(page, verse)
        }


    suspend fun getVerses(version: String, book: Int, chapter: Int): List<Verse> =
        withContext(Dispatchers.IO) {
            val database = VerseDatabase.getDatabase(context, version)
            val verseDao = database.verseDao()
            verseDao.getVerses(book, chapter)
        }

    suspend fun getVersesByPage(version: String, page: Int): List<Verse> =
        withContext(Dispatchers.IO) {
            val database = VerseDatabase.getDatabase(context, version)
            val verseDao = database.verseDao()
            verseDao.getVersesByPage(page)
        }

    suspend fun getVersesCount(version: String): Int =
        withContext(Dispatchers.IO) {
            val database = VerseDatabase.getDatabase(context, version)
            val verseDao = database.verseDao()
            verseDao.getVersesCount()
        }

    suspend fun searchVerses(version: String, searchTerm: String): List<Verse> =
        withContext(Dispatchers.IO) {
            val database = VerseDatabase.getDatabase(context, version)
            val verseDao = database.verseDao()

            verseDao.searchVersesByTextRaw(searchTerm)
        }
}
