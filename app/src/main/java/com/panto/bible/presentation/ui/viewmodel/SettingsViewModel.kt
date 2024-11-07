package com.panto.bible.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.panto.bible.data.local.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _fontSize = MutableStateFlow(preferenceManager.fontSize)
    val fontSize = _fontSize.asStateFlow()

    private val _paragraphSpacing = MutableStateFlow(preferenceManager.paragraphSpacing)
    val paragraphSpacing = _paragraphSpacing.asStateFlow()

    private val _fontFamily = MutableStateFlow(preferenceManager.fontFamily)
    val fontFamily = _fontFamily.asStateFlow()

    private val _isDarkModeEnabled = MutableStateFlow(preferenceManager.isDarkModeEnabled)
    val isDarkModeEnabled = _isDarkModeEnabled.asStateFlow()

    fun updateFontSize(fontSize: Float) {
        _fontSize.value = fontSize
        preferenceManager.fontSize = fontSize
    }

    fun updateParagraphSpacing(spacing: Float) {
        _paragraphSpacing.value = spacing
        preferenceManager.paragraphSpacing = spacing
    }

    fun updateFontFamily(fontFamily: String?) {
        _fontFamily.value = fontFamily
        preferenceManager.fontFamily = fontFamily

    }

    fun toggleDarkMode(isEnabled: Boolean) {
        _isDarkModeEnabled.value = isEnabled
        preferenceManager.isDarkModeEnabled = isEnabled
    }
}
