package com.example.elocker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.elocker.viewmodel.RegistrationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpScreen(
    navController: NavController,
    viewModel: RegistrationViewModel
) {
    var otp by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.value
    val message = viewModel.message.value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter OTP", fontSize = 22.sp, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("OTP", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.verifyOtp(otp) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Authentication successful ✅")
                            delay(1000)
                            navController.popBackStack()
                        }
                    }
                },
                enabled = otp.length == 6 && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                else
                    Text("Verify OTP")
            }

            TextButton(onClick = {
                viewModel.resendOtp()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("OTP resent")
                }
            }) {
                Text("Resend OTP", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
