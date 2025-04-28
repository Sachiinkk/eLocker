package com.example.elocker.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.text.font.FontWeight
import com.example.elocker.ui.screens.OtpPopupCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.elocker.utils.PUBLIC_KEY_FROM_SERVER
import com.example.elocker.utils.encryptAadhaar
import com.example.elocker.viewmodel.RegistrationViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import androidx.compose.runtime.snapshotFlow
import java.util.*
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = hiltViewModel(),
    onSubmitClick: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val snackbarHostState = remember { SnackbarHostState() }

    val isLoading = viewModel.isLoading.value
    val message = viewModel.message.value
    val isAuthenticated by viewModel.isAadhaarAuthenticated.collectAsStateWithLifecycle()

    val nameError = remember { mutableStateOf<String?>(null) }
    val fatherNameError = remember { mutableStateOf<String?>(null) }
    val motherNameError = remember { mutableStateOf<String?>(null) }
    val aadhaarError = remember { mutableStateOf<String?>(null) }
    var dobError by remember { mutableStateOf<String?>(null) }
    var showOtpCard by remember { mutableStateOf(false) }
    var otpValue by remember { mutableStateOf("") }

    val inputColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = Color.Gray,
        unfocusedBorderColor = Color.Gray,
        errorBorderColor = Color.Red
    )

    val allValid by remember {
        derivedStateOf {
            nameError.value == null &&
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
        }
    }

    val buttonEnabled by remember {
        derivedStateOf { allValid && isAuthenticated && !isLoading }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Punjab e-Locker") },
                navigationIcon = {
                    IconButton(onClick = { /* your back logic */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Full Name
                OutlinedTextField(
                    value = viewModel.name.value,
                    onValueChange = {
                        viewModel.onNameChange(it)
                        nameError.value = if (it.isBlank()) "This field is required"
                        else if (!Pattern.matches("^[A-Za-z\\s]+$", it)) "Name must be alphabets only" else null
                    },
                    label = { Text("Full Name") },
                    placeholder = { Text("Enter Full Name") },
                    isError = nameError.value != null,
                    supportingText = { nameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                // Father's Name
                OutlinedTextField(
                    value = viewModel.fatherName.value,
                    onValueChange = {
                        viewModel.onFatherNameChange(it)
                        fatherNameError.value = if (it.isBlank()) "This field is required"
                        else if (!Pattern.matches("^[A-Za-z\\s]+$", it)) "Name must be alphabets only" else null
                    },
                    label = { Text("Father's Name") },
                    placeholder = { Text("Enter Father's Name") },
                    isError = fatherNameError.value != null,
                    supportingText = { fatherNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                // Mother's Name
                OutlinedTextField(
                    value = viewModel.motherName.value,
                    onValueChange = {
                        viewModel.onMotherNameChange(it)
                        motherNameError.value = if (it.isBlank()) "This field is required"
                        else if (!Pattern.matches("^[A-Za-z\\s]+$", it)) "Name must be alphabets only" else null
                    },
                    label = { Text("Mother's Name") },
                    placeholder = { Text("Enter Mother's Name") },
                    isError = motherNameError.value != null,
                    supportingText = { motherNameError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth()
                )

                // Date of Birth
                val calendar = Calendar.getInstance()
                var showPicker by remember { mutableStateOf(false) }
                if (showPicker) {
                    DatePickerDialog(
                        context,
                        { _: DatePicker, year: Int, month: Int, day: Int ->
                            val selectedDate = Calendar.getInstance().apply { set(year, month, day) }.time
                            val today = Date()
                            if (selectedDate.after(today)) {
                                dobError = "DOB must be less than today"
                            } else {
                                viewModel.onDateOfBirthChange("$day/${month + 1}/$year")
                                dobError = null
                            }
                            showPicker = false
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }

                OutlinedTextField(
                    value = viewModel.dateOfBirth.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date of Birth") },
                    placeholder = { Text("Select DOB (DD/MM/YYYY)") },
                    trailingIcon = {
                        IconButton(onClick = { showPicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPicker = true },
                    isError = dobError != null,
                    supportingText = { dobError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = inputColors
                )

                // Gender Dropdown
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(viewModel.genderExpanded.value) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = inputColors
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

                // Aadhaar Number
                OutlinedTextField(
                    value = viewModel.aadhaarNumber.value,
                    onValueChange = {
                        viewModel.onAadhaarChange(it)
                        aadhaarError.value = if (it.isBlank()) "This field is required"
                        else if (!Pattern.matches("^[0-9]{12}$", it)) "Aadhaar must be 12 digits" else null
                    },
                    label = { Text("Aadhaar Number") },
                    placeholder = { Text("Enter Aadhaar Number") },
                    isError = aadhaarError.value != null,
                    supportingText = { aadhaarError.value?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = inputColors,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Authenticate Text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "AUTHENTICATE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            val aadhaarValid = Pattern.matches("^[0-9]{12}$", viewModel.aadhaarNumber.value)
                            coroutineScope.launch {
                                if (!aadhaarValid) {
                                    snackbarHostState.showSnackbar("Please enter correct Aadhaar number")
                                } else {
                                    val publicKey = PUBLIC_KEY_FROM_SERVER
                                    val encryptedAadhaar = encryptAadhaar(viewModel.aadhaarNumber.value.trim(), publicKey)
                                    viewModel.authenticateAadhaar(
                                        encryptedAadhaar,
                                        onOtpSent = { showOtpCard = true }
                                    )
                                }
                            }
                        }
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        viewModel.submitForm {
                            onSubmitClick()
                        }
                    },
                    enabled = buttonEnabled,
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

            // OTP CARD (popup panel above form)
            if (showOtpCard) {
                OtpPopupCard(
                    otpValue = otpValue,
                    onOtpValueChange = { otpValue = it },
                    onSubmitOtp = { enteredOtp ->
                        viewModel.verifyOtp(enteredOtp) {
                            showOtpCard = false
                            otpValue = ""
                        }
                    },
                    onDismissRequest = { showOtpCard = false }
                )
            }
        }
    }
}
