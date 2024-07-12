package com.pasha.ytodo.di

import com.pasha.android_core.di.ApplicationScope
import com.pasha.android_core.di.RemoteDataSource
import com.pasha.android_core.identification.DeviceIdentificationManager
import com.pasha.data.identification.DeviceIdentificationManagerImpl
import com.pasha.data.repositories.IdentificationRepositoryImpl
import com.pasha.data.sources.remote.RetrofitService
import com.pasha.domain.DataSource
import com.pasha.domain.repositories.IdentificationRepository
import com.pasha.network.NetworkClient
import com.pasha.network.TodoApi
import com.pasha.network.Token
import com.pasha.network.TokenManager
import com.pasha.network.TokenType
import com.pasha.ytodo.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Inject


@Module
interface NetworkModule {

    @Binds
    fun bindTokenManager(impl: TokenManagerImpl): TokenManager

    @Binds
    fun bindDeviceIdentificationManager(impl: DeviceIdentificationManagerImpl): DeviceIdentificationManager

    @Binds
    fun bindIdentificationRepository(impl: IdentificationRepositoryImpl): IdentificationRepository

    companion object {
        @Provides
        @ApplicationScope
        fun provideNetworkClient(tokenManager: TokenManager): NetworkClient =
            NetworkClient(tokenManager)

        @Provides
        @ApplicationScope
        fun provideTodoApi(networkClient: NetworkClient): TodoApi =
            networkClient.createRetrofitService(TodoApi::class.java)
    }

    @Binds
    @ApplicationScope
    @RemoteDataSource
    fun provideRemoteDataSource(impl: RetrofitService): DataSource
}

class TokenManagerImpl @Inject constructor() : TokenManager {
    override fun getToken(): Token {
        return Token(BuildConfig.TOKEN_BEARER, TokenType.Bearer)
    }
}