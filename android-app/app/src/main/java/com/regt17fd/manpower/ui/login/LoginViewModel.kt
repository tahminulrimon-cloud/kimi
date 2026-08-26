package com.regt17fd.manpower.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.data.repository.AuthRepository
import com.regt17fd.manpower.data.repository.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = value)
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Enter username and password")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            when (val result = authRepository.login(state.username, state.password)) {
                is LoginResult.Success -> {
                    sessionManager.signIn(result.user, state.rememberMe)
                    _uiState.value = LoginUiState()
                    onSuccess()
                }
                LoginResult.InvalidCredentials -> {
                    _uiState.value = state.copy(isLoading = false, error = "Invalid username or password")
                }
            }
        }
    }
}
