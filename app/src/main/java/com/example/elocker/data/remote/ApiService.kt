package com.example.elocker.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import  retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.Response
import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject

import  com.example.elocker.model.UserDetails
// Form data model for final submission
data class FormData(
    val name: String,
    val fatherName: String,
    val motherName: String,
    val dateOfBirth: String,
    val gender: String,
    val aadhaarNumber: String
)

// Adhar Request API

data class AadhaarRequest(
    val name: String = "Test", // optional
    @SerializedName("AadhaarNo") val aadhaar: String
)

data class SendOtpResponse(
    val aadhaarrefid: String?,
    val response: String,
    val data: String?,
    val sys_message: String,
    val response_code: String
)

// OTP Verification API
data class OtpVerificationRequest(
    @SerializedName("OTP") val otp: String,
    @SerializedName("Aadhaar") val aadhaar: String,
    @SerializedName("txn") val txn: String
)
data class VerifyOtpResponse(
    val aadhar_verification_id: String?,
    val response: String,
    val response_code: String,
    val data: OtpData?,
    val sys_message: String
)
data class OtpData(
    val poi: Poi?,                   // Personal Info
    val poa: Poa?,                   // Address Info
    val lData: LData?,               // Localized Address Info
    val pht: String?,                // Base64 photo string
    val tkn: String?,                // Verification token
    val uid: String?,                // Aadhaar number
    val aadharVaultKey: String?     // Vault key
)
data class Poi(
    val dob: String?,
    val gender: String?,
    val name: String?
)

data class Poa(
    val co: String?,
    val country: String?,
    val dist: String?,
    val house: String?,
    val lm: String?,
    val loc: String?,
    val pc: String?,     // Use `String` if PIN might start with 0
    val state: String?,
    val street: String?,
    val vtc: String?
)

data class LData(
    val co: String?,
    val country: String?,
    val dist: String?,
    val house: String?,
    val lang: String?,
    val lm: String?,
    val loc: String?,
    val name: String?,
    val pc: String?,
    val state: String?,
    val street: String?,
    val vtc: String?
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
    suspend fun sendOtp(@Body request: AadhaarRequest): Response<SendOtpResponse>

    // Step 3: Verify OTP entered by user
    @POST("HealthaadhaarValidate/AadhaarOTPBasedEkyc/")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<VerifyOtpResponse>





}

interface  ApiService2{
    @POST("common/v1/Fetch-elocker")
    suspend fun getUserDetails(@Header("Authorization") token: String): Response<UserDetails>
}