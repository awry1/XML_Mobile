package com.seriouscompany.xmlmobile

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var file: File? = null
    var wasFileLoaded = false
    var isFabMenuOpen = false
    var isDarkModeEnabled = false
    var darkModeIcon = R.drawable.ic_light_mode

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    fun loadThemePreference(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkModeEnabled = prefs.getBoolean(KEY_DARK_MODE, false)
        updateDarkModeIcon()
    }

    fun saveThemePreference(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_DARK_MODE, isDarkModeEnabled) }
        updateDarkModeIcon()
    }

    private fun updateDarkModeIcon() {
        darkModeIcon = if (isDarkModeEnabled) {
            R.drawable.ic_light_mode
        } else {
            R.drawable.ic_dark_mode
        }
    }
}