//transactionItem.kt
package com.esba.ahorroscompartidos.Presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esba.ahorroscompartidos.domain.model.Transaction
import com.esba.ahorroscompartidos.domain.model.TransactionStatus

@Composable
fun transactionItem(
    transaction: Transaction
) {

    val statusColor = when (transaction.status) {
        TransactionStatus.CONFIRMED -> Color(0xFF4CAF50) // Verde
        TransactionStatus.PENDING -> Color(0xFFFFC107)   // Amarillo
        TransactionStatus.FAILED -> Color(0xFFF44336)    // Rojo
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = transaction.type.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${"%.2f".format(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(statusColor, CircleShape)
            )
        }
    }
}