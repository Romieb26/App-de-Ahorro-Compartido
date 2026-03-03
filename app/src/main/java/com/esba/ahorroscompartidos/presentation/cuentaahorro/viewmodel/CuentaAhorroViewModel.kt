//CuentaAhorroViewModel.kt
package com.esba.ahorroscompartidos.presentation.cuentaahorro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esba.ahorroscompartidos.presentation.cuentaahorro.data.CentaHorrorState
import com.esba.ahorroscompartidos.presentation.historial.data.Transaccion
import com.esba.ahorroscompartidos.domain.model.Transaction
import com.esba.ahorroscompartidos.domain.model.TransactionType
import com.esba.ahorroscompartidos.domain.repository.AuthRepository
import com.esba.ahorroscompartidos.domain.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CuentaAhorroViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bankRepository: BankRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CentaHorrorState())
    val uiState: StateFlow<CentaHorrorState> = _uiState

    init {
        Timber.d("🚀 CuentaAhorroViewModel INICIADO")
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            Timber.d("📡 Iniciando observación de datos")

            combine(
                bankRepository.observeUserAccount(),
                bankRepository.observeSharedAccount(),
                bankRepository.observeTransactions()
            ) { user, shared, transactions ->
                Timber.d("📊 Datos recibidos - User: $user, Shared: $shared, Transactions: ${transactions.size}")

                // Log detallado de las transacciones
                if (transactions.isNotEmpty()) {
                    transactions.take(3).forEachIndexed { index, t ->
                        Timber.d("   📝 Transacción $index: type=${t.type}, amount=${t.amount}, from=${t.fromUserId}")
                    }
                }

                CentaHorrorState(
                    personalBalance = user?.personalBalance ?: 0.0,
                    sharedBalance = shared?.balance ?: 0.0,
                    historical = transactions.map { it.toTransaccion() },
                    isLoading = false
                )
            }.collect { state ->
                Timber.d("✅ Estado actualizado: personalBalance=${state.personalBalance}, historial.size=${state.historical.size}")
                _uiState.value = state
            }
        }
    }

    fun depositPersonal(amount: Double) {
        viewModelScope.launch {
            try {
                Timber.d("💵 depositPersonal: $amount")
                _uiState.value = _uiState.value.copy(isLoading = true)
                bankRepository.depositPersonal(amount)
            } catch (e: Exception) {
                Timber.e(e, "❌ Error en depositPersonal")
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun withdrawPersonal(amount: Double) {
        viewModelScope.launch {
            try {
                Timber.d("💵 withdrawPersonal: $amount")
                _uiState.value = _uiState.value.copy(isLoading = true)
                bankRepository.withdrawPersonal(amount)
            } catch (e: Exception) {
                Timber.e(e, "❌ Error en withdrawPersonal")
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun transferToShared(amount: Double) {
        viewModelScope.launch {
            try {
                Timber.d("💵 transferToShared: $amount")
                _uiState.value = _uiState.value.copy(isLoading = true)
                bankRepository.transferToShared(amount)
            } catch (e: Exception) {
                Timber.e(e, "❌ Error en transferToShared")
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun logout() {
        Timber.d("👋 Logout")
        authRepository.logout()
    }

    private fun Transaction.toTransaccion(): Transaccion {
        return Transaccion(
            id = id,
            tipo = when (type) {
                TransactionType.TRANSFER_TO_SHARED -> "TRANSFER_TO_SHARED"
                TransactionType.WITHDRAW_SHARED -> "WITHDRAW_SHARED"
                TransactionType.DEPOSIT_PERSONAL -> "DEPOSIT_PERSONAL"
                TransactionType.WITHDRAW_PERSONAL -> "WITHDRAW_PERSONAL"
            },
            usuarioId = fromUserId,
            monto = amount,
            fecha = Date(timestamp)
        )
    }
}