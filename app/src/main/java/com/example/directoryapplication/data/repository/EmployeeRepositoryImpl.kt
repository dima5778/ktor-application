package com.example.directoryapplication.data.repository

import com.example.directoryapplication.data.remote.DirectoryApi
import com.example.directoryapplication.data.remote.EmployeeRequest
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.repository.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val api: DirectoryApi
) : EmployeeRepository {

    private fun EmployeeDto.toDomain() = Employee(
        id = id, name = name, position = position,
        phone = phone, email = email, department = department
    )

    override suspend fun getAllEmployees(): Result<List<Employee>> = try {
        Result.success(api.getAllEmployees().map { it.toDomain() })
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getEmployeeById(id: Int): Result<Employee> = try {
        Result.success(api.getEmployeeById(id).toDomain())
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun searchEmployees(query: String): Result<List<Employee>> = try {
        Result.success(api.searchEmployees(query).map { it.toDomain() })
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun createEmployee(employee: Employee): Result<Employee> = try {
        val dto = api.createEmployee(
            EmployeeRequest(employee.name, employee.position, employee.phone, employee.email, employee.department)
        )
        Result.success(dto.toDomain())
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateEmployee(id: Int, employee: Employee): Result<Boolean> = try {
        val response = api.updateEmployee(
            id, EmployeeRequest(employee.name, employee.position, employee.phone, employee.email, employee.department)
        )
        Result.success(response.isSuccessful)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun deleteEmployee(id: Int): Result<Unit> = try {
        val response = api.deleteEmployee(id)
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete employee. Code: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}


private typealias EmployeeDto = com.example.directoryapplication.data.remote.dto.EmployeeDto