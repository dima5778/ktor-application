package com.example.directoryapplication.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, confirmPassword: String) {
        when {
            email.isBlank() || password.isBlank() -> {
                _uiState.value = RegisterUiState(error = "Заполните все поля")
                return
            }
            password != confirmPassword -> {
                _uiState.value = RegisterUiState(error = "Пароли не совпадают")
                return
            }
            password.length < 6 -> {
                _uiState.value = RegisterUiState(error = "Пароль должен быть не менее 6 символов")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _uiState.value = RegisterUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = RegisterUiState(error = "Ошибка: ${e.message}")
            }
        }
    }
}