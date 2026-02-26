package com.esba.ahorroscompartidos.Presentation.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lint.kotlin.metadata.Visibility

// Paleta de colores morados
object PurpleColors {
    val Purple80 = Color(0xFFD0BCFF)
    val Purple60 = Color(0xFFB69DFF)
    val Purple40 = Color(0xFF7A5AF0)
    val Purple30 = Color(0xFF5D3FD9)
    val Purple20 = Color(0xFF4527A0)
    val Purple10 = Color(0xFF2A1B5C)
    val PurpleGradient = Brush.verticalGradient(
        colors = listOf(Purple40, Purple20)
    )
    val LightPurple = Color(0xFFE6D9FF)
    val DarkPurple = Color(0xFF1A0B2E)
    val SoftPurple = Color(0xFF9B7BFF)
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Estado para mostrar/ocultar contraseña
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel){
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> {
                    Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_LONG).show()
                    onLoginSuccess()
                }
                is LoginEvent.ShowError ->
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

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
        // Fondo con elementos decorativos
        DecorativeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo o ícono
            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(30),
                colors = CardDefaults.cardColors(
                    containerColor = PurpleColors.Purple40.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💰",
                        fontSize = 48.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "Ahorros Compartidos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Inicia sesión para continuar",
                fontSize = 16.sp,
                color = PurpleColors.Purple80,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Card principal del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Campo Email
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = PurpleColors.Purple40
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = PurpleColors.LightPurple.copy(alpha = 0.3f),
                            unfocusedContainerColor = PurpleColors.LightPurple.copy(alpha = 0.1f),
                            focusedIndicatorColor = PurpleColors.Purple40,
                            unfocusedIndicatorColor = PurpleColors.Purple80,
                            focusedLabelColor = PurpleColors.Purple40,
                            cursorColor = PurpleColors.Purple40,
                            focusedTextColor = PurpleColors.Purple20
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Password
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PurpleColors.Purple40
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Info  // Temporal
                                    else
                                        Icons.Default.Warning,  // Temporal
                                    contentDescription = "Toggle password visibility",
                                    tint = PurpleColors.Purple40
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = PurpleColors.LightPurple.copy(alpha = 0.3f),
                            unfocusedContainerColor = PurpleColors.LightPurple.copy(alpha = 0.1f),
                            focusedIndicatorColor = PurpleColors.Purple40,
                            unfocusedIndicatorColor = PurpleColors.Purple80,
                            focusedLabelColor = PurpleColors.Purple40,
                            cursorColor = PurpleColors.Purple40,
                            focusedTextColor = PurpleColors.Purple20
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            }
                        ),
                        singleLine = true
                    )


                    // Botón de login
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.login()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleColors.Purple40,
                            disabledContainerColor = PurpleColors.Purple80,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "INICIAR SESIÓN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun DecorativeBackground() {
    // Elementos decorativos para el fondo
    Box(modifier = Modifier.fillMaxSize()) {
        // Círculo grande
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .clip(RoundedCornerShape(150.dp))
                .background(PurpleColors.Purple40.copy(alpha = 0.2f))
        )

        // Círculo mediano
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 250.dp, y = 100.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(PurpleColors.Purple30.copy(alpha = 0.15f))
        )

        // Círculo pequeño
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 50.dp, y = 500.dp)
                .clip(RoundedCornerShape(75.dp))
                .background(PurpleColors.Purple60.copy(alpha = 0.1f))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = PurpleColors.Purple40,
            secondary = PurpleColors.Purple60,
            background = PurpleColors.DarkPurple
        )
    ) {
        LoginScreen(
            onLoginSuccess = {}
        )
    }
}
