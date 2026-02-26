package com.esba.ahorroscompartidos.Presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esba.ahorroscompartidos.Presentation.transactions.component.TransactionItem
@Composable
fun DashboardScreen(
    onOpenHistory: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowError ->
                    snackbarHostState.showSnackbar(event.message)
                UiEvent.TransactionSuccess ->
                    snackbarHostState.showSnackbar("Success")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text("Personal: $${state.personalBalance}")
            Text("Shared: $${state.sharedBalance}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.transfer(100.0) }) {
                Text("Transfer 100")
            }

            Button(onClick = { viewModel.withdrawShared(50.0) }) {
                Text("Withdraw Shared 50")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(state.transactions.take(3)) {
                    TransactionItem(it)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onOpenHistory) {
                Text("View Full History")
            }
        }
    }
}