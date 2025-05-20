package com.seriouscompany.xmlmobile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.core.content.edit

class MainViewModel : ViewModel() {
    var file: File? = null
    var wasFileLoaded: Boolean = false
    var isDarkModeEnabled: Boolean = false

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    fun loadThemePreference(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkModeEnabled = prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun saveThemePreference(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_DARK_MODE, isDarkModeEnabled) }
    }
}