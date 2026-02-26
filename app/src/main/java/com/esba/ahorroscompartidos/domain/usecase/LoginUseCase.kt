package com.esba.ahorroscompartidos.domain.usecase

import com.esba.ahorroscompartidos.data.remote.FirebaseAuthService
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authService: FirebaseAuthService
) {
    suspend operator fun invoke(email: String, password: String) {
        authService.login(email, password)
    }
}