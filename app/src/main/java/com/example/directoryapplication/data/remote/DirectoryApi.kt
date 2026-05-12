package com.example.directoryapplication.data.remote


import com.example.directoryapplication.data.remote.dto.EmployeeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DirectoryApi {
    @GET("api/employees")
    suspend fun getAllEmployees(): List<EmployeeDto>

    @GET("api/employees/{id}")
    suspend fun getEmployeeById(@Path("id") id: Int): EmployeeDto

    @GET("api/employees/search")
    suspend fun searchEmployees(@Query("q") query: String): List<EmployeeDto>
}