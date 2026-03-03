//LoginViewModel.kt
package com.esba.ahorroscompartidos.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esba.ahorroscompartidos.presentation.login.data.LoginEvent
import com.esba.ahorroscompartidos.presentation.login.data.LoginUiState
import com.esba.ahorroscompartidos.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
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

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _events.emit(LoginEvent.ShowError("Campos vacíos"))
            }
            return
        }

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            val result = loginUseCase(email, password)

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                _events.emit(LoginEvent.LoginSuccess)
            }.onFailure {
                _events.emit(
                    LoginEvent.ShowError(
                        it.message ?: "Error desconocido"
                    )
                )
            }
        }
    }
}