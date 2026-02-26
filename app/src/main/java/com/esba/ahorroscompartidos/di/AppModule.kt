package com.esba.ahorroscompartidos.di

import android.content.Context
import androidx.room.Room
import com.esba.ahorroscompartidos.data.local.DualSaveDatabase
import com.esba.ahorroscompartidos.data.local.dao.BankDao
import com.esba.ahorroscompartidos.data.remote.FirebaseAuthService
import com.esba.ahorroscompartidos.data.remote.FirebaseFirestoreService
import com.esba.ahorroscompartidos.data.repository.BankRepositoryImpl
import com.esba.ahorroscompartidos.domain.repository.BankRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DualSaveDatabase =
        Room.databaseBuilder(
            context,
            DualSaveDatabase::class.java,
            "dual_save_db"
        ).build()

    @Provides
    fun provideBankDao(db: DualSaveDatabase): BankDao =
        db.bankDao()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideRepository(
        dao: BankDao,
        authService: FirebaseAuthService,
        firestoreService: FirebaseFirestoreService
    ): BankRepository =
        BankRepositoryImpl(dao, authService, firestoreService)
}