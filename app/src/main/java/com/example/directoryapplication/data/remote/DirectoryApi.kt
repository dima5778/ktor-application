package com.example.directoryapplication.data.remote

import com.example.directoryapplication.data.remote.dto.EmployeeDto
import retrofit2.http.*

data class EmployeeRequest(
    val name: String,
    val position: String,
    val phone: String,
    val email: String,
    val department: String
)

interface DirectoryApi {
    @GET("api/employees")
    suspend fun getAllEmployees(): List<EmployeeDto>

    @GET("api/employees/{id}")
    suspend fun getEmployeeById(@Path("id") id: Int): EmployeeDto

    @GET("api/employees/search")
    suspend fun searchEmployees(@Query("q") query: String): List<EmployeeDto>

    @POST("api/employees")
    suspend fun createEmployee(@Body request: EmployeeRequest): EmployeeDto

    @PUT("api/employees/{id}")
    suspend fun updateEmployee(@Path("id") id: Int, @Body request: EmployeeRequest): retrofit2.Response<Unit>

    @DELETE("api/employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: Int): retrofit2.Response<Unit>
}