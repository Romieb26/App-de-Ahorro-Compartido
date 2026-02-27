//CheckSessionUseCase.kt
package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.domain.repository.AuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean =
        repository.isUserLoggedIn()
}