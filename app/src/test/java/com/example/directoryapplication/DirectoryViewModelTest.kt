package com.example.directoryapplication

import app.cash.turbine.test
import com.example.directoryapplication.data.local.SearchHistoryDataStore
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.usecase.DeleteEmployeeUseCase
import com.example.directoryapplication.domain.usecase.GetEmployeesUseCase
import com.example.directoryapplication.domain.usecase.SearchEmployeesUseCase
import com.example.directoryapplication.presentation.directory.DirectoryViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DirectoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getEmployeesUseCase: GetEmployeesUseCase
    private lateinit var searchEmployeesUseCase: SearchEmployeesUseCase
    private lateinit var deleteEmployeeUseCase: DeleteEmployeeUseCase
    private lateinit var searchHistoryDataStore: SearchHistoryDataStore
    private lateinit var viewModel: DirectoryViewModel

    private val testEmployees = listOf(
        Employee(1, "Иван Иванов", "Разработчик", "+79991234567", "ivan@test.com", "IT")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getEmployeesUseCase = mockk()
        searchEmployeesUseCase = mockk()
        deleteEmployeeUseCase = mockk()
        searchHistoryDataStore = mockk(relaxed = true) // relaxed = true, чтобы не мокировать все методы

        coEvery { getEmployeesUseCase() } returns Result.success(testEmployees)
        coEvery { searchEmployeesUseCase(any()) } returns Result.success(testEmployees)
        coEvery { deleteEmployeeUseCase(any()) } returns Result.success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load populates employees`() = runTest {
        viewModel = DirectoryViewModel(
            getEmployeesUseCase,
            searchEmployeesUseCase,
            deleteEmployeeUseCase,
            searchHistoryDataStore
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.employees.size)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }
    }

    @Test
    fun `error state shown when use case fails`() = runTest {
        coEvery { getEmployeesUseCase() } returns Result.failure(Exception("Ошибка сети"))

        viewModel = DirectoryViewModel(
            getEmployeesUseCase,
            searchEmployeesUseCase,
            deleteEmployeeUseCase,
            searchHistoryDataStore
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.error!!.contains("Ошибка"))
        }
    }

    @Test
    fun `performSearch updates employees correctly`() = runTest {
        viewModel = DirectoryViewModel(
            getEmployeesUseCase,
            searchEmployeesUseCase,
            deleteEmployeeUseCase,
            searchHistoryDataStore
        )

        viewModel.performSearch("Иван")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.employees.size)
            assertFalse(state.isLoading)
        }
    }
}