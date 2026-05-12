package com.example.directoryapplication


import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.repository.EmployeeRepository
import com.example.directoryapplication.domain.usecase.GetEmployeesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetEmployeesUseCaseTest {

    private lateinit var repository: EmployeeRepository
    private lateinit var useCase: GetEmployeesUseCase

    private val testEmployees = listOf(
        Employee(1, "Иван Иванов", "Разработчик", "+7999", "ivan@test.com", "IT"),
        Employee(2, "Мария Петрова", "Дизайнер", "+7888", "maria@test.com", "IT")
    )

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetEmployeesUseCase(repository)
    }

    @Test
    fun `invoke returns success with employees list`() = runTest {
        coEvery { repository.getAllEmployees() } returns Result.success(testEmployees)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Иван Иванов", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `invoke returns failure when repository throws`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getAllEmployees() } returns Result.failure(exception)

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke returns empty list when no employees`() = runTest {
        coEvery { repository.getAllEmployees() } returns Result.success(emptyList())

        val result = useCase()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }
}