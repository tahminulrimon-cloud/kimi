package com.regt17fd.manpower.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regt17fd.manpower.auth.SessionManager
import com.regt17fd.manpower.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChangePasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val error: String? = null,
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, error = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null)
    }

    fun submit(onDone: () -> Unit) {
        val state = _uiState.value
        val user = sessionManager.currentUser.value ?: return
        if (state.newPassword.length < 6) {
            _uiState.value = state.copy(error = "Password must be at least 6 characters")
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.value = state.copy(error = "Passwords do not match")
            return
        }
        viewModelScope.launch {
            authRepository.changePassword(user, state.newPassword)
            sessionManager.updateUser(user.copy(mustChangePassword = false))
            onDone()
        }
    }
}
