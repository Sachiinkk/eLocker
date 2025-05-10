package com.example.elocker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elocker.data.remote.LicenceDocument
import com.example.elocker.R
import com.example.elocker.viewmodel.RegistrationViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun DocumentTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFF7FF))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF003B73),
            modifier = Modifier
                .padding(start = 16.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Punjab e-Locker",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholderText: String = "Search Documents"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color(0xFF003B73)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = placeholderText,
            color = Color.Gray
        )
    }
}

@Composable
fun DocumentsScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val token = viewModel.userToken.value
    val dob = viewModel.dateOfBirth.value
    val name = viewModel.name.value
    val father = viewModel.fatherName.value
    val mother = viewModel.motherName.value
    val gender = viewModel.gender.value
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Issued", "Expired")
    var hasFetched by remember { mutableStateOf(false) }
    val issuedDocs = viewModel.issuedDocs
    val expiredDocs = viewModel.expiredDocs
    LaunchedEffect(token, dob, name, father, mother, gender) {
        if (
            token.isNotBlank() && dob.isNotBlank() &&
            name.isNotBlank() && father.isNotBlank() &&
            mother.isNotBlank() && gender.isNotBlank() && !hasFetched
        ) {
            hasFetched = true
            viewModel.fetchDocumentsAfterForm(navController)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF))
    ) {
        DocumentTopBar()
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFF003B73)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color(0xFF003B73) else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val documents = if (selectedTabIndex == 0) issuedDocs else expiredDocs

        if (documents.isEmpty()) {
            Text("No documents found", fontSize = 16.sp, modifier = Modifier.padding(16.dp))
        } else {
            documents.forEach { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = doc.service_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Ministry of Health & Family Welfare", fontSize = 12.sp, color = Color.Gray)
                            Text(text = "Issued On  ${doc.issued_on}", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "View",
                                color = Color(0xFF003B73),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.output_path))
                                    context.startActivity(intent)
                                }
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_ashoka),
                            contentDescription = "Seal",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}
