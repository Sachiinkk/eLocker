package com.example.elocker.viewmodel
import android.util.Log

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elocker.data.remote.FormData
import com.example.elocker.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.elocker.data.remote.OtpVerificationRequest
import com.example.elocker.data.remote.AadhaarRequest
import com.example.elocker.utils.encryptAadhaar
import com.example.elocker.utils.PUBLIC_KEY_FROM_SERVER
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    val name = mutableStateOf("")
    val fatherName = mutableStateOf("")
    val motherName = mutableStateOf("")
    val dateOfBirth = mutableStateOf("")
    val gender = mutableStateOf("")
    val genderExpanded = mutableStateOf(false)
    val aadhaarNumber = mutableStateOf("")
    val apiResponse = mutableStateOf("")

    val isLoading = mutableStateOf(false)
    val message = mutableStateOf<String?>(null)
    val isAadhaarAuthenticated = MutableStateFlow(false)

    fun setAadhaarAuthenticated(value: Boolean) {
        isAadhaarAuthenticated.value = value
    }

    fun onNameChange(newName: String) {
        name.value = newName
    }

    fun onFatherNameChange(newName: String) {
        fatherName.value = newName
    }

    fun onMotherNameChange(newName: String) {
        motherName.value = newName
    }

    fun onDateOfBirthChange(newDob: String) {
        dateOfBirth.value = newDob
    }

    fun onGenderSelect(selected: String) {
        gender.value = selected
        genderExpanded.value = false
    }

    fun onGenderExpandToggle() {
        genderExpanded.value = !genderExpanded.value
    }

    fun onAadhaarChange(newAadhaar: String) {
        aadhaarNumber.value = newAadhaar
    }

    fun clearMessage() {
        message.value = null
    }
    fun resendOtp() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = repository.sendOtp(lastEncryptedAadhaar)
                message.value = if (response.isSuccessful) "OTP resent ✅" else "Failed to resend OTP"
            } catch (e: Exception) {
                message.value = "Resend error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }


    fun submitForm(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val data = FormData(
                    name = name.value,
                    fatherName = fatherName.value,
                    motherName = motherName.value,
                    dateOfBirth = dateOfBirth.value,
                    gender = gender.value,
                    aadhaarNumber = aadhaarNumber.value
                )

                val response = repository.submitForm(data)

                if (response.isSuccessful) {
                    val responseBody = "Submitted successfully"
                    apiResponse.value = responseBody
                    message.value = responseBody
                    clearForm()
                    onSuccess()
                } else {
                    message.value = "Submission failed: ${response.code()}"
                }
            } catch (e: Exception) {
                message.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
    var lastTxn = ""
    var lastEncryptedAadhaar = ""

    fun authenticateAadhaar(
        encryptedAadhaar: String,
        onOtpSent: () -> Unit
    ) {
        viewModelScope.launch {

            try {
                isLoading.value = true
                lastEncryptedAadhaar = encryptedAadhaar
                val encrypted = encryptAadhaar(aadhaarNumber.value, PUBLIC_KEY_FROM_SERVER)
                lastEncryptedAadhaar = encrypted // Store for OTP verification

                val response = repository.authenticateAadhaar(encrypted)
                Log.d("AadhaarResponse", response.code().toString())
                Log.d("AadhaarResponse", response.errorBody()?.string() ?: "No error body")

                if (response.isSuccessful) {
                    val otpResponse = repository.sendOtp(encrypted)
                    if (otpResponse.isSuccessful) {
                        // Assume txn is in headers or parse from body if needed
                        lastTxn = otpResponse.headers()["txn"] ?: ""
                        message.value = "OTP sent successfully"
                        onOtpSent()
                    } else {
                        message.value = "Failed to send OTP"
                    }
                } else {
                    message.value = "Aadhaar mismatch"
                }
            } catch (e: Exception) {
                message.value = "Auth error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }


    fun showValidationError() {
        message.value = "Please fix the errors in the form."
    }

    fun verifyOtp(otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val request = OtpVerificationRequest(
                    otp = otp,
                    aadhaar = lastEncryptedAadhaar,
                    txn = lastTxn
                )
                val response = repository.verifyOtp(
                    aadhaar = lastEncryptedAadhaar,
                    otp = otp,
                    txn = lastTxn
                )
                if (response.isSuccessful) {
                    message.value = "OTP verified ✅"
                    isAadhaarAuthenticated.value = true
                    onSuccess()
                } else {
                    message.value = "Invalid OTP"
                }
            } catch (e: Exception) {
                message.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }


    private fun clearForm() {
        name.value = ""
        fatherName.value = ""
        motherName.value = ""
        dateOfBirth.value = ""
        gender.value = ""
        aadhaarNumber.value = ""
    }
}
