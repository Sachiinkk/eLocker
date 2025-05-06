
package com.example.elocker.model

data class User(
    val name: String,
    val fatherName: String,
    val motherName: String,
    val dateOfBirth: String,
    val gender: String,
    val aadhaarNumber: String
)
data class UserDetails(
    val username: String,
    val fathername: String,
    val mothername: String,
    val gender: String,
    val aadhar_verification_id: String,
    val dob: String
)
