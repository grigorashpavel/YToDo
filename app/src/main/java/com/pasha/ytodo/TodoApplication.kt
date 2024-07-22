package com.pasha.ytodo

import android.app.Application
import android.net.ConnectivityManager
import com.pasha.android_core.di.DepsMap
import com.pasha.android_core.di.HasDependencies
import com.pasha.android_core.network.ConnectionChecker
import com.pasha.android_core.preferences.PreferencesManager
import com.pasha.android_core.synchronize.SynchronizeWorker
import com.pasha.domain.repositories.SynchronizeRepository
import com.pasha.domain.repositories.SynchronizeRepositoryProvider
import com.pasha.domain.repositories.TodoItemRepositoryProvider
import com.pasha.domain.repositories.TodoItemsRepository
import com.pasha.ytodo.di.DaggerApplicationComponent
import javax.inject.Inject


class TodoApplication : Application(), HasDependencies, TodoItemRepositoryProvider,
    SynchronizeRepositoryProvider {
    @Inject
    override lateinit var depsMap: DepsMap

    @Inject
    override lateinit var todoItemsRepository: TodoItemsRepository

    @Inject
    override lateinit var synchronizeRepository: SynchronizeRepository

    @Inject
    lateinit var connectionChecker: ConnectionChecker

    @Inject
    lateinit var networkCallback: ConnectivityManager.NetworkCallback

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent.factory()
            .create(applicationContext)
            .inject(this)

        SynchronizeWorker.registerPeriodSynchronizeWork(applicationContext)
        connectionChecker.registerNetworkCallback(networkCallback)

        preferencesManager.applyTheme()
    }
}