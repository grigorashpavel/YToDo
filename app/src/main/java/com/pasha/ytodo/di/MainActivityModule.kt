package com.pasha.ytodo.di

import androidx.navigation.navOptions
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
    private val navOptions = navOptions {
        anim {
            enter = com.pasha.core_ui.R.anim.enter_slide_to_left
            exit = com.pasha.core_ui.R.anim.exit_slide_to_left
            popEnter = com.pasha.core_ui.R.anim.enter_slide_to_right
            popExit = com.pasha.core_ui.R.anim.exit_slide_to_right
        }
    }
    override val toTaskEdit: NavCommand =
        NavCommand(
            action = R.id.action_tasksFragment_to_taskEditComposeFragment,
            navOptions = navOptions
        )
    override val toSettings: NavCommand =
        NavCommand(
            action = R.id.action_tasksFragment_to_settingsFragment,
            navOptions = navOptions
        )
    override val toAboutApp: NavCommand =
        NavCommand(
            action = R.id.action_tasksFragment_to_aboutAppFragment,
            navOptions = navOptions
        )
}