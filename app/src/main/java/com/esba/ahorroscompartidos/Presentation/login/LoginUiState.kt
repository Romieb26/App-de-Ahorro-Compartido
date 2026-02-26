package com.esba.ahorroscompartidos.Presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoging: Boolean = false,
    val isLoading: Boolean
)