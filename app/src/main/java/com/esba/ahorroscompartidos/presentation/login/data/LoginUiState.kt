//LoginUiState.kt3
package com.esba.ahorroscompartidos.presentation.login.data

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLogging: Boolean = false,
    val isLoading: Boolean = false
)