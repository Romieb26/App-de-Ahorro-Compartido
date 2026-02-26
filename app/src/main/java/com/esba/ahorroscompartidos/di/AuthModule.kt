package com.esba.ahorroscompartidos.di

import com.esba.ahorroscompartidos.data.remote.FirebaseAuthDataSource
import com.esba.ahorroscompartidos.data.repository.AuthRepositoryImpl
import com.esba.ahorroscompartidos.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource =
        FirebaseAuthDataSource(firebaseAuth)

    @Provides
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource
    ): AuthRepository =
        AuthRepositoryImpl(dataSource)
}