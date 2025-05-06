package com.example.elocker.viewmodel
import kotlinx.coroutines.delay
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elocker.data.remote.FormData
import com.example.elocker.data.remote.OtpVerificationRequest
import com.example.elocker.repository.UserRepository
import com.example.elocker.utils.PUBLIC_KEY_FROM_SERVER
import com.example.elocker.utils.encryptAadhaar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    val userToken = mutableStateOf("")

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
    val showOtpPopup = mutableStateOf(false)
    val showSuccessDialog = mutableStateOf(false)

    var lastTxn = ""
    var lastEncryptedAadhaar = ""

    // ----------------- Input Change Functions -----------------

    fun onNameChange(newName: String) { name.value = newName }
    fun onFatherNameChange(newName: String) { fatherName.value = newName }
    fun onMotherNameChange(newName: String) { motherName.value = newName }
    fun onDateOfBirthChange(newDob: String) { dateOfBirth.value = newDob }
    fun onGenderSelect(selected: String) {
        gender.value = selected
        genderExpanded.value = false
    }
    fun onGenderExpandToggle() { genderExpanded.value = !genderExpanded.value }
    fun onAadhaarChange(newAadhaar: String) { aadhaarNumber.value = newAadhaar }
    fun clearMessage() { message.value = null }
    fun setAadhaarAuthenticated(value: Boolean) {
        isAadhaarAuthenticated.value = value
    }

    // ----------------- Submit Form -----------------

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

                // ✅ Print Form Data to Console (without plain Aadhaar)
                Log.d("FormSubmission", "---- Form Data ----")
                Log.d("FormSubmission", "Full Name: ${data.name}")
                Log.d("FormSubmission", "Father Name: ${data.fatherName}")
                Log.d("FormSubmission", "Mother Name: ${data.motherName}")
                Log.d("FormSubmission", "Date of Birth: ${data.dateOfBirth}")
                Log.d("FormSubmission", "Gender: ${data.gender}")

                val encryptedAadhaar = encryptAadhaar(data.aadhaarNumber, PUBLIC_KEY_FROM_SERVER)
                Log.d("FormSubmission", "Encrypted Aadhaar: $encryptedAadhaar")

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

    // ----------------- Authenticate Aadhaar -----------------

    fun authenticateAadhaar(encryptedAadhaar: String, onOtpSent: () -> Unit) {
        viewModelScope.launch {
            if (otpCooldown.value > 0) {
                message.value = "⏳ Please wait ${otpCooldown.value} sec before retrying."
                return@launch
            }

            isLoading.value = true
            try {
                lastEncryptedAadhaar = encryptedAadhaar

                val otpResponse = repository.sendOtp(encryptedAadhaar)
                if (otpResponse.isSuccessful) {
                    val body = otpResponse.body()
                    Log.d("OTP_FULL_RESPONSE", "SendOtpResponse: $body")
                    showOtpPopup.value = true

                    if (body?.response == "1" && body.response_code == "200") {
                        lastTxn = body.sys_message.trim()
                        if (lastTxn.isNotBlank()) {
                            message.value = "✅ OTP sent"
                            startOtpCooldown()
                            onOtpSent()
                        } else {
                            message.value = "❌ Empty txn received"
                        }
                    } else if (body?.sys_message?.contains("flooding", true) == true) {
                        message.value = "❌ OTP flooding detected. Please wait."
                    } else {
                        message.value = "❌ ${body?.sys_message ?: "OTP send failed"}"
                    }
                } else {
                    message.value = "❌ Failed to send OTP"
                }
            } catch (e: Exception) {
                message.value = "❌ Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    val otpCooldown = mutableStateOf(0)

    fun startOtpCooldown() {
        viewModelScope.launch {
            otpCooldown.value = 60
            while (otpCooldown.value > 0) {
                delay(1000)
                otpCooldown.value -= 1
            }
        }
    }








    // ----------------- Verify OTP -----------------

    fun verifyOtp(otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val request = OtpVerificationRequest(
                    otp = otp,
                    aadhaar = lastEncryptedAadhaar.trim(),
                    txn = lastTxn.trim()
                )

                val response = repository.verifyOtp(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("OTP_VERIFY", "Parsed Response: $body")
                    val errorBody = response.errorBody()?.string()
                    Log.d("OTP_RAW_ERROR", "Error Body: $errorBody")


                    if (body?.response == "1" && body.response_code == "200") {

                        val token = body?.data?.tkn
                        if (!token.isNullOrEmpty()) {
                            userToken.value = token
                            isAadhaarAuthenticated.value = true
                            message.value = "✅ OTP Verified"
                            showSuccessDialog.value = true
                            showOtpPopup.value = false
//                            onSuccess()
                        } else {
                            message.value = "❌ Verification token missing"
                        }

                    } else {
                        message.value = "❌ ${body?.sys_message ?: "Verification failed"}"
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("OTP_VERIFY", "Error Body: $error")
                    message.value = "❌ OTP verification failed"
                }
            } catch (e: Exception) {
                message.value = "❌ Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

//------------onNext Click----------------

    fun onNextClicked() {
        // 🚀 Handle logic after success dialog (navigate or scroll form etc.)
        showSuccessDialog.value = false
    }
    // ----------------- Utility -----------------

    private fun clearForm() {
        name.value = ""
        fatherName.value = ""
        motherName.value = ""
        dateOfBirth.value = ""
        gender.value = ""
        aadhaarNumber.value = ""
    }

    fun showValidationError() {
        message.value = "Please fix the errors in the form."
    }


    //--------------------------------Fetch User details----------------------------
    fun fetchUserDetails(token: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = repository.getUserDetails(token)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val jsonLog = """
                        {
                          "username": "${user.username}",
                          "fathername": "${user.fathername}",
                          "mothername": "${user.mothername}",
                          "gender": "${user.gender}",
                          "aadhar_verification_id": "${user.aadhar_verification_id}",
                          "dob": "${user.dob}"
                        }
                    """.trimIndent()
                        Log.d("UserDetailsJSON", jsonLog)
                        message.value = "User details fetched ✅"
                    }
                } else {
                    message.value = "Failed to fetch user details ❌"
                }
            } catch (e: Exception) {
                message.value = "Error fetching details: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

}
