package com.pasha.ytodo.di

import com.pasha.all_tasks.api.TasksNavCommandsProvider
import com.pasha.all_tasks.api.TasksNavDeps
import com.pasha.android_core.di.ActivityScope
import com.pasha.android_core.di.Dependencies
import com.pasha.android_core.di.DependenciesKey
import com.pasha.android_core.navigation.NavCommand
import com.pasha.ytodo.R
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject


@Module
interface MainActivityModule {
    @Binds
    @ActivityScope
    fun bindTasksNavCommandProvider(impl: TasksNavCommandsProviderImpl): TasksNavCommandsProvider

    @Binds
    @IntoMap
    @DependenciesKey(TasksNavDeps::class)
    fun bindDependencies(component: MainActivityComponent): Dependencies
}

class TasksNavCommandsProviderImpl @Inject constructor() : TasksNavCommandsProvider {
    override val toTaskEdit: NavCommand =
        NavCommand(action = R.id.action_tasksFragment_to_taskEditComposeFragment)
}