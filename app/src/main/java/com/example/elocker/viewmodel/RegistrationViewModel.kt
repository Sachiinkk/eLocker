package com.example.elocker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elocker.data.remote.FormData
import com.example.elocker.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    val name = MutableStateFlow("")
    val fatherName = MutableStateFlow("")
    val motherName = MutableStateFlow("")
    val dateOfBirth = MutableStateFlow("")
    val gender = MutableStateFlow("")
    val aadhaarNumber = MutableStateFlow("")
    val genderExpanded = MutableStateFlow(false)

    val isLoading = MutableStateFlow(false)
    val message = MutableStateFlow<String?>(null)

    fun onNameChange(v: String) { name.value = v }
    fun onFatherNameChange(v: String) { fatherName.value = v }
    fun onMotherNameChange(v: String) { motherName.value = v }
    fun onDateOfBirthChange(v: String) { dateOfBirth.value = v }
    fun onGenderSelect(v: String) {
        gender.value = v
        genderExpanded.value = false
    }
    fun onGenderExpandToggle() { genderExpanded.value = !genderExpanded.value }
    fun onAadhaarChange(v: String) { aadhaarNumber.value = v }

    fun submitForm() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val data = FormData(
                    name.value,
                    fatherName.value,
                    motherName.value,
                    dateOfBirth.value,
                    gender.value,
                    aadhaarNumber.value
                )
                val response = repository.submitForm(data)
                if (response.isSuccessful) {
                    message.value = "Submitted successfully ✅"
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


    fun clearMessage() {
        message.value = null
    }
}
