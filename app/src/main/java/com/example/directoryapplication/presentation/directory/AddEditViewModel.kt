package com.example.directoryapplication.presentation.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditUiState(
    val name: String = "",
    val position: String = "",
    val phone: String = "",
    val email: String = "",
    val department: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    fun loadEmployee(id: Int) {
        viewModelScope.launch {
            repository.getEmployeeById(id).onSuccess { emp ->
                _uiState.update {
                    it.copy(
                        name = emp.name,
                        position = emp.position,
                        phone = emp.phone,
                        email = emp.email,
                        department = emp.department
                    )
                }
            }
        }
    }

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v) }
    fun onPositionChange(v: String) = _uiState.update { it.copy(position = v) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(phone = v) }
    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v) }
    fun onDepartmentChange(v: String) = _uiState.update { it.copy(department = v) }

    fun save(id: Int?) {
        val state = _uiState.value
        if (state.name.isBlank() || state.position.isBlank()) {
            _uiState.update { it.copy(error = "Имя и должность обязательны") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val employee = Employee(
                id = id ?: 0,
                name = state.name,
                position = state.position,
                phone = state.phone,
                email = state.email,
                department = state.department
            )
            val result = if (id == null || id == -1) {
                repository.createEmployee(employee)
            } else {
                repository.updateEmployee(id, employee)
            }
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}