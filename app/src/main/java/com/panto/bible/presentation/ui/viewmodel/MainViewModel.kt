package com.panto.bible.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG_SHORT
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR_SHORT
import com.panto.bible.data.local.BibleConstant.HAN_GAE_DIFFERENCE_REFERENCE
import com.panto.bible.data.local.BibleConstant.LANGUAGE_LIST
import com.panto.bible.data.local.BibleConstant.TAG
import com.panto.bible.data.local.BibleConstant.VERSE_COUNT_LIST
import com.panto.bible.data.local.BibleConstant.VERSION_LIST
import com.panto.bible.data.local.PreferenceManager
import com.panto.bible.data.local.VerseLocalDataSource
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val verseLocalDataSource: VerseLocalDataSource,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _currentVersion = MutableStateFlow(preferenceManager.currentVersion)
    val currentVersion = _currentVersion.asStateFlow()

    private val _currentSubVersion = MutableStateFlow(preferenceManager.currentSubVersion)
    val currentSubVersion = _currentSubVersion.asStateFlow()

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses = _verses.asStateFlow()

    val _subVerses = MutableStateFlow<List<String>>(emptyList())
    val subVerses = _subVerses.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedVerse = MutableStateFlow(-1)
    val selectedVerse = _selectedVerse.asStateFlow()

    private val _searchedVerses = MutableStateFlow<List<Verse>>(emptyList())
    val searchedVerses = _searchedVerses.asStateFlow()

    private val _historyQueries = MutableStateFlow<List<String>>(emptyList())
    val historyQueries = _historyQueries.asStateFlow()

    private val _historyVerses = MutableStateFlow<List<Verse>>(emptyList())
    val historyVerses = _historyVerses.asStateFlow()

    private val _saved = MutableStateFlow<List<Save>>(emptyList())
    val saved = _saved.asStateFlow()

    private val _currentPage = MutableStateFlow(preferenceManager.currentPage)
    val currentPage = _currentPage.asStateFlow()

    private val _currentBookList = MutableStateFlow(BOOK_LIST_KOR)
    val currentBookList = _currentBookList.asStateFlow()

    private val _currentBookShortList = MutableStateFlow(BOOK_LIST_KOR_SHORT)
    val currentBookShortList = _currentBookShortList.asStateFlow()

    init {
        _currentBookList.value =
            if (LANGUAGE_LIST[0].contains(_currentVersion.value)) BOOK_LIST_KOR else BOOK_LIST_ENG
        _currentBookShortList.value =
            if (LANGUAGE_LIST[0].contains(_currentVersion.value)) BOOK_LIST_KOR_SHORT else BOOK_LIST_ENG_SHORT
        viewModelScope.launch {
            loadVerses()
        }
    }

    fun updateVersion(version: Int) {
        _currentVersion.value = version
        preferenceManager.currentVersion = version

        if (_currentSubVersion.value == version) {
            updateSubVersion(-1)
        }
        getVersesByPage(_currentPage.value)
    }

    fun updateSubVersion(subVersion: Int) {
        _currentSubVersion.value = subVersion
        preferenceManager.currentSubVersion = subVersion

        _subVerses.value = listOf()
        getSubVersesByPage(_currentPage.value)
    }

    fun getVersesByPage(page: Int) {
        viewModelScope.launch {
            val verses = verseLocalDataSource.getVersesByPage(_currentVersion.value, page)
            _verses.value = verses

            _currentPage.value = page
            preferenceManager.currentPage = page

            getSubVersesByPage(page)
            getSavesByPage(page)
        }
    }


    fun getVersesByBookAndChapter(book: Int, chapter: Int) {
        viewModelScope.launch {
            val verses =
                verseLocalDataSource.getVersesByBookAndChapter(_currentVersion.value, book, chapter)
            _verses.value = verses

            val p = verses[0].page
            _currentPage.value = p
            preferenceManager.currentPage = p

            getSubVersesByPage(p)
            getSavesByPage(p)
        }
    }

    fun getSubVersesByPage(page: Int) {
        viewModelScope.launch {
            if (_currentSubVersion.value != -1) {
                val verses = verseLocalDataSource.getVersesByPage(_currentSubVersion.value, page)
                _subVerses.value = verses.map { it.textRaw }

                handleBibleVersionDifference()
            }
        }
    }

    fun searchVerses(query: String) {
        query.also {
            _searchQuery.value = it
        }

        viewModelScope.launch {
            if (_searchQuery.value.length >= 2) {
                _searchedVerses.value =
                    verseLocalDataSource.searchVerses(_currentVersion.value, query)

            } else if (_searchQuery.value.isBlank()) {
                _searchedVerses.value = listOf()
            }
        }
    }

    fun selectVerse(index: Int) {
        _selectedVerse.value = index
    }

    fun insertHistory(verse: Verse, query: String) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis().toInt()
                verseLocalDataSource.deleteHistoryByQuery(verse, query)

                verseLocalDataSource.insertHistory(
                    time = currentTime, page = verse.page, verse = verse.verse, query = query
                )
            } catch (e: Exception) {
                Log.e(TAG, "검색 기록 추가 실패: ${e.message}")
            }
        }
    }


    fun getHistories() {
        viewModelScope.launch {
            try {
                val searchHistories = verseLocalDataSource.getRecentHistories()

                val queryList = mutableListOf<String>()
                val verseList = mutableListOf<Verse>()
                searchHistories.forEach { history ->
                    val verse = verseLocalDataSource.getVerseByPageAndVerse(
                        version = _currentVersion.value, page = history.page, verse = history.verse
                    )
                    queryList.add(history.query)
                    verseList.add(verse)
                }

                _historyQueries.value = queryList
                _historyVerses.value = verseList
            } catch (e: Exception) {
                Log.e(TAG, "검색 히스토리 로드 실패: ${e.message}")
            }
        }
    }

    fun deleteHistory(verse: Verse, query: String) {
        viewModelScope.launch {
            verseLocalDataSource.deleteHistoryByQuery(verse, query)
            getHistories()
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            verseLocalDataSource.deleteAllHistory()
            getHistories()
        }
    }

    fun insertSave(verse: Verse, color: Int) {
        viewModelScope.launch {
            if (color == -1) {
                verseLocalDataSource.deleteSaves(verse.page, verse.verse)
            } else {
                try {
                    val currentTime = System.currentTimeMillis().toInt()
                    verseLocalDataSource.deleteSaves(verse.page, verse.verse)

                    verseLocalDataSource.insertSave(
                        time = currentTime, page = verse.page, verse = verse.verse, color = color
                    )

                } catch (e: Exception) {
                    Log.e(TAG, "저장 추가 실패: ${e.message}")
                }
            }

            getSavesByPage(_currentPage.value)
        }
    }

    fun getSavesByPage(page: Int) {
        viewModelScope.launch {
            val saves = verseLocalDataSource.getSavesByPage(page)
            _saved.value = saves
        }
    }

    fun getAllSavesGroupedByTime() {
        viewModelScope.launch {
            val saves = verseLocalDataSource.getAllSaves()
            saves.groupBy { it.time }.values.toList()
        }
    }

    private fun loadVerses() {
        viewModelScope.launch {
            for (version in VERSION_LIST) {
                if (verseLocalDataSource.getVersesCount(VERSION_LIST.indexOf(version)) == 0) {
                    verseLocalDataSource.loadVersesFromCSV(
                        "${version}.csv", VERSION_LIST.indexOf(version)
                    )
                }
            }
            while (true) {
                var currentCount = 0
                for (version in VERSION_LIST) {
                    currentCount += verseLocalDataSource.getVersesCount(VERSION_LIST.indexOf(version))
                }
                if (currentCount >= VERSE_COUNT_LIST.sum()) {
                    Log.d(TAG, "모든 구절 로드 완료")
                    break
                }
                delay(500)
            }

            getVersesByPage(_currentPage.value)
            while (true) {
                if (_verses.value.isNotEmpty()) {
                    break
                }
                delay(500)
            }
            Log.d(TAG, "구절 출력 준비 완료")

            _isLoading.value = false
        }
    }

    private fun handleBibleVersionDifference() {
        val bVersion = _currentVersion.value
        val sVersion = _currentSubVersion.value

        val bIndex = _verses.value[0].book
        val cIndex = _verses.value[0].chapter

        var cur_ref = HAN_GAE_DIFFERENCE_REFERENCE.toMutableList()
        if (bVersion == 0 && sVersion == 1) {
            cur_ref = HAN_GAE_DIFFERENCE_REFERENCE.toMutableList()
        } else if (bVersion == 1 && sVersion == 0) {
            cur_ref = HAN_GAE_DIFFERENCE_REFERENCE.map {
                it.copy(offset = it.offset * -1)
            }.toMutableList()
        }

        val result = cur_ref.filter { it.book == bIndex && it.chapter == cIndex }
        result.forEach {
            val offset = it.offset
            val verse = it.verse
            if (offset == 1) {
                _subVerses.value =
                    _subVerses.value.take(verse) + (_subVerses.value[verse] + " " + _subVerses.value[verse + 1]) + _subVerses.value.drop(
                        verse + 2
                    )

            } else {
                _subVerses.value = _subVerses.value.toMutableList().apply {
                    if (verse + 1 in 0..size) {
                        add(verse + 1, "(없거나 이전 절에 포함됨)")
                    }
                }
            }
        }
    }
}

