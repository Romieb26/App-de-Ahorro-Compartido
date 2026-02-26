package com.esba.ahorroscompartidos.data.local.dao


import androidx.room.*
import com.esba.ahorroscompartidos.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDao {

    @Query("SELECT * FROM user_account LIMIT 1")
    fun observeUser(): Flow<UserAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("SELECT * FROM shared_account LIMIT 1")
    fun observeShared(): Flow<SharedAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShared(shared: SharedAccountEntity)

    @Update
    suspend fun updateShared(shared: SharedAccountEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
}