package com.example.elocker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elocker.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpPopupCard(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    onSubmitOtp: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onResendOtp: () -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    var currentFocusIndex by remember { mutableStateOf(0) }

    val totalTime = 5 * 60
    var timeLeft by remember { mutableStateOf(totalTime) }
    var timerRunning by remember { mutableStateOf(true) }
    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            timerRunning = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismissRequest)
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "OTP sent to your registered mobile number\nmapped with XXXX-XXXX-XXXX",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                if (timeLeft > 0) {
                    val minutes = timeLeft / 60
                    val seconds = timeLeft % 60
                    Text(
                        text = String.format("Time Left : %02d:%02d", minutes, seconds),
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    TextButton(onClick = {
                        onResendOtp()
                        timeLeft = totalTime
                        timerRunning = true
                    }) {
                        Text("Resend OTP", color = Color(0xFF0057FF))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(6) { index ->
                        OutlinedTextField(
                            value = otpValue.getOrNull(index)?.toString() ?: "",
                            onValueChange = { value ->
                                val newOtp = otpValue.padEnd(6, ' ').toCharArray()
                                if (value.isNotEmpty() && value[0].isDigit()) {
                                    newOtp[index] = value[0]
                                    onOtpValueChange(newOtp.concatToString().trim())
                                    if (index < 5) {
                                        currentFocusIndex = index + 1
                                        focusRequesters[currentFocusIndex].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp)
                                .weight(0.7f)
                                .focusRequester(focusRequesters[index])
                                .onPreviewKeyEvent { event ->
                                    if (event.key == Key.Backspace && event.type == KeyEventType.KeyDown) {
                                        val newOtp = otpValue.padEnd(6, ' ').toCharArray()
                                        if (index > 0) {
                                            val updatedOtp = otpValue.padEnd(6, ' ').toCharArray()
                                            updatedOtp[index] = ' '
                                            updatedOtp[index - 1] = ' '
                                            onOtpValueChange(updatedOtp.concatToString().trim())
                                            currentFocusIndex = index - 1
                                            focusRequesters[currentFocusIndex].requestFocus()
                                        } else {
                                            val updatedOtp = otpValue.padEnd(6, ' ').toCharArray()
                                            updatedOtp[index] = ' '
                                            onOtpValueChange(updatedOtp.concatToString().trim())
                                        }

                                        true
                                    } else false
                                },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                color = Color.Black,
                                lineHeight = 22.sp
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF0057FF),
                                unfocusedBorderColor = Color(0xFF0057FF),
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onSubmitOtp(otpValue) },
                    enabled = otpValue.length == 6 && otpValue.all { it.isDigit() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0057FF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Submit", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOtpPopupCard() {
    OtpPopupCard(
        otpValue = "34238",
        onOtpValueChange = {},
        onSubmitOtp = {},
        onDismissRequest = {},
        onResendOtp = {}
    )
}
