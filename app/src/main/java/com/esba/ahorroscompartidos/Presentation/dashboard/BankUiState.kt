package com.esba.ahorroscompartidos.Presentation.dashboard

import com.esba.ahorroscompartidos.domain.model.Transaction

data class BankUiState(
    val personalBalance: Double = 0.0,
    val sharedBalance: Double = 0.0,
    val transactions: List<Transaction> = emptyList()
)