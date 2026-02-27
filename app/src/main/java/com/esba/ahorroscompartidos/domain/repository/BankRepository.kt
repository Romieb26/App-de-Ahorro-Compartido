//BankRepository.kt
package com.esba.ahorroscompartidos.domain.repository

import com.esba.ahorroscompartidos.domain.model.SharedAccount
import com.esba.ahorroscompartidos.domain.model.Transaction
import com.esba.ahorroscompartidos.domain.model.TransactionStatus
import com.esba.ahorroscompartidos.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface BankRepository {
    fun observeUserAccount(): Flow<UserAccount?>
    fun observeSharedAccount(): Flow<SharedAccount?>
    fun observeTransactions(): Flow<List<Transaction>>

    suspend fun transferToShared(amount: Double)
    suspend fun withdrawFromShared(amount: Double)
    suspend fun depositPersonal(amount: Double)
    suspend fun withdrawPersonal(amount: Double)

    suspend fun getCurrentUserId(): String?
}