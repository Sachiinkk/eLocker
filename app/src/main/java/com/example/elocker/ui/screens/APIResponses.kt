package com.example.elocker.ui.screens
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
            val json = JSONObject(jsonResponse)
            val dataArray = json.getJSONArray("data")
            val list = mutableListOf<Map<String, String>>()
            for (i in 0 until dataArray.length()) {
                val obj = dataArray.getJSONObject(i)
                list.add(
                    mapOf(
                        "id" to obj.getInt("id").toString(),
                        "name" to obj.getString("name"),
                        "year" to obj.getInt("year").toString(),
                        "color" to obj.getString("color"),
                        "pantone" to obj.getString("pantone_value")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
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
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(parsedData) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(android.graphics.Color.parseColor(item["color"] ?: "#FFFFFF")))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ID: ${item["id"]}")
                        Text("Name: ${item["name"]}")
                        Text("Year: ${item["year"]}")
                        Text("Color: ${item["color"]}")
                        Text("Pantone: ${item["pantone"]}")
                    }
                }
            }
        }
    }
}
