package com.pasha.all_tasks.internal.di

import com.pasha.all_tasks.api.TasksDeps
import com.pasha.all_tasks.api.TasksFragment
import com.pasha.all_tasks.api.TasksNavDeps
import com.pasha.all_tasks.internal.TasksViewModel
import com.pasha.android_core.di.FragmentScope
import dagger.Component


@FragmentScope
@Component(dependencies = [TasksDeps::class, TasksNavDeps::class])
interface TasksComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: TasksDeps, navDeps: TasksNavDeps): TasksComponent
    }

    fun inject(fragment: TasksFragment)
}