package com.example.elocker.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elocker.data.remote.FormData
import com.example.elocker.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    val isLoading = mutableStateOf(false)
    val message = mutableStateOf<String?>(null)

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

    fun submitForm() {
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
                    message.value = "Submitted successfully ✅"
                    clearForm()
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

    fun showValidationError() {
        message.value = "Please fix the errors in the form."
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
