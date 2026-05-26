package com.example.directoryapplication.presentation.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directoryapplication.data.local.SearchHistoryDataStore
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.usecase.DeleteEmployeeUseCase
import com.example.directoryapplication.domain.usecase.GetEmployeesUseCase
import com.example.directoryapplication.domain.usecase.SearchEmployeesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class DirectoryViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val searchEmployeesUseCase: SearchEmployeesUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val searchHistoryDataStore: SearchHistoryDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectoryUiState())
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    val searchHistory: Flow<List<String>> = searchHistoryDataStore.searchHistory

    init {
        loadEmployees()
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
                        it.copy(isLoading = false, error = "Ошибка загрузки: ${error.message}")
                    }
                }
        }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) {
            loadEmployees()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            searchEmployeesUseCase(query)
                .onSuccess { employees ->
                    _uiState.update { it.copy(employees = employees, isLoading = false) }
                    saveSearchQuery(query)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "Ошибка поиска: ${error.message}")
                    }
                }
        }
    }

    private suspend fun saveSearchQuery(query: String) {
        searchHistoryDataStore.addSearchQuery(query)
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            deleteEmployeeUseCase(id)
                .onSuccess { loadEmployees() }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "Ошибка удаления: ${error.message}")
                    }
                }
        }
    }

    fun refresh() {
        performSearch(_uiState.value.searchQuery)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryDataStore.clearHistory()
        }
    }
}