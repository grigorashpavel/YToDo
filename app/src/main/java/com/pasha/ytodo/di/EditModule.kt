package com.pasha.ytodo.di

import com.pasha.android_core.di.Dependencies
import com.pasha.android_core.di.DependenciesKey
import com.pasha.edit.api.EditDeps
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface EditModule {
    @Binds
    @IntoMap
    @DependenciesKey(EditDeps::class)
    fun bindDependencies(component: ApplicationComponent): Dependencies
}