package com.example.directoryapplication.domain.repository

import com.example.directoryapplication.domain.model.Employee

interface EmployeeRepository {
    suspend fun getAllEmployees(): Result<List<Employee>>
    suspend fun getEmployeeById(id: Int): Result<Employee>
    suspend fun searchEmployees(query: String): Result<List<Employee>>
    suspend fun createEmployee(employee: Employee): Result<Employee>
    suspend fun updateEmployee(id: Int, employee: Employee): Result<Boolean>
    suspend fun deleteEmployee(id: Int): Result<Unit>
}