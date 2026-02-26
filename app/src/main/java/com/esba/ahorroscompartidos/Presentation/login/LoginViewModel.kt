package com.esba.ahorroscompartidos.Presentation.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esba.ahorroscompartidos.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun login() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                loginUseCase(
                    _uiState.value.email,
                    _uiState.value.password
                )

                _events.emit(LoginEvent.LoginSuccess)

            } catch (e: Exception) {
                _events.emit(
                    LoginEvent.ShowError(e.message ?: "Login error")
                )
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}