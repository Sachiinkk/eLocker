package com.example.elocker.viewmodel
import android.media.session.MediaSession.Token
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
import androidx.compose.runtime.mutableStateListOf
import com.example.elocker.data.remote.UserInfoRequest
import com.example.elocker.data.remote.LicenceDocument
import com.example.elocker.data.remote.UserDocumentResponse
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*


@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    val userToken = mutableStateOf("")
    val aadhaarUid = mutableStateOf("")
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
    val vaultKey = mutableStateOf("")






    //------------------Document--------------------
    val issuedDocs = mutableStateListOf<LicenceDocument>()
    val expiredDocs = mutableStateListOf<LicenceDocument>()




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

//    fun submitForm(navController: NavController,
//                   onSuccess: () -> Unit = {}) {
//        viewModelScope.launch {
//            isLoading.value = true
//            try {
//                val data = FormData(
//                    name = name.value,
//                    fatherName = fatherName.value,
//                    motherName = motherName.value,
//                    dateOfBirth = dateOfBirth.value,
//                    gender = gender.value,
//                    aadhaarNumber = aadhaarNumber.value
//                )
//
//                Log.d("SUBMIT_DATA", "name=${name.value}, father=${fatherName.value}, mother=${motherName.value}, dob=${dateOfBirth.value}, gender=${gender.value}, aadhaar=${aadhaarNumber.value}")
//
//                val encryptedAadhaar = encryptAadhaar(data.aadhaarNumber, PUBLIC_KEY_FROM_SERVER)
//                Log.d("FormSubmission", "Encrypted Aadhaar: $encryptedAadhaar")
//
//                val response = repository.submitForm(data)
//
//                message.value = "Submission failed: ${response.code()} ${response.message()}"
//                Log.e("SUBMIT_FAIL", "Error Body: ${response.errorBody()?.string()}")
//
//                if (response.isSuccessful) {
//                    message.value = "Submitted successfully"
//                    clearForm()
//
//                    // 🔁 Now fetch documents
//                    val request = UserInfoRequest(
//                        username = data.name,
//                        fathername = data.fatherName,
//                        mothername = data.motherName,
//                        gender = data.gender,
//                        aadhar_verification_id = userToken.value, // OR aadharVaultKey.value
//                        dob = data.dateOfBirth
//                    )
//                    fetchUserDetails(request)
//
//                    navController.navigate("documents_screen")
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e("SUBMIT_FAIL", "Code: ${response.code()}, Error Body: $error")
//                    message.value = "Submission failed: $error"
//
//                }
//            } catch (e: Exception) {
//                message.value = "Error: ${e.message}"
//            } finally {
//                isLoading.value = false
//            }
//        }
//    }


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
//

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



//-------------reSendOtp-------------------------
fun resendOtp() {
    viewModelScope.launch {
        if (otpCooldown.value > 0) {
            message.value = "⏳ Please wait ${otpCooldown.value} sec before retrying."
            return@launch
        }

        isLoading.value = true
        try {
            val otpResponse = repository.sendOtp(lastEncryptedAadhaar)
            if (otpResponse.isSuccessful) {
                val body = otpResponse.body()
                Log.d("RESEND_OTP_RESPONSE", "SendOtpResponse: $body")

                if (body?.response == "1" && body.response_code == "200") {
                    lastTxn = body.sys_message.trim()
                    if (lastTxn.isNotBlank()) {
                        message.value = "✅ OTP resent"
                        startOtpCooldown()
                    } else {
                        message.value = "❌ Empty txn received on resend"
                    }
                } else {
                    message.value = "❌ ${body?.sys_message ?: "OTP resend failed"}"
                }
            } else {
                message.value = "❌ Failed to resend OTP"
            }
        } catch (e: Exception) {
            message.value = "❌ Error: ${e.message}"
        } finally {
            isLoading.value = false
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

                    val token = body?.data?.tkn
                    val uid = body?.data?.uid
                    val vault = body?.data?.aadharVaultKey
                    vaultKey.value = body?.data?.aadharVaultKey ?: ""

                    Log.d("OTP_TOKEN", "Token: $token, UID: $uid, VaultKey: $vault")

                    if (!token.isNullOrEmpty()) {
                        // Save Aadhaar user info
                        body.data?.poi?.let { poi ->
                            name.value = poi.name.orEmpty()
                            gender.value = poi.gender.orEmpty()
                            dateOfBirth.value = poi.dob.orEmpty()
                        }

                        // Save identifiers
                        userToken.value = token
                        vaultKey.value = vault ?: ""
                        aadhaarUid.value = uid ?: ""

                        isAadhaarAuthenticated.value = true
                        message.value = "✅ OTP Verified"
                        showSuccessDialog.value = true
                        showOtpPopup.value = false
                    } else {
                        message.value = "❌ Verification token missing"
                    }

                } else {
                    Log.e("OTP_VERIFY", "Failed: ${response.errorBody()?.string()}")
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

    fun onNextClicked(request: UserInfoRequest) {
        showSuccessDialog.value = false

        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = repository.getUserDetails(userToken.value, request)


                if (response.isSuccessful) {
                    val userDocs = response.body()
                    if (userDocs?.data?.isNotEmpty() == true) {
                        val licenceData = userDocs.data[0].attributes.data.user.licenceDetails.data

                        val issued = licenceData.filter { !it.valid_upto.isNullOrBlank() }
                        val expired = licenceData.filter { it.valid_upto.isNullOrBlank() }

                        issuedDocs.clear()
                        expiredDocs.clear()
                        issuedDocs.addAll(issued)
                        expiredDocs.addAll(expired)

                        message.value = "Documents fetched ✅"
                    } else {
                        message.value = "❌ No document data found"
                    }
                } else {
                    message.value = "❌ Failed to fetch documents"
                }
            } catch (e: Exception) {
                message.value = "❌ Error fetching documents: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
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




    //--------------------------------Fetch User details----------------------------
    fun fetchDocumentsAfterForm(navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val parsedDate = inputFormat.parse(dateOfBirth.value)
                val formattedDob = outputFormat.format(parsedDate ?: Date())

                val request = UserInfoRequest(
                    username = name.value,
                    fathername = fatherName.value,
                    mothername = motherName.value,
                    gender = gender.value,
                    aadhar_verification_id = vaultKey.value,
                    dob = formattedDob
                )

                val jsonLog = """
                {
                    "username": "${request.username}",
                    "fathername": "${request.fathername}",
                    "mothername": "${request.mothername}",
                    "gender": "${request.gender}",
                    "aadhar_verification_id": "${request.aadhar_verification_id}",
                    "dob": "${request.dob}"
                }
                """.trimIndent()
                Log.d("DOC_FETCH_REQUEST", jsonLog)
                Log.d("AUTH_TOKEN", "Bearer ${userToken.value}")
                val response = repository.getUserDetails(userToken.value, request)


                if (response.isSuccessful) {
                    val userDocs = response.body()
                    if (userDocs?.data?.isNotEmpty() == true) {
                        val licenceData = userDocs.data[0].attributes.data.user.licenceDetails.data
                        issuedDocs.clear()
                        expiredDocs.clear()
                        issuedDocs.addAll(licenceData.filter { !it.valid_upto.isNullOrBlank() })
                        expiredDocs.addAll(licenceData.filter { it.valid_upto.isNullOrBlank() })
                        message.value = "Documents fetched ✅"
                        navController.navigate("documents_screen")
                    }
                } else {
                    Log.e("DOC_FETCH", "Failed: ${response.errorBody()?.string()}")
                    message.value = "Failed to fetch documents ❌"
                }
            } catch (e: Exception) {
                message.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }


}
