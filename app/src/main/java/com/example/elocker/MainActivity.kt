package com.example.elocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elocker.ui.screens.RegistrationScreen
import com.example.elocker.ui.theme.ELockerTheme
import com.example.elocker.viewmodel.RegistrationViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.elocker.ui.screens.DocumentsScreen
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ELockerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "registration") {
                    composable("registration") {
                        val viewModel: RegistrationViewModel = hiltViewModel()

                        RegistrationScreen(
                            navController = navController,
                            viewModel = viewModel,
//                            onSubmitClick = {
//                                navController.navigate("documents_screen")
//                            }
                        )
                    }
                    composable("documents_screen") {
                        val viewModel: RegistrationViewModel = hiltViewModel()
                        DocumentsScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
