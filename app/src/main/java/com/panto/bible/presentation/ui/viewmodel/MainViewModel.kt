package com.panto.bible.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val currentVersion = _currentVersion


    init {
        viewModelScope.launch {
            loadVerses()
        }
    }

    fun getVerses(book: Int, chapter: Int) {
        viewModelScope.launch {
            val verses =
                verseLocalDataSource.getVerses(currentVersion.value ?: "han", book, chapter)
            _verses.value = verses
            preferenceManager.currentPage = verses[0].page
        }
    }


    fun getVersesByPage(page: Int) {
        viewModelScope.launch {
            _currentPage.value = page
            val verses = verseLocalDataSource.getVersesByPage(currentVersion.value ?: "han", page)
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
                val filteredVerses =
                    verseLocalDataSource.searchVerses(currentVersion.value ?: "han", query)
                _searchedVerses.value = filteredVerses
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
                    time = currentTime,
                    page = page,
                    verse = verse,
                    query = query
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
                        version = _currentVersion.value ?: "han",
                        page = history.page,
                        verse = history.verse
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
                if (verseLocalDataSource.getVersesCount(version) == 0) {
                    verseLocalDataSource.loadVersesFromCSV("${version}.csv", version)
                }
            }
            while (true) {
                var currentCount = 0
                for (version in VERSION_LIST) {
                    currentCount += verseLocalDataSource.getVersesCount(version)
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
