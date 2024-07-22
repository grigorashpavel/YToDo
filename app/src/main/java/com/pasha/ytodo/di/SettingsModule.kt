package com.pasha.ytodo.di

import com.pasha.android_core.di.Dependencies
import com.pasha.android_core.di.DependenciesKey
import com.pasha.preferences.SettingsDeps
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface SettingsModule {
    @Binds
    @IntoMap
    @DependenciesKey(SettingsDeps::class)
    fun bindDependencies(component: ApplicationComponent): Dependencies
}