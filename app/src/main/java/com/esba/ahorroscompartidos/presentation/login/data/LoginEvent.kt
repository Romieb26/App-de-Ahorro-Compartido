//LoginEvent.kt
package com.esba.ahorroscompartidos.presentation.login.data

sealed class LoginEvent {
    object LoginSuccess : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}