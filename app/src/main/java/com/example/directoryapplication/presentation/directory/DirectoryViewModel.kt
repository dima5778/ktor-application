package com.example.directoryapplication.presentation.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.usecase.DeleteEmployeeUseCase
import com.example.directoryapplication.domain.usecase.GetEmployeesUseCase
import com.example.directoryapplication.domain.usecase.SearchEmployeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DirectoryUiState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isDeleted: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val searchEmployeesUseCase: SearchEmployeesUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase   // ← Добавили
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectoryUiState())
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        loadEmployees()

        viewModelScope.launch {
            searchQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isEmpty()) {
                        loadEmployees()
                    } else {
                        searchEmployees(query)
                    }
                }
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getEmployeesUseCase()
                .onSuccess { employees ->
                    _uiState.update { it.copy(employees = employees, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка загрузки: ${error.message}"
                        )
                    }
                }
        }
    }

    private fun searchEmployees(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            searchEmployeesUseCase(query)
                .onSuccess { employees ->
                    _uiState.update { it.copy(employees = employees, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка поиска: ${error.message}"
                        )
                    }
                }
        }
    }

    // Исправленная функция удаления
    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            deleteEmployeeUseCase(id)
                .onSuccess {
                    loadEmployees() // перезагружаем список
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка удаления: ${error.message}"
                        )
                    }
                }
        }
    }
    fun refresh() {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery.isEmpty()) {
            loadEmployees()
        } else {
            searchEmployees(currentQuery)
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQuery.value = query
    }
}