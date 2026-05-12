package com.example.directoryapplication.presentation.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val employee: Employee? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadEmployee(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState(isLoading = true)
            repository.getEmployeeById(id)
                .onSuccess { employee ->
                    _uiState.value = DetailUiState(employee = employee)
                }
                .onFailure { error ->
                    _uiState.value = DetailUiState(error = "Ошибка: ${error.message}")
                }
        }
    }
}