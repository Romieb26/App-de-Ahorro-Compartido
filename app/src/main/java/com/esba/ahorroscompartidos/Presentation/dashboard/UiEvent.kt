package com.esba.ahorroscompartidos.presentation.dashboard

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    object TransactionSuccess : UiEvent()
}