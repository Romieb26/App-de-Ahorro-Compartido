package com.esba.ahorroscompartidos.Presentation.dashboard

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {

    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        BalanceCard(
            title = "Saldo Personal",
            amount = state.personalBalance
        )

        BalanceCard(
            title = "Saldo Compartido",
            amount = state.sharedBalance
        )

        Button(onClick = { viewModel.transfer(100.0) }) {
            Text("Transferir $100")
        }
    }
}