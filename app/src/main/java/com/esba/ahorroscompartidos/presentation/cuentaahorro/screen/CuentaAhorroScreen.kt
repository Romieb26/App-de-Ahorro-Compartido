//CuentaAhorroScreen.kt
package com.esba.ahorroscompartidos.presentation.cuentaahorro.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esba.ahorroscompartidos.presentation.cuentaahorro.viewmodel.CuentaAhorroViewModel
import com.esba.ahorroscompartidos.presentation.historial.data.Transaccion
import com.esba.ahorroscompartidos.presentation.login.screen.PurpleColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaAhorroScreen(
    onBackToLogin: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    viewModel: CuentaAhorroViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Cuentas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            onBackToLogin()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { onNavigateToHistorial() }
                    ) {
                        Text(
                            "Ver Todo",
                            color = PurpleColors.Purple80,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleColors.Purple30,
                    scrolledContainerColor = PurpleColors.Purple40,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = PurpleColors.Purple80
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PurpleColors.DarkPurple,
                            PurpleColors.Purple10,
                            PurpleColors.DarkPurple
                        )
                    )
                )
        ) {
            DecorativeBackground()

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = PurpleColors.Purple40,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(50.dp)
                    )
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Saldo Total Card
                item {
                    SaldoTotalCard(
                        total = state.personalBalance + state.sharedBalance,
                        personal = state.personalBalance,
                        shared = state.sharedBalance
                    )
                }

                // Cuenta Personal
                item {
                    CuentaCard(
                        title = "CUENTA PERSONAL",
                        balance = state.personalBalance,
                        icon = Icons.Default.Person,
                        backgroundColor = PurpleColors.Purple40,
                        actions = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ActionButton(
                                    text = "Depositar \$100",
                                    icon = Icons.Default.Add,
                                    onClick = { viewModel.depositPersonal(100.0) },
                                    color = PurpleColors.SoftPurple
                                )
                                ActionButton(
                                    text = "Retirar \$50",
                                    icon = Icons.Default.Remove,
                                    onClick = { viewModel.withdrawPersonal(50.0) },
                                    color = PurpleColors.Purple60
                                )
                            }
                        }
                    )
                }

                // Cuenta Compartida
                item {
                    CuentaCard(
                        title = "CUENTA COMPARTIDA",
                        balance = state.sharedBalance,
                        icon = Icons.Default.People,
                        backgroundColor = PurpleColors.Purple60,
                        actions = {
                            ActionButton(
                                text = "Transferir \$100",
                                icon = Icons.Default.SwapHoriz,
                                onClick = { viewModel.transferToShared(100.0) },
                                color = PurpleColors.Purple30,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }

                // Últimos 5 movimientos
                if (state.historical.isNotEmpty()) {
                    item {
                        UltimosMovimientos(
                            movimientos = state.historical.take(5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SaldoTotalCard(
    total: Double,
    personal: Double,
    shared: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleColors.Purple40.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SALDO TOTAL",
                        fontSize = 12.sp,
                        color = PurpleColors.Purple80,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "$${String.format("%,.2f", total)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(PurpleColors.Purple60)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Personal: $${String.format("%,.2f", personal)}",
                    color = PurpleColors.Purple80,
                    fontSize = 12.sp
                )
                Text(
                    text = "Compartida: $${String.format("%,.2f", shared)}",
                    color = PurpleColors.Purple80,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CuentaCard(
    title: String,
    balance: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    actions: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = backgroundColor
                    )
                }
                Text(
                    text = "💵",
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Saldo disponible",
                fontSize = 12.sp,
                color = PurpleColors.Purple40.copy(alpha = 0.7f)
            )
            Text(
                text = "$${String.format("%,.2f", balance)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleColors.Purple30
            )

            Spacer(modifier = Modifier.height(16.dp))

            actions()
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UltimosMovimientos(
    movimientos: List<Transaccion>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ÚLTIMOS MOVIMIENTOS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleColors.Purple80,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            movimientos.forEachIndexed { index, movimiento ->
                MovimientoItemReal(
                    transaccion = movimiento
                )
                if (index < movimientos.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = PurpleColors.Purple80.copy(alpha = 0.3f)
                    )
                }
            }

            if (movimientos.isEmpty()) {
                Text(
                    text = "No hay movimientos recientes",
                    color = PurpleColors.Purple80,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun MovimientoItemReal(
    transaccion: Transaccion
) {
    val (icono, color, texto) = when (transaccion.tipo) {
        "transfer", "TRANSFER_TO_SHARED" -> Triple(
            Icons.Default.ArrowUpward,
            PurpleColors.SoftPurple,
            "Transferencia a compartida"
        )
        "withdraw", "WITHDRAW_SHARED" -> Triple(
            Icons.Default.ArrowDownward,
            PurpleColors.Purple60,
            "Retiro de compartida"
        )
        "deposit", "DEPOSIT_PERSONAL" -> Triple(
            Icons.Default.ArrowDownward,
            PurpleColors.SoftPurple,
            "Depósito personal"
        )
        "withdraw_personal", "WITHDRAW_PERSONAL" -> Triple(
            Icons.Default.ArrowUpward,
            PurpleColors.Purple60,
            "Retiro personal"
        )
        else -> Triple(
            Icons.Default.SwapHoriz,
            PurpleColors.Purple40,
            "Movimiento"
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = texto,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = SimpleDateFormat("dd/MM HH:mm", Locale("es", "MX"))
                        .format(transaccion.fecha ?: Date()),
                    color = PurpleColors.Purple80,
                    fontSize = 10.sp
                )
            }
        }
        Text(
            text = "$${String.format("%,.2f", transaccion.monto)}",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

@Composable
fun DecorativeBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .clip(RoundedCornerShape(150.dp))
                .background(PurpleColors.Purple40.copy(alpha = 0.2f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 250.dp, y = 300.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(PurpleColors.Purple30.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = (-50).dp, y = 600.dp)
                .clip(RoundedCornerShape(75.dp))
                .background(PurpleColors.Purple60.copy(alpha = 0.1f))
        )
    }
}