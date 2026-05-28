package com.example.directoryapplication.presentation.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directoryapplication.data.local.SearchHistoryDataStore
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
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val searchHistoryDataStore: SearchHistoryDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectoryUiState())
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    val searchHistory: Flow<List<String>> = searchHistoryDataStore.searchHistory

    private val searchQueryFlow = MutableStateFlow("")

    init {
        loadEmployees()
        viewModelScope.launch {
            searchQueryFlow
                .debounce(250)
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    private fun loadEmployees() {
        performSearch("")
    }

    fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = if (query.isBlank()) {
                getEmployeesUseCase()
            } else {
                searchEmployeesUseCase(query)
            }

            result.onSuccess { employees ->
                _uiState.update {
                    it.copy(employees = employees, isLoading = false)
                }
                if (query.isNotBlank()) {
                    saveSearchQuery(query)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = if (query.isBlank()) {
                            "Ошибка загрузки: ${exception.message}"
                        } else {
                            "Ошибка поиска: ${exception.message}"
                        }
                    )
                }
            }
        }
    }

    private suspend fun saveSearchQuery(query: String) {
        searchHistoryDataStore.addSearchQuery(query)
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
    }

    fun performManualSearch() {
        performSearch(_uiState.value.searchQuery)
    }

    fun refresh() {
        performSearch(_uiState.value.searchQuery)
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            deleteEmployeeUseCase(id)
                .onSuccess {
                    loadEmployees()
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

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryDataStore.clearHistory()
        }
    }
}