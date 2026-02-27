//DepositPersonalUseCase.kt
package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.domain.repository.BankRepository
import java.time.temporal.TemporalAmount

import javax.inject.Inject

class DepositPersonalUseCase @Inject constructor(
    private val repository: BankRepository
){
    suspend operator fun invoke(amount: Double) {
    repository.depositPersonal(amount)
    }
    }