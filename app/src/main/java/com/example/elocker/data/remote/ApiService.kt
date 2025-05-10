package com.example.elocker.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers
import  retrofit2.http.GET

import retrofit2.Response
import com.google.gson.annotations.SerializedName
import com.google.gson.JsonObject
import retrofit2.http.Header

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
    val aadhaarrefid: String?,
    val response: String,
    val data: OtpVerificationData?,
    val sys_message: String,
    val response_code: String
)

data class OtpVerificationData(
    val poi: Poi?,
    val poa: Poa?,
    val lData: LData?,
    val pht: String?,

    @SerializedName("tkn")
    val tkn: String?,

    @SerializedName("uid")
    val uid: String?,

    @SerializedName("aadharVaultKey")
    val aadharVaultKey: String?
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
    val pc: String?, // Use String to avoid parsing issues with leading zeros
    val state: String?,
    val street: String?,
    val vtc: String?
)

data class LData(
    val co: String?,
    val country: String?,
    val dist: String?,
    val house: String?,
    val lang: Any?, // use Any if it could be int or string
    val lm: String?,
    val loc: String?,
    val name: String?,
    val pc: String?,
    val state: String?,
    val street: String?,
    val vtc: String?
)

//------------get documents--------------
data class UserDocumentResponse(
    val response: Int,
    val sys_message: String?,
    val data: List<UserDataWrapper>?
)

data class UserDataWrapper(
    val type: String,
    val attributes: UserAttributes
)

data class UserAttributes(
    val data: UserData
)

data class UserData(
    val message: String,
    val user: UserInfo
)

data class UserInfo(
    val dbResults: List<DbResult>?,
    val licenceDetails: LicenceDetails
)

data class DbResult(
    val Citizenid: String?,
    val Name: String?,
    val Application_IDs: String?,
    val Document_IDs: String?,
    val services_name: String?
)

data class LicenceDetails(
    val response: String,
    val sys_message: String?,
    val data: List<LicenceDocument>
)

data class LicenceDocument(
    val response: String,
    val applicationID: String,
    val output_path: String,
    val licenseID: String,
    val doc_sr_no: String,
    val issued_on: String,
    val valid_upto: String?,
    val service_name: String
)


//-----------request ---------------
data class UserInfoRequest(
    val username: String,
    val fathername: String,
    val mothername: String,
    val gender: String,
    val aadhar_verification_id: String,
    val dob: String
)
interface ApiService {


    // Step 1: Authenticate Aadhaar (encrypted on frontend)
//    @POST("HealthaadhaarValidate/AadhaarSendOtp/")
//    suspend fun authenticateAadhaar(@Body request: AadhaarRequest): Response<Void>


    // Step 2: Trigger OTP if Aadhaar matches
    @POST("HealthaadhaarValidate/AadhaarSendOtp/")
    suspend fun sendOtp(@Body request: AadhaarRequest): Response<SendOtpResponse>

    // Step 3: Verify OTP entered by user
    @POST("HealthaadhaarValidate/AadhaarOTPBasedEkyc/")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<VerifyOtpResponse>
}

interface  ApiService2{
    @POST("common/v1/Fetch-elocker")
//    @Headers("Content-Type: application/json")
    suspend fun getUserDocuments(
        @Header("Authorization") authHeader: String,
    @Body request: UserInfoRequest
): Response<UserDocumentResponse>
}