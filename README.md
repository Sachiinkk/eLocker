# Punjab e-Locker

A modern Android registration screen UI built with **Jetpack Compose**, using **MVVM architecture**, **Hilt** for dependency injection, and clean modular structure.

---

## ✨ Features

- Clean, pixel-perfect UI matching Punjab e-Locker design
- Fields for Name, Father Name, Mother Name, DOB, Gender, Aadhaar
- Material3 UI components
- 🖋️ User registration form
- 📅 Date picker for DOB
- ♂️ Gender dropdown
- 🆔 Aadhaar authentication placeholder
- 📡 Mock API submission via Retrofit
- 💉 Dependency Injection using Hilt
- 🎨 Built with Jetpack Compose and Material 3
---

## 📁 Project Structure

app/
├── manifests/
│   └── AndroidManifest.xml
├── kotlin+java/
│   └── com.example.elocker/
│       ├── data/
│       │   └── remote/
│       │       ├── ApiService.kt
│       │       └── RetrofitInstance.kt
│       ├── model/
│       │   └── User.kt
│       ├── repository/
│       │   └── UserRepository.kt
│       ├── ui/
│       │   ├── screens/
│       │   |   ├── RegistrationScreen.kt
|       |   |   ├── DocumentScreen.kt
|       |   |   ├── OtpPopupCard.kt
|       |   |   └── SuccessDialog.kt
│       |   └── theme/
|       |      ├── Color.kt
|       |      ├── Theam.kt
|       |      └── Type.kt
|       ├── utils/
|       |      ├── Constants.kt
|       |      └──EncryptionUtils.kt
│       ├── viewmodel/
│       │   └── RegistrationViewModel.kt
│       ├── App.kt
│       └── MainActivity.kt
├── com.example.elocker (androidTest)/
├── com.example.elocker (test)/
└── java/ (generated)

---

## 📦 Tech Stack

| Layer         | Tech Used               |
|---------------|--------------------------|
| UI            | Jetpack Compose          |
| State         | ViewModel + State Flows  |
| Networking    | Retrofit + Coroutines    |
| Security      | Aadhaar Encryption (BouncyCastle) |
| Architecture  | MVVM                     |

---

---

📦 Dependencies
Retrofit 2.9.0

Gson Converter

Hilt 2.50

Compose BOM 2024.04.01

Material Icons
## 🚀 Setup


# Aadhaar OTP Authentication - Jetpack Compose

This module provides a secure OTP-based Aadhaar authentication system built using **Jetpack Compose**, **Kotlin**, and **MVVM architecture**.


### 🔐 Aadhaar Encryption
- Frontend encrypts Aadhaar using `BouncyCastle`.
- Secure transmission to backend for OTP generation.

### 📩 OTP Flow
- Sends OTP via API using encrypted Aadhaar.
- Displays `OtpPopupCard` with:
  - 6-digit OTP entry.
  - Smooth input focus handling.
  - Countdown timer (5 min).
  - **Auto-disable and Resend OTP** after expiry.

### 🕒 Timer System
- 5-minute countdown on OTP screen.
- Timer disables resend until cooldown expires.
- Proper UI feedback using Snackbar/messages.

### 🔁 Resend OTP
- Fully working resend logic using the last encrypted Aadhaar.
- Handles edge cases like flooding and error responses.

### ✅ OTP Verification
- OTP is verified via backend with the stored `txn` ID.
- On success, shows success dialog and proceeds to next flow.

---

## 🧠 UX Improvements
- Prevented UI shake by:
  - Avoiding recomposition on input.
  - Adding fixed width to OTP input fields.
  - Introducing delay in focus jump.
- Snackbar-style messages for feedback.
- Responsive layout.

---



## 🛠 Next Steps
- Add biometric fallback for Aadhaar.
- UI test automation for OTP flow.
- Accessibility improvements.
- Retry limit logic.

---


#### 📤 Send OTP
- **Base URL:** `BASE_URL`
- **Endpoint:** `POST /sendOtp`
- **Description:** Sends OTP to the Aadhaar-linked mobile number.
- **Function Used:** `sendOtp()`

#### ✅ Verify OTP
- **Base URL:** `BASE_URL`
- **Endpoint:** `POST /verifyOtp`
- **Description:** Verifies the entered OTP with the backend.
- **Function Used:** `verifyOtp()`

---

### 📄 Fetching User Documentation

#### 📥 Get User Details
- **Base URL:** `BASE_URL_2`
- **Endpoint:** `POST /getUserDetails`
- **Description:** Retrieves user documentation and related metadata.
- **Function Used:** `getUserDetails()`
