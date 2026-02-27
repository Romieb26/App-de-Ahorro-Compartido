//ActionButtons.kt
package com.esba.ahorroscompartidos.Presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esba.ahorroscompartidos.domain.model.*

@Composable
fun TransactionItem(transaction: Transaction) {

    val color = when (transaction.status) {
        TransactionStatus.CONFIRMED -> Color.Green
        TransactionStatus.PENDING -> Color.Yellow
        TransactionStatus.FAILED -> Color.Red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(transaction.type.name)
                Text("$${transaction.amount}")
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
        }
    }
}