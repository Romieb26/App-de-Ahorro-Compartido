package com.esba.ahorroscompartidos.di

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bank_db"
        ).build()

    @Provides
    fun provideBalanceDao(db: AppDatabase) = db.balanceDao()

    @Provides
    fun provideTransactionDao(db: AppDatabase) = db.transactionDao()

    @Provides
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideAuth() = FirebaseAuth.getInstance()

    @Provides
    fun provideRepository(
        balanceDao: BalanceDao,
        transactionDao: TransactionDao,
        remote: FirebaseDataSource
    ): BankRepository =
        BankRepositoryImpl(balanceDao, transactionDao, remote)
}