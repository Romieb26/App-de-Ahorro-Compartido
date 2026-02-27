//TransferToSharedUseCase.kt
package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.domain.repository.BankRepository
import javax.inject.Inject

class TransferToSharedUseCase @Inject constructor(
    private val repository: BankRepository
) {
    suspend operator fun invoke(amount: Double) {
        repository.transferToShared(amount)
    }
}