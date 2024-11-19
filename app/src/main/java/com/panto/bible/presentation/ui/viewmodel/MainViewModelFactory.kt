package com.panto.bible.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panto.bible.data.local.LocalDataSource
import com.panto.bible.data.local.PreferenceManager

class MainViewModelFactory(
    private val localDataSource: LocalDataSource,
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(localDataSource, preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}