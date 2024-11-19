package com.panto.bible.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panto.bible.data.api.DictionaryDataSource
import com.panto.bible.data.model.api.Dictionary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DictionaryViewModel : ViewModel() {
    private val dictionaryDataSource = DictionaryDataSource()

    private val _searchedItems = MutableStateFlow<List<Dictionary.Items>>(emptyList())
    val searchedItems = _searchedItems.asStateFlow()

    fun searchDictionary(query: String) {
        if (query.isBlank()) {
            _searchedItems.value = listOf()
            return
        }

        viewModelScope.launch {

            val dict = dictionaryDataSource.getDictionary(query = query)
            val filteredLinks = dict?.items?.filter { it.link.contains("categoryId=51387") }
            if (!filteredLinks.isNullOrEmpty()) {
                _searchedItems.value = filteredLinks
            } else {
                _searchedItems.value = listOf()
            }
        }
    }
}