# Punjab e-Locker

A modern Android registration screen UI built with **Jetpack Compose**, using **MVVM architecture**, **Hilt** for dependency injection, and clean modular structure.

---

## ✨ Features

- Clean, pixel-perfect UI matching Punjab e-Locker design
- Fields for Name, Father Name, Mother Name, DOB, Gender, Aadhaar
- Material3 UI components
- DatePicker and ExposedDropdownMenu
- Form state managed using ViewModel and `mutableStateOf`
- Hilt DI with ViewModel injection
- Scalable folder structure (ui, viewmodel, model, repository)

---

## 📁 Project Structure

app/ ├── ui/ │ ├── screens/ │ │ └── RegistrationScreen.kt │ └── components/ ├── viewmodel/ │ └── RegistrationViewModel.kt ├── model/ │ └── User.kt ├── repository/ │ └── UserRepository.kt └── MainActivity.kt

---

## 🛠️ Tech Stack

- Jetpack Compose
- Material3
- Hilt (DI)
- Kotlin
- MVVM Architecture

---

## 🚀 Setup

1. Clone the repo
2. Add this to `AndroidManifest.xml`:

```xml
<application
    android:name=".App"
    ...>
