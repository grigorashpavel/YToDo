package com.pasha.android_core.preferences

import android.content.Context
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate


class PreferencesManager(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val themeCode get() = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_YES)

    fun configureTheme(themeType: ThemeType) {
        val code = when(themeType) {
            ThemeType.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeType.Night -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeType.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        setThemeType(code)
        saveThemeType(code)
    }

    fun applyTheme() {
        setThemeType(themeCode)
    }

    val themeType get() = when(themeCode) {
        AppCompatDelegate.MODE_NIGHT_YES -> ThemeType.Night
        AppCompatDelegate.MODE_NIGHT_NO -> ThemeType.Light
        else -> ThemeType.System
    }

    private fun saveThemeType(code: Int) {
        preferences.edit().putInt(THEME_KEY, code).apply()
    }

    private fun setThemeType(code: Int) {
        AppCompatDelegate.setDefaultNightMode(code)
    }

    companion object {
        private const val THEME_KEY = "TodoTheme"

        enum class ThemeType {
            Light, Night, System
        }
    }
}