//package com.example.elocker
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import com.example.elocker.ui.theme.ELockerTheme
//import com.example.elocker.ui.screens.RegistrationScreen
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            ELockerTheme {
//                RegistrationScreen()
//            }
//        }
//    }
//
//}
//

package com.example.elocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elocker.ui.screens.APIResponses
import com.example.elocker.ui.screens.RegistrationScreen
import com.example.elocker.ui.theme.ELockerTheme
import com.example.elocker.viewmodel.RegistrationViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ELockerTheme {
                val navController = rememberNavController()
                val viewModel: RegistrationViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = "registration") {
                    // ✅ Add registration screen here directly
                    composable("registration") {
                        RegistrationScreen(
                            navController = navController,
                            onSubmitClick = {
                                navController.navigate("api_response")
                            }
                        )
                    }

                    composable("api_response") {
                        APIResponses(
                            jsonResponse = viewModel.apiResponse.value,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
