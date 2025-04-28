package com.example.elocker.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import com.google.gson.annotations.SerializedName

// Form data model for final submission
data class FormData(
    val name: String,
    val fatherName: String,
    val motherName: String,
    val dateOfBirth: String,
    val gender: String,
    val aadhaarNumber: String
)

// Aadhaar encryption payload
data class AadhaarRequest(
    val name: String = "Test", // optional
    @SerializedName("AadhaarNo") val aadhaar: String
)



// OTP payload
data class OtpVerificationRequest(
    val otp: String,
    val aadhaar: String,
    val txn: String
)





interface ApiService {
    // Submit form after Aadhaar and OTP verification
    @POST("submitForm")
    suspend fun submitForm(@Body formData: FormData): Response<Void>

    // Step 1: Authenticate Aadhaar (encrypted on frontend)
    @POST("HealthaadhaarValidate/AadhaarSendOtp/")
    suspend fun authenticateAadhaar(@Body request: AadhaarRequest): Response<Void>


    // Step 2: Trigger OTP if Aadhaar matches
    @POST("HealthaadhaarValidate/AadhaarSendOtp/")
    suspend fun sendOtp(@Body request: AadhaarRequest): Response<Void>

    // Step 3: Verify OTP entered by user
    @POST("HealthaadhaarValidate/AadhaarOTPBasedEkyc/")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<Void>


}

