package com.example.elocker.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading = viewModel.isLoading.value

    val message = viewModel.message.value

    val nameError = remember { mutableStateOf<String?>(null) }
    val fatherNameError = remember { mutableStateOf<String?>(null) }
    val motherNameError = remember { mutableStateOf<String?>(null) }
    val aadhaarError = remember { mutableStateOf<String?>(null) }
    var dobError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, day)
            }.time
            val currentDate = Date()
            if (selectedDate.after(currentDate)) {
                dobError = "DOB must be less than today"
            } else {
                viewModel.onDateOfBirthChange("$day/${month + 1}/$year")
                dobError = null
            }
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
                    IconButton(onClick = { /* Handle back nav */ }) {
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
                value = viewModel.name.value,
                onValueChange = {
                    viewModel.onNameChange(it)
                    nameError.value = if (!Pattern.matches("^[A-Za-z\\s]+$", it)) "Name must be alphabets only" else null
                },
                isError = nameError.value != null,
                supportingText = { nameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                placeholder = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Father Name", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = viewModel.fatherName.value,
                onValueChange = {
                    viewModel.onFatherNameChange(it)
                    fatherNameError.value = if (!Pattern.matches("^[A-Za-z\\s]+$", it)) "Name must be alphabets only" else null
                },
                isError = fatherNameError.value != null,
                supportingText = { fatherNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                placeholder = { Text("Enter father Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Mother Name", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = viewModel.motherName.value,
                onValueChange = {
                    viewModel.onMotherNameChange(it)
                    motherNameError.value = if (!Pattern.matches("^[A-Za-z\\s]+$", it)) "Name must be alphabets only" else null
                },
                isError = motherNameError.value != null,
                supportingText = { motherNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                placeholder = { Text("Enter mother Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Date of Birth", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = viewModel.dateOfBirth.value,
                onValueChange = {},
                isError = dobError != null,
                supportingText = { dobError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
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
                expanded = viewModel.genderExpanded.value,
                onExpandedChange = { viewModel.onGenderExpandToggle() }
            ) {
                OutlinedTextField(
                    value = viewModel.gender.value,
                    onValueChange = {},
                    readOnly = true,
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

            Text("Aadhaar Number", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = viewModel.aadhaarNumber.value,
                onValueChange = {
                    viewModel.onAadhaarChange(it)
                    aadhaarError.value = if (!Pattern.matches("^[0-9]{12}$", it)) "Aadhaar must be 12 digits" else null
                },
                isError = aadhaarError.value != null,
                supportingText = { aadhaarError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
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
                onClick = {
                    if (nameError.value == null && fatherNameError.value == null && aadhaarError.value == null && dobError == null)
                        viewModel.submitForm()
                    else
                        viewModel.showValidationError()
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                if (isLoading)
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                else
                    Text("Submit")
            }

        }
    }
}
