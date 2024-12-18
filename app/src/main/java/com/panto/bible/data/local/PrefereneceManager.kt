package com.panto.bible.data.local

import android.content.Context

class PreferenceManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    var currentPage: Int
        get() = sharedPref.getInt("currentPage", 0)
        set(value) = sharedPref.edit().putInt("currentPage", value).apply()

    var currentVersion: Int
        get() = sharedPref.getInt("currentVersion", 0)
        set(value) = sharedPref.edit().putInt("currentVersion", value).apply()

    var currentSubVersion: Int
        get() = sharedPref.getInt("currentSubVersion", -1)
        set(value) = sharedPref.edit().putInt("currentSubVersion", value).apply()

    var fontSize: Float
        get() = sharedPref.getFloat("fontSize", 16f)
        set(value) = sharedPref.edit().putFloat("fontSize", value).apply()

    var paragraphSpacing: Float
        get() = sharedPref.getFloat("paragraphSpacing", 16f)
        set(value) = sharedPref.edit().putFloat("paragraphSpacing", value).apply()

    var fontFamily: String?
        get() = sharedPref.getString("fontFamily", "Default")
        set(value) = sharedPref.edit().putString("fontFamily", value).apply()

    var themeMode: Int
        get() = sharedPref.getInt("themeMode", 0)
        set(value) = sharedPref.edit().putInt("themeMode", value).apply()

    var saveColor: Int
        get() = sharedPref.getInt("saveColor", 0)
        set(value) = sharedPref.edit().putInt("saveColor", value).apply()
}
