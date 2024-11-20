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
import com.panto.bible.data.local.BibleConstant.VERSION_LIST
import com.panto.bible.data.local.LocalDataSource
import com.panto.bible.data.local.PreferenceManager
import com.panto.bible.data.model.Hymn
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(
    private val localDataSource: LocalDataSource, private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _currentVersion = MutableStateFlow(preferenceManager.currentVersion)
    val currentVersion = _currentVersion.asStateFlow()

    private val _currentSubVersion = MutableStateFlow(preferenceManager.currentSubVersion)
    val currentSubVersion = _currentSubVersion.asStateFlow()

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses = _verses.asStateFlow()

    private val _subVerses = MutableStateFlow<List<String>>(emptyList())
    val subVerses = _subVerses.asStateFlow()

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

    private val _allSaved = MutableStateFlow<List<List<Save>>>(emptyList())
    val allSaved = _allSaved.asStateFlow()

    private val _searchedHymns = MutableStateFlow<List<Hymn>>(emptyList())
    val searchedHymns = _searchedHymns.asStateFlow()

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
            loadHymns()
        }
    }

    fun updateVersion(version: Int) {
        _currentVersion.value = version
        preferenceManager.currentVersion = version

        if (_currentSubVersion.value == version) {
            updateSubVersion(-1)
        }

        if (LANGUAGE_LIST[0].contains(version)) {
            _currentBookList.value = BOOK_LIST_KOR
            _currentBookShortList.value = BOOK_LIST_KOR_SHORT
        } else {
            _currentBookList.value = BOOK_LIST_ENG
            _currentBookShortList.value = BOOK_LIST_ENG_SHORT
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
            val verses = localDataSource.getVersesByPage(_currentVersion.value, page)
            _verses.value = verses

            _currentPage.value = page
            preferenceManager.currentPage = page

            getSubVersesByPage(page)
            getHighlightsByPage(page)
        }
    }


    fun getVersesByBookAndChapter(book: Int, chapter: Int) {
        viewModelScope.launch {
            val verses =
                localDataSource.getVersesByBookAndChapter(_currentVersion.value, book, chapter)
            _verses.value = verses

            val p = verses[0].page
            _currentPage.value = p
            preferenceManager.currentPage = p

            getSubVersesByPage(p)
            getHighlightsByPage(p)
        }
    }

    fun getSubVersesByPage(page: Int) {
        viewModelScope.launch {
            if (_currentSubVersion.value != -1) {
                val verses = localDataSource.getVersesByPage(_currentSubVersion.value, page)
                _subVerses.value = verses.map { it.textRaw }
            }
        }
    }

    fun searchVerses(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                _searchedVerses.value = localDataSource.searchVerses(_currentVersion.value, query)

            } else {
                _searchedVerses.value = listOf()
            }
        }
    }

    fun searchHymns(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                _searchedHymns.value = localDataSource.searchHymns(query = query)
            } else if (query.isBlank()) {
                _searchedHymns.value = localDataSource.searchHymns(query = " ") // 모두 찾기
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
                localDataSource.deleteHistoryByQuery(verse, query)

                localDataSource.insertHistory(
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
                val searchHistories = localDataSource.getRecentHistories()

                val queryList = mutableListOf<String>()
                val verseList = mutableListOf<Verse>()
                searchHistories.forEach { history ->
                    val verse = localDataSource.getVerseByPageAndVerse(
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
            localDataSource.deleteHistoryByQuery(verse, query)
            getHistories()
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch {
            localDataSource.deleteAllHistory()
            getHistories()
        }
    }

    fun insertSaves(sVerses: List<Verse>, title: String) {
        viewModelScope.launch {
            try {
                val currentTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm"))

                val t = if (title.isBlank()) currentTime.substringAfter('.') else title

                sVerses.forEach {
                    localDataSource.insertSave(
                        time = currentTime,
                        page = it.page,
                        verse = it.verse,
                        title = t
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "저장 추가 실패: ${e.message}")
            }
        }
    }

    fun insertHighlights(hVerses: List<Verse>, color: Int) {
        viewModelScope.launch {
            if (color == -1) {
                hVerses.forEach {
                    localDataSource.deleteHighlight(it.page, it.verse)
                }
            } else {
                try {
                    val currentTime =
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"))

                    hVerses.forEach {
                        localDataSource.deleteHighlight(it.page, it.verse)
                        localDataSource.insertHighlight(
                            time = currentTime,
                            page = it.page,
                            verse = it.verse,
                            color = color,
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "저장 추가 실패: ${e.message}")
                }
            }

            getHighlightsByPage(_currentPage.value)
        }
    }

    fun getAllSaves() {
        viewModelScope.launch {
            val saves = localDataSource.getAllSaves()
            _allSaved.value = saves.groupBy { it.time }.values.toList()
        }
    }

    fun getHighlightsByPage(page: Int) {
        viewModelScope.launch {
            val saves = localDataSource.getHighlightsByPage(page)
            _saved.value = saves
        }
    }

    fun deleteSaves(saves: List<Save>) {
        viewModelScope.launch {
            saves.forEach { it ->
                localDataSource.deleteSave(it.page, it.verse)
            }
            getAllSaves()
        }
    }

    fun deleteAllSaves() {
        viewModelScope.launch {
            localDataSource.deleteAllSaves()
            getAllSaves()
        }
    }

    private fun loadVerses() {
        viewModelScope.launch {
            var currentTotal = 0
            for (version in VERSION_LIST) {
                val currentCount = localDataSource.getVersesCount(VERSION_LIST.indexOf(version))
                if (currentCount == 0) {
                    Log.d(TAG, "${version} 구절 로드 시작")
                    localDataSource.loadVersesFromCSV(
                        "${version}.csv", VERSION_LIST.indexOf(version)
                    )
                    Log.d(TAG, "${version} ${currentCount} 페이지 구절 로드 종료")
                    currentTotal += currentCount
                }
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

    private fun loadHymns() {
        viewModelScope.launch {
            if (localDataSource.getHymnsCount() == 0) {
                localDataSource.loadHymnsFromCSV()
            }
        }
    }
}

