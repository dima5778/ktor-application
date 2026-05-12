package com.example.directoryapplication.data.repository


import com.example.directoryapplication.data.remote.DirectoryApi
import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.repository.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val api: DirectoryApi
) : EmployeeRepository {

    override suspend fun getAllEmployees(): Result<List<Employee>> {
        return try {
            val employees = api.getAllEmployees().map { dto ->
                Employee(
                    id = dto.id,
                    name = dto.name,
                    position = dto.position,
                    phone = dto.phone,
                    email = dto.email,
                    department = dto.department
                )
            }
            Result.success(employees)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEmployeeById(id: Int): Result<Employee> {
        return try {
            val dto = api.getEmployeeById(id)
            Result.success(
                Employee(
                    id = dto.id,
                    name = dto.name,
                    position = dto.position,
                    phone = dto.phone,
                    email = dto.email,
                    department = dto.department
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchEmployees(query: String): Result<List<Employee>> {
        return try {
            val employees = api.searchEmployees(query).map { dto ->
                Employee(
                    id = dto.id,
                    name = dto.name,
                    position = dto.position,
                    phone = dto.phone,
                    email = dto.email,
                    department = dto.department
                )
            }
            Result.success(employees)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}