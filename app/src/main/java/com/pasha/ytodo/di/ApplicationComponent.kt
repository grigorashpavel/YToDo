package com.pasha.ytodo.di

import android.app.Application
import android.content.Context
import com.pasha.all_tasks.api.TasksDeps
import com.pasha.android_core.di.ApplicationScope
import com.pasha.edit.api.EditDeps
import com.pasha.ytodo.TodoApplication
import dagger.BindsInstance
import dagger.Component


@ApplicationScope
@Component(modules = [ApplicationModule::class, TasksModule::class, EditModule::class])
interface ApplicationComponent: TasksDeps, EditDeps {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }

    fun inject(application: TodoApplication)
}

