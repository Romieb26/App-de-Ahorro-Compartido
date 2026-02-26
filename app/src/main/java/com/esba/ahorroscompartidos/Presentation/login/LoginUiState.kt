package com.esba.ahorroscompartidos.Presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLogging: Boolean = false,
    val isLoading: Boolean = false
)