package com.pasha.ytodo.di

import android.content.Context
import com.pasha.all_tasks.api.TasksNavDeps
import com.pasha.android_core.di.ActivityScope
import com.pasha.ytodo.MainActivity
import dagger.BindsInstance
import dagger.Component


@ActivityScope
@Component(modules = [MainActivityModule::class])
interface MainActivityComponent: TasksNavDeps {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): MainActivityComponent
    }

    fun inject(activity: MainActivity)
}