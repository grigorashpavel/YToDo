package com.pasha.preferences

import com.pasha.android_core.di.Dependencies
import com.pasha.android_core.preferences.PreferencesManager

interface SettingsDeps: Dependencies {
    val preferencesManager: PreferencesManager
}