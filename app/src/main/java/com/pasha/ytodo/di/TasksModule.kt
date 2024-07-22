package com.pasha.ytodo.di

import com.pasha.all_tasks.api.TasksDeps
import com.pasha.android_core.di.Dependencies
import com.pasha.android_core.di.DependenciesKey
import com.pasha.android_core.di.FragmentScope
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
interface TasksModule {
    @Binds
    @IntoMap
    @DependenciesKey(TasksDeps::class)
    fun bindDependencies(component: ApplicationComponent): Dependencies
}