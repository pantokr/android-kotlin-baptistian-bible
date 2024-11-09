package com.panto.bible.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_ENG_SHORT
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR_SHORT
import com.panto.bible.data.local.BibleConstant.LANGUAGE_LIST
import com.panto.bible.data.local.BibleConstant.TAG
import com.panto.bible.data.local.BibleConstant.VERSE_COUNT_LIST
import com.panto.bible.data.local.BibleConstant.VERSION_LIST
import com.panto.bible.data.local.PreferenceManager
import com.panto.bible.data.local.VerseLocalDataSource
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

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses = _verses.asStateFlow()

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

    private val _currentPage = MutableStateFlow(preferenceManager.currentPage)
    val currentPage = _currentPage.asStateFlow()

    private val _currentVersion = MutableStateFlow(preferenceManager.currentVersion)
    val currentVersion = _currentVersion.asStateFlow()

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

    fun getVersesByBookChapter(book: Int, chapter: Int) {
        viewModelScope.launch {
            val verses =
                verseLocalDataSource.getVersesByBookAndChapter(_currentVersion.value, book, chapter)
            _verses.value = verses
            preferenceManager.currentPage = verses[0].page
        }
    }

    fun getVersesByPage(page: Int) {
        viewModelScope.launch {
            _currentPage.value = page
            val verses = verseLocalDataSource.getVersesByPage(_currentVersion.value, page)
            _verses.value = verses
            preferenceManager.currentPage = page
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

    fun addHistory(page: Int, verse: Int, query: String) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis().toInt()

                val isExist = verseLocalDataSource.isSearchHistoryExist(page, verse, query)

                if (isExist) {
                    verseLocalDataSource.deleteSearchHistory(page, verse, query)
                }

                verseLocalDataSource.insertSearchHistory(
                    time = currentTime, page = page, verse = verse, query = query
                )
            } catch (e: Exception) {
                Log.e(TAG, "검색 기록 추가 실패: ${e.message}")
            }
        }
    }


    fun getHistories() {
        viewModelScope.launch {
            try {
                val searchHistories = verseLocalDataSource.getRecentSearchHistories()

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

    private suspend fun loadVerses() {
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

            _isLoading.value = false
        }
    }
}
