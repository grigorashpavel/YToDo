package com.pasha.ytodo.di

import android.content.Context
import com.pasha.android_core.di.ApplicationScope
import com.pasha.data.sources.local.LocalTodos
import com.pasha.domain.DataSource
import com.pasha.domain.LocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
interface DatabaseModule {
    companion object {
        @Provides
        @ApplicationScope
        fun provideLocalTodos(context: Context): LocalTodos = LocalTodos(context)
    }

    @Binds
    @ApplicationScope
    @com.pasha.android_core.di.LocalDataSource
    fun provideLocalDataSourceAsDataSource(impl: LocalTodos): DataSource

    @Binds
    @ApplicationScope
    fun provideLocalDataSourceAsLocalDataSource(impl: LocalTodos): LocalDataSource
}