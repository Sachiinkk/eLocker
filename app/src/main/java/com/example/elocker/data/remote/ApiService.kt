package com.example.elocker.data.remote

import android.media.session.MediaSession.Token
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers


import retrofit2.Response
import com.google.gson.annotations.SerializedName

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
    @SerializedName("dbResults") val dbResults: List<DbResult>?,
    @SerializedName("licenceDetails") val licenceDetails: LicenceDetails
)

data class DbResult(
    @SerializedName("Citizenid") val Citizenid: String?,
    @SerializedName("Name") val Name: String?,
    @SerializedName("Application_IDs") val Application_IDs: String?,
    @SerializedName("Document_IDs") val Document_IDs: String?,
    @SerializedName("Total_licenses") val Total_licenses: Int?,
    @SerializedName("services_name") val services_name: String?

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

//---------------------get Base64 pdf -----------
//----------------request---------------------
data class ViewDocumentRequest(
    val docSrNo: String
)

//-----------------response---------------
data class ViewDocumentResponse(
    val response: Int,
    val sys_message: String?,
    val data: List<ViewDocumentData>?
)

data class ViewDocumentData(
    val base64Pdf: String
)
interface ApiService {
    @POST("HealthaadhaarValidate/AadhaarSendOtp/")
    suspend fun sendOtp(@Body request: AadhaarRequest): Response<SendOtpResponse>

    // Step 3: Verify OTP entered by user
    @POST("HealthaadhaarValidate/AadhaarOTPBasedEkyc/")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): Response<VerifyOtpResponse>
}

interface  ApiService2{
    @POST("common/v1/Fetch-elocker")
    @Headers("Content-Type: application/json")
    suspend fun getUserDocuments(
        @Header("Authorization") token: String,
        @Body request: UserInfoRequest
    ): Response<UserDocumentResponse>
}

interface  ApiService3{
    @POST("Verification/GetDsnDetails")
    suspend fun viewDocument(
        @Body request: ViewDocumentRequest
    ): Response<ViewDocumentResponse>
}
