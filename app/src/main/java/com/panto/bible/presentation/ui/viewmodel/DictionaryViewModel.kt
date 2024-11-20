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

    private val _isSearched = MutableStateFlow(false)
    val isSearched = _isSearched.asStateFlow()

    fun searchDictionary(query: String) {
        if (query.isBlank()) {
            _isSearched.value = false
            _searchedItems.value = listOf()
            return
        }

        viewModelScope.launch {

            val dict = dictionaryDataSource.getDictionary(query = query)
            if (dict == null) {
                _searchedItems.value = listOf()
            } else {
                val fDict = dict.items.filter { it.link.contains("categoryId=51387") }.map { item ->
                    item.copy(
                        title = item.title.replace("<b>", "").replace("</b>", ""),
                        description = item.description.replace("<b>", "").replace("</b>", "")
                    )
                }
                _isSearched.value = true
                _searchedItems.value = fDict
            }
        }
    }
}