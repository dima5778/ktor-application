package com.example.directoryapplication.domain.usecase


import com.example.directoryapplication.domain.model.Employee
import com.example.directoryapplication.domain.repository.EmployeeRepository
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    suspend operator fun invoke(): Result<List<Employee>> {
        return repository.getAllEmployees()
    }
}