package com.example.elocker.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.elocker.viewmodel.RegistrationViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val name by viewModel.name.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()
    val motherName by viewModel.motherName.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val genderExpanded by viewModel.genderExpanded.collectAsState()
    val aadhaarNumber by viewModel.aadhaarNumber.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

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
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Name", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                placeholder = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Father Name", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = fatherName,
                onValueChange = viewModel::onFatherNameChange,
                placeholder = { Text("Enter father Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Mother Name", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = motherName,
                onValueChange = viewModel::onMotherNameChange,
                placeholder = { Text("Enter mother Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Date of Birth", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {},
                placeholder = { Text("Select Date") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Gender", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { viewModel.onGenderExpandToggle() }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select Gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
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

            Text("Aadhaar Number", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = aadhaarNumber,
                onValueChange = viewModel::onAadhaarChange,
                placeholder = { Text("Enter Aadhaar number") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "AUTHENTICATE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { /* Aadhaar Auth */ }
                )
            }

            Button(
                onClick = { viewModel.submitForm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                if (isLoading)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else
                    Text("Submit")
            }
        }
    }
}
