package com.esba.ahorroscompartidos.Presentation.dashboard.components

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transferToSharedUseCase: TransferToSharedUseCase,
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    observeBalanceUseCase: GetBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankUiState())
    val uiState: StateFlow<BankUiState> = _uiState

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeBalanceUseCase().onEach { balance ->
            _uiState.update {
                it.copy(
                    personalBalance = balance?.personalBalance ?: 0.0,
                    sharedBalance = balance?.sharedBalance ?: 0.0
                )
            }
        }.launchIn(viewModelScope)

        observeTransactionsUseCase().onEach { transactions ->
            _uiState.update {
                it.copy(transactions = transactions)
            }
        }.launchIn(viewModelScope)
    }

    fun transfer(amount: Double) {
        viewModelScope.launch {
            try {
                transferToSharedUseCase(amount)
            } catch (e: Exception) {
                _events.emit(UiEvent.ShowError("Error en la transferencia"))
            }
        }
    }
}