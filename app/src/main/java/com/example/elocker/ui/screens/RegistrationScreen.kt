package com.example.elocker.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.elocker.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import androidx.compose.foundation.clickable
import androidx.compose.runtime.snapshotFlow
import java.util.*
import java.util.regex.Pattern
import androidx.compose.material3.ExperimentalMaterial3Api

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

    val dobInteractionSource = remember { MutableInteractionSource() }

    val datePicker = remember {
        DatePickerDialog(
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
    }

    LaunchedEffect(Unit) {
        snapshotFlow { dobInteractionSource.interactions }
            .filterIsInstance<FocusInteraction.Focus>()
            .collect {
                datePicker.show()
            }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    val inputColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Gray,
        unfocusedBorderColor = Color.Gray,
        disabledBorderColor = Color.Gray,
        errorBorderColor = Color.Gray
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
            OutlinedTextField(
                value = viewModel.name.value,
                onValueChange = {
                    viewModel.onNameChange(it)
                    nameError.value = when {
                        it.isBlank() -> "This field is required"
                        !Pattern.matches("^[A-Za-z\\s]+$", it) -> "Name must be alphabets only"
                        else -> null
                    }
                },
                label = { Text("Full Name") },
                isError = nameError.value != null,
                supportingText = {
                    if (nameError.value != null) Text(nameError.value!!, color = MaterialTheme.colorScheme.error)
                },
                colors = inputColors,
                placeholder = { Text("Enter Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.fatherName.value,
                onValueChange = {
                    viewModel.onFatherNameChange(it)
                    fatherNameError.value = when {
                        it.isBlank() -> "This field is required"
                        !Pattern.matches("^[A-Za-z\\s]+$", it) -> "Name must be alphabets only"
                        else -> null
                    }
                },
                label = { Text("Father Name") },
                isError = fatherNameError.value != null,
                supportingText = {
                    if (fatherNameError.value != null) Text(fatherNameError.value!!, color = MaterialTheme.colorScheme.error)
                },
                colors = inputColors,
                placeholder = { Text("Enter Father Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.motherName.value,
                onValueChange = {
                    viewModel.onMotherNameChange(it)
                    motherNameError.value = when {
                        it.isBlank() -> "This field is required"
                        !Pattern.matches("^[A-Za-z\\s]+$", it) -> "Name must be alphabets only"
                        else -> null
                    }
                },
                label = { Text("Mother Name") },
                isError = motherNameError.value != null,
                supportingText = {
                    if (motherNameError.value != null) Text(motherNameError.value!!, color = MaterialTheme.colorScheme.error)
                },
                colors = inputColors,
                placeholder = { Text("Enter Mother Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.dateOfBirth.value,
                onValueChange = {},
                interactionSource = dobInteractionSource,
                isError = dobError != null,
                supportingText = {
                    if (dobError != null) Text(dobError!!, color = MaterialTheme.colorScheme.error)
                },
                placeholder = { Text("Select Date") },
                label = { Text("Date of Birth") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                },
                readOnly = true,
                colors = inputColors,
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
                    placeholder = { Text("Select Gender") },
                    label = { Text("Gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(viewModel.genderExpanded.value)
                    },
                    colors = inputColors,
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

            OutlinedTextField(
                value = viewModel.aadhaarNumber.value,
                onValueChange = {
                    viewModel.onAadhaarChange(it)
                    aadhaarError.value = when {
                        it.isBlank() -> "This field is required"
                        !Pattern.matches("^[0-9]{12}$", it) -> "Aadhaar must be 12 digits"
                        else -> null
                    }
                },
                label = { Text("Aadhaar Number") },
                isError = aadhaarError.value != null,
                supportingText = {
                    if (aadhaarError.value != null) Text(aadhaarError.value!!, color = MaterialTheme.colorScheme.error)
                },
                colors = inputColors,
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

            val allValid = nameError.value == null &&
                    fatherNameError.value == null &&
                    motherNameError.value == null &&
                    aadhaarError.value == null &&
                    dobError == null &&
                    viewModel.name.value.isNotBlank() &&
                    viewModel.fatherName.value.isNotBlank() &&
                    viewModel.motherName.value.isNotBlank() &&
                    viewModel.aadhaarNumber.value.isNotBlank() &&
                    viewModel.dateOfBirth.value.isNotBlank() &&
                    viewModel.gender.value.isNotBlank()

            Button(
                onClick = {
                    if (allValid)
                        viewModel.submitForm()
                    else
                        viewModel.showValidationError()
                },
                enabled = allValid && !isLoading,
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

