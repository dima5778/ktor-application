package com.example.directoryapplication.presentation.directory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.directoryapplication.presentation.directory.components.EmployeeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    isDarkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    onEmployeeClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onLogout: () -> Unit,
    shouldRefresh: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    viewModel: DirectoryViewModel = hiltViewModel()
) {
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.refresh()
            onRefreshHandled()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState(initial = emptyList())

    var deleteId by remember { mutableStateOf<Int?>(null) }

    deleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text("Удалить сотрудника?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEmployee(id)
                    deleteId = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteId = null }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Справочник сотрудников") },
                actions = {
                    // Кнопка переключения темы
                    IconButton(onClick = onThemeToggle) {
                        // Меняем иконку в зависимости от текущей темы
                        if (isDarkTheme) {
                            Icon(Icons.Default.LightMode, contentDescription = "Включить светлую тему")
                        } else {
                            Icon(Icons.Default.DarkMode, contentDescription = "Включить темную тему")
                        }
                    }
                    // Кнопка выхода
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Выйти")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Добавить", style = MaterialTheme.typography.labelLarge) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // === Строка поиска ===
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск по имени, должности...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.onSearchQueryChange("")
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Очистить",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.performSearch(uiState.searchQuery)
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // === История поиска + кнопка очистки ===
            if (searchHistory.isNotEmpty() && uiState.searchQuery.isBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Недавние запросы",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TextButton(onClick = { viewModel.clearSearchHistory() }) {
                        Text("Очистить", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(searchHistory) { query ->
                        SuggestionChip(
                            onClick = { viewModel.onSearchQueryChange(query) },
                            label = { Text(query) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // === Основной контент ===
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.performSearch("") }) { Text("Повторить") }
                        }
                    }
                }
                uiState.employees.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Сотрудники не найдены", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(uiState.employees, key = { it.id }) { employee ->
                            EmployeeCard(
                                employee = employee,
                                onClick = { onEmployeeClick(employee.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}