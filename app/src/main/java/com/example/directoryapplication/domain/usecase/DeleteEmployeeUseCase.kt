// domain/usecase/DeleteEmployeeUseCase.kt
package com.example.directoryapplication.domain.usecase

import com.example.directoryapplication.domain.repository.EmployeeRepository
import javax.inject.Inject

class DeleteEmployeeUseCase @Inject constructor(
    private val repository: EmployeeRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.deleteEmployee(id)
    }
}