
package com.example.elocker.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {
    var name = mutableStateOf("")
    var fatherName = mutableStateOf("")
    var motherName = mutableStateOf("")
    var dateOfBirth = mutableStateOf("")
    var gender = mutableStateOf("")
    var aadhaarNumber = mutableStateOf("")
    var genderExpanded = mutableStateOf(false)

    fun onNameChange(new: String) { name.value = new }
    fun onFatherNameChange(new: String) { fatherName.value = new }
    fun onMotherNameChange(new: String) { motherName.value = new }
    fun onDateOfBirthChange(new: String) { dateOfBirth.value = new }
    fun onAadhaarChange(new: String) { aadhaarNumber.value = new }
    fun onGenderExpandToggle() { genderExpanded.value = !genderExpanded.value }
    fun onGenderSelect(new: String) {
        gender.value = new
        genderExpanded.value = false
    }
}
