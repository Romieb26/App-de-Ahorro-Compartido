package com.esba.ahorroscompartidos.presentation.transactions

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.esba.ahorroscompartidos.Presentation.transactions.component.TransactionItem

/*
@Composable
fun TransactionHistoryScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn {
        items(state.transactions) {
            TransactionItem(it)
        }
    }
}*/