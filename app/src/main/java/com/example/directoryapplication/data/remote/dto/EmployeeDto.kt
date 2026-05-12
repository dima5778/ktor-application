package com.example.directoryapplication.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmployeeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("department") val department: String
)