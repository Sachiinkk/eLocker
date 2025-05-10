
package com.example.elocker.model

data class User(
    val name: String,
    val fatherName: String,
    val motherName: String,
    val dateOfBirth: String,
    val gender: String,
    val aadhaarNumber: String
)
data class LicenceDocument(
    val applicationID: String,
    val outputPath: String,
    val issuedOn: String,
    val validUpto: String?,
    val serviceName: String
)
