package com.example.elocker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.LocalTextStyle  
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elocker.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpPopupCard(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    onSubmitOtp: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "OTP sent to your registered mobile number\nmapped with XXXX-XXXX-XXXX",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 18.sp,
//                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(6) { index ->
                        OutlinedTextField(
                            value = otpValue.getOrNull(index)?.toString() ?: "",
                            onValueChange = { value ->
                                if (value.length <= 1 && value.all { it.isDigit() }) {
                                    val newOtp = otpValue.padEnd(6, ' ').toCharArray()
                                    newOtp[index] = value.getOrNull(0) ?: ' '
                                    onOtpValueChange(newOtp.concatToString().trim())

                                    if (value.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else if (value.isEmpty() && index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .focusRequester(focusRequesters[index]),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = Color.Black
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFF0057FF),
                                unfocusedBorderColor = Color(0xFF0057FF)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onSubmitOtp(otpValue) },
                    enabled = otpValue.length == 6 && !otpValue.contains(' '),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0057FF),           // Always blue
                        disabledContainerColor = Color(0xFF0057FF),    // Also blue when disabled
                        contentColor = Color.White,                    // White text
                        disabledContentColor = Color.White             // White text even when disabled
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Submit", fontSize = 16.sp)
                }
            }
        }
    }
}
