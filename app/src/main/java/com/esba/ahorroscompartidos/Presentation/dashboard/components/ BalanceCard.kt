package com.esba.ahorroscompartidos.Presentation.dashboard.components

@Composable
fun BalanceCard(title: String, amount: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = title)
            Text(text = "$$amount")
        }
    }
}