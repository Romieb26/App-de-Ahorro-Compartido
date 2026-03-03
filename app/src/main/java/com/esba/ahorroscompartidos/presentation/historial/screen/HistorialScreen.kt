package com.esba.ahorroscompartidos.presentation.historial.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esba.ahorroscompartidos.presentation.cuentaahorro.viewmodel.CuentaAhorroViewModel
import com.esba.ahorroscompartidos.presentation.historial.data.Transaccion
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: CuentaAhorroViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Movimientos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.historical.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay movimientos registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.historical) { transaccion ->
                    HistorialItem(transaccion = transaccion)
                }
            }
        }
    }
}

@Composable
fun HistorialItem(transaccion: Transaccion) {
    val icono: ImageVector
    val colorMonto: Color
    val texto: String

    when (transaccion.tipo) {
        "transfer", "TRANSFER_TO_SHARED" -> {
            icono = Icons.Default.ArrowUpward
            colorMonto = Color(0xFF2E7D32)
            texto = "Transferencia a cuenta compartida"
        }
        "withdraw", "WITHDRAW_SHARED" -> {
            icono = Icons.Default.ArrowDownward
            colorMonto = Color(0xFFC62828)
            texto = "Retiro de cuenta compartida"
        }
        "deposit", "DEPOSIT_PERSONAL" -> {
            icono = Icons.Default.ArrowDownward
            colorMonto = Color(0xFF2E7D32)
            texto = "Depósito a cuenta personal"
        }
        "withdraw_personal", "WITHDRAW_PERSONAL" -> {
            icono = Icons.Default.ArrowUpward
            colorMonto = Color(0xFFC62828)
            texto = "Retiro de cuenta personal"
        }
        else -> {
            icono = Icons.Default.SwapHoriz
            colorMonto = Color(0xFF9C27B0)
            texto = "Movimiento"
        }
    }

    val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "MX"))

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorMonto,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = texto,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = SimpleDateFormat("dd/MM HH:mm", Locale("es", "MX"))
                        .format(transaccion.fecha ?: Date()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatoMoneda.format(transaccion.monto),
                color = colorMonto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
