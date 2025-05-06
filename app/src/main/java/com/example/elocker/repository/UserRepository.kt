package com.example.elocker.repository

import com.example.elocker.data.remote.*
import retrofit2.Response
import okhttp3.ResponseBody
import javax.inject.Inject
import com.example.elocker.model.UserDetails
class UserRepository @Inject constructor() {

    // Submit registration form
    suspend fun submitForm(data: FormData): Response<Void> {
        return RetrofitInstance.api.submitForm(data)
    }

    // Authenticate Aadhaar (encrypt Aadhaar and call API)
    suspend fun authenticateAadhaar(encrypted: String): Response<Void> {
        return RetrofitInstance.api.authenticateAadhaar(AadhaarRequest(aadhaar = encrypted))
    }

    // Send OTP
    suspend fun sendOtp(encrypted: String): Response<SendOtpResponse> {
        return RetrofitInstance.api.sendOtp(AadhaarRequest(aadhaar = encrypted))
    }

    // Verify OTP
    suspend fun verifyOtp(request: OtpVerificationRequest): Response<VerifyOtpResponse> {
        return RetrofitInstance.api.verifyOtp(request)
    }

    // Fetch user details after Aadhaar authentication
    suspend fun getUserDetails(token: String): Response<UserDetails> {
        return RetrofitInstance.api.getUserDetails("Bearer $token")
    }
}
