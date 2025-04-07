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
│       │   ├── components/
│       │   └── screens/
│       │       └── RegistrationScreen.kt
│       ├── theme/
│       ├── viewmodel/
│       │   └── RegistrationViewModel.kt
│       ├── App.kt
│       └── MainActivity.kt
├── com.example.elocker (androidTest)/
├── com.example.elocker (test)/
└── java/ (generated)

---

## 🛠️ Tech Stack

- Jetpack Compose
- Kotlin
- Retrofit2
- Hilt (DI)
- Material3
- MVVM + Clean Architecture

---

📦 Dependencies
Retrofit 2.9.0

Gson Converter

Hilt 2.50

Compose BOM 2024.04.01

Material Icons
## 🚀 Setup

1. Clone the repo
2. Add this to `AndroidManifest.xml`:

```xml
<application
    android:name=".App"
    ...>
