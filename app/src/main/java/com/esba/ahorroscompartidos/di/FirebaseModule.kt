//FirebaseModule.kt
package com.esba.ahorroscompartidos.di

import com.esba.ahorroscompartidos.data.remote.FirebaseAuthDataSource
import com.esba.ahorroscompartidos.data.remote.FirebaseRealtimeService
import com.esba.ahorroscompartidos.data.repository.AuthRepositoryImpl
import com.esba.ahorroscompartidos.data.repository.BankRepositoryImpl
import com.esba.ahorroscompartidos.domain.repository.AuthRepository
import com.esba.ahorroscompartidos.domain.repository.BankRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val database = Firebase.database
        database.setPersistenceEnabled(true)
        return database
    }

    @Provides
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource = FirebaseAuthDataSource(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirebaseRealtimeService(
        database: FirebaseDatabase
    ): FirebaseRealtimeService = FirebaseRealtimeService(database)

    @Provides
    fun provideAuthRepository(
        dataSource: FirebaseAuthDataSource,
        realtimeService: FirebaseRealtimeService
    ): AuthRepository = AuthRepositoryImpl(dataSource, realtimeService)

    @Provides
    @Singleton
    fun provideBankRepository(
        authDataSource: FirebaseAuthDataSource,
        realtimeService: FirebaseRealtimeService
    ): BankRepository = BankRepositoryImpl(authDataSource, realtimeService)
}