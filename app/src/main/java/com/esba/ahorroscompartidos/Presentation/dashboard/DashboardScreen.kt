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
    onOpenHistory: () -> Unit = {}
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Dashboard funcionando ✅")
        }
    }
}