package com.pasha.ytodo.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import com.pasha.android_core.di.ApplicationScope
import com.pasha.android_core.network.ConnectionChecker
import com.pasha.android_core.preferences.PreferencesManager
import com.pasha.android_core.synchronize.SynchronizeWorker
import com.pasha.data.repositories.SynchronizeRepositoryImpl
import com.pasha.data.repositories.TodoItemsRepositoryImpl
import com.pasha.domain.repositories.SynchronizeRepository
import com.pasha.domain.repositories.TodoItemsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Inject


@Module(includes = [DatabaseModule::class, NetworkModule::class])
interface ApplicationModule {
    @Binds
    @ApplicationScope
    fun bindTodoRepository(impl: TodoItemsRepositoryImpl): TodoItemsRepository

    @Binds
    @ApplicationScope
    fun bindSynchronizeRepository(impl: SynchronizeRepositoryImpl): SynchronizeRepository

    companion object {
        @Provides
        fun provideConnectionChecker(context: Context): ConnectionChecker =
            ConnectionChecker(context)

        @Provides
        fun providePreferencesManager(context: Context): PreferencesManager =
            PreferencesManager(context)

        @Provides
        @ApplicationScope
        fun provideNetworkCallback(context: Context): ConnectivityManager.NetworkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    Log.e("Callback", "onAvailable()")
                    SynchronizeWorker.startSynchronizeIfNeed(context)
                }
            }
    }
}
