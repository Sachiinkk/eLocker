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
import com.example.elocker.ui.screens.PdfViewScreen
import com.example.elocker.ui.screens.DocumentScreen
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ELockerTheme {
                val navController = rememberNavController()

                val viewModel: RegistrationViewModel = hiltViewModel()
                NavHost(navController = navController, startDestination = "registration") {
                    composable("registration") {
                        RegistrationScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable("documents_screen") {
                        DocumentScreen(navController = navController, viewModel = viewModel)
                    }
                    composable("pdf_view_screen") {
                        val base64 = navController.previousBackStackEntry
                            ?.savedStateHandle?.get<String>("pdf_base64")
                        base64?.let {
                            PdfViewScreen(base64Pdf = it)
                        }
                    }
                }
            }
        }
    }
}
