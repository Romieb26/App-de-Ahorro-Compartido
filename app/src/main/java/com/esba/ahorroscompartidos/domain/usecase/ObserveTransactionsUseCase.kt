//ObserveTransactionsUseCase.kt
package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.domain.model.Transaction
import com.esba.ahorroscompartidos.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val repository: BankRepository
) {
    operator fun invoke(): Flow<List<Transaction>> =
        repository.observeTransactions()
}