package com.example.elocker.repository

import com.example.elocker.data.remote.*
import retrofit2.Response
import okhttp3.ResponseBody
import javax.inject.Inject
class UserRepository @Inject constructor() {

    // Submit registration form
//    suspend fun submitForm(data: FormData): Response<Void> {
//        return RetrofitInstance.api2.submitForm(data)
//    }



    // Send OTP
    suspend fun sendOtp(encrypted: String): Response<SendOtpResponse> {
        return RetrofitInstance.api.sendOtp(AadhaarRequest(aadhaar = encrypted))
    }

    // Verify OTP
    suspend fun verifyOtp(request: OtpVerificationRequest): Response<VerifyOtpResponse> {
        return RetrofitInstance.api.verifyOtp(request)
    }

    // Fetch user details after Aadhaar authentication
    suspend fun getUserDetails(token: String, request: UserInfoRequest): Response<UserDocumentResponse> {
        return RetrofitInstance.api2.getUserDocuments("Bearer $token", request)
    }
    suspend fun viewDocument( request: ViewDocumentRequest): Response<ViewDocumentResponse> {
        return RetrofitInstance.api3.viewDocument(request)
    }
}
