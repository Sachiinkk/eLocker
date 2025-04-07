package com.example.elocker.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

data class FormData(
    val name: String,
    val fatherName: String,
    val motherName: String,
    val dateOfBirth: String,
    val gender: String,
    val aadhaarNumber: String
)

interface ApiService {
    @POST("submitForm")
    suspend fun submitForm(@Body formData: FormData): Response<Void>
}
