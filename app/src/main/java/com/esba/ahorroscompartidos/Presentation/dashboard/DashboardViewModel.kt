package com.esba.ahorroscompartidos.Presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esba.ahorroscompartidos.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getPersonalBalance: GetPersonalBalanceUseCase,
    private val getSharedBalance: GetSharedBalanceUseCase,
    private val observeTransactions: ObserveTransactionsUseCase,
    private val transferToShared: TransferToSharedUseCase,
    private val withdrawFromShared: WithdrawFromSharedUseCase,
    private val depositPersonal: DepositPersonalUseCase,
    private val withdrawPersonal: WithdrawPersonalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankUiState())
    val uiState: StateFlow<BankUiState> = _uiState

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getPersonalBalance(),
                getSharedBalance(),
                observeTransactions()
            ) { personal, shared, transactions ->
                BankUiState(personal, shared, transactions)
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun transfer(amount: Double) = launchSafe {
        transferToShared(amount)
    }

    fun withdrawShared(amount: Double) = launchSafe {
        withdrawFromShared(amount)
    }

    fun deposit(amount: Double) = launchSafe {
        depositPersonal(amount)
    }

    fun withdrawPersonal(amount: Double) = launchSafe {
    }

    private fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
                _events.emit(UiEvent.TransactionSuccess)
            } catch (e: Exception) {
                _events.emit(
                    UiEvent.ShowError(e.message ?: "Error")
                )
            }
        }
    }
}