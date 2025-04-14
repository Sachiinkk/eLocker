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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elocker.ui.screens.APIResponses
import com.example.elocker.ui.screens.RegistrationScreen
import com.example.elocker.ui.theme.ELockerTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.elocker.viewmodel.RegistrationViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ELockerTheme {
                val navController = rememberNavController()
                val viewModel: RegistrationViewModel = hiltViewModel() // ✅ Add this

                NavHost(navController = navController, startDestination = "registration") {
                    addRegistration(navController) // ✅ Already passes navController

                    composable("api_response") {
                        APIResponses(
                            jsonResponse = viewModel.apiResponse.value ?: "",
                            navController = navController // ✅ Pass navController too
                        )
                    }
                }
            }
        }

    }
}

private fun NavGraphBuilder.addRegistration(navController: NavHostController) {
    composable(route = "registration") {
        RegistrationScreen(
            navController = navController,
            onSubmitClick = {

                navController.navigateToAPI()
            }
        )
    }
}

fun NavController.navigateToAPI() {
    this.navigate("api_response")
}
