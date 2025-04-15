package com.example.elocker.ui.screens
import android.util.Log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.json.JSONObject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun APIResponses(jsonResponse: String, navController: NavController) {
    val parsedData = remember(jsonResponse) {
        try {
            Log.d("API_Response", jsonResponse) // Debug print
            val json = JSONObject(jsonResponse)

            if (json.has("data")) {
                val dataArray = json.getJSONArray("data")
                List(dataArray.length()) { i ->
                    val obj = dataArray.getJSONObject(i)
                    "ID: ${obj.optInt("id")}, Name: ${obj.optString("name")}, Year: ${obj.optInt("year")}, Color: ${obj.optString("color")}"
                }
            } else {
                listOf("No 'data' key found in response")
            }
        } catch (e: Exception) {
            listOf("Failed to parse response: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Response Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(parsedData) {
                Text(it, color = Color.White)
            }
        }
    }
}
