package com.esba.ahorroscompartidos.Presentation.dashboard

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    object TransactionSuccess : UiEvent()
}