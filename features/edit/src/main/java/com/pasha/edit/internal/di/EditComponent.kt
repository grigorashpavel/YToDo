package com.pasha.edit.internal.di

import com.pasha.android_core.di.FragmentScope
import com.pasha.edit.api.EditDeps
import com.pasha.edit.api.TaskEditComposeFragment
import com.pasha.edit.api.TaskEditFragment
import dagger.Component


@FragmentScope
@Component(dependencies = [EditDeps::class])
interface EditComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: EditDeps): EditComponent
    }

    fun inject(fragment: TaskEditComposeFragment)
    fun inject(fragment: TaskEditFragment)
}