package com.example.directoryapplication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

@Singleton
class SearchHistoryDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("search_history")

    val searchHistory: Flow<List<String>> = context.searchDataStore.data.map { preferences ->
        preferences[SEARCH_HISTORY_KEY]?.toList()?.sortedByDescending { it } ?: emptyList()
    }

    suspend fun addSearchQuery(query: String) {
        if (query.isBlank()) return

        context.searchDataStore.edit { preferences ->
            val currentSet = preferences[SEARCH_HISTORY_KEY] ?: emptySet()

            // Исправлено: преобразуем в List перед takeLast
            val updatedList = (currentSet + query)
                .toList()
                .distinct()           // убираем дубликаты
                .takeLast(10)         // оставляем последние 10

            preferences[SEARCH_HISTORY_KEY] = updatedList.toSet()
        }
    }

    suspend fun clearHistory() {
        context.searchDataStore.edit { it.clear() }
    }
}