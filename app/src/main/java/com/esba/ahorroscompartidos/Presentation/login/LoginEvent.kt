package com.esba.ahorroscompartidos.Presentation.login


sealed class LoginEvent {
    object LoginSuccess : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}