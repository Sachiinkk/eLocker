package com.example.elocker.repository

import com.example.elocker.data.remote.*
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor() {

    // Submit final registration form
    suspend fun submitForm(data: FormData): Response<Void> {
        return RetrofitInstance.api.submitForm(data)
    }

    // Aadhaar encryption auth API
    suspend fun authenticateAadhaar(encrypted: String): Response<Void> {
        return RetrofitInstance.api.authenticateAadhaar(AadhaarRequest(aadhaar = encrypted))

    }

    // Send OTP
    suspend fun sendOtp(encrypted: String): Response<Void> {
        return RetrofitInstance.api.authenticateAadhaar(AadhaarRequest(aadhaar = encrypted))

    }

    // (Optional) Verify OTP
    suspend fun verifyOtp(aadhaar: String, otp: String, txn: String): Response<Void> {
        return RetrofitInstance.api.verifyOtp(OtpVerificationRequest(otp = otp, aadhaar = aadhaar, txn = txn))
    }

}
