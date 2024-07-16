package com.pasha.preferences.di

import com.pasha.android_core.di.FragmentScope
import com.pasha.preferences.SettingsDeps
import com.pasha.preferences.SettingsFragment
import dagger.Component


@FragmentScope
@Component(dependencies = [SettingsDeps::class])
internal interface SettingsComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: SettingsDeps): SettingsComponent
    }

    fun inject(fragment: SettingsFragment)
}