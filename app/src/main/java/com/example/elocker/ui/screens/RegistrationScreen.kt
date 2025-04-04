package com.example.elocker.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.elocker.viewmodel.RegistrationViewModel
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            viewModel.onDateOfBirthChange("$day/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Punjab e-Locker") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name") },
                placeholder = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.fatherName.value,
                onValueChange = viewModel::onFatherNameChange,
                label = { Text("Father name") },
                placeholder = { Text("Enter father Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.motherName.value,
                onValueChange = viewModel::onMotherNameChange,
                label = { Text("Mother name") },
                placeholder = { Text("Enter mother Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.dateOfBirth.value,
                onValueChange = {},
                label = { Text("Date of Birth") },
                placeholder = { Text("Select Date") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = viewModel.genderExpanded.value,
                onExpandedChange = { viewModel.onGenderExpandToggle() }
            ) {
                OutlinedTextField(
                    value = viewModel.gender.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    placeholder = { Text("Select Gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(viewModel.genderExpanded.value)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = viewModel.genderExpanded.value,
                    onDismissRequest = { viewModel.genderExpanded.value = false }
                ) {
                    listOf("Male", "Female", "Other").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { viewModel.onGenderSelect(it) }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = viewModel.aadhaarNumber.value,
                    onValueChange = viewModel::onAadhaarChange,
                    label = { Text("Aadhaar Number") },
                    placeholder = { Text("Enter Aadhaar number") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = { /* TODO: Authenticate */ }) {
                    Text("AUTHENTICATE")
                }
            }

            Button(
                onClick = { /* TODO: Submit */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Submit")
            }
        }
    }
}