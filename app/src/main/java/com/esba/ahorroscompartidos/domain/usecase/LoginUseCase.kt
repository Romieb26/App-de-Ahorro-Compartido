//LoginUseCase.kt
package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.login(email, password)
}