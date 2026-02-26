package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPersonalBalanceUseCase @Inject constructor(
    private val repository: BankRepository
) {
    operator fun invoke(): Flow<Double> =
        repository.observeUserAccount().map { it.personalBalance }
}