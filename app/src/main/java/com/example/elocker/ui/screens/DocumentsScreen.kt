package com.example.elocker.ui.screens
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.example.elocker.R
import com.example.elocker.data.remote.ViewDocumentRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.elocker.ui.screens.PdfViewScreen
import com.example.elocker.data.remote.LicenceDocument
import com.example.elocker.viewmodel.RegistrationViewModel
import com.example.elocker.data.remote.ViewDocumentData
@Composable
fun DocumentScreen(viewModel: RegistrationViewModel, navController: NavController) {
    val searchQuery = remember { mutableStateOf("") }
    val selectedTab = remember { mutableStateOf("Issued") }

    val issuedDocs = viewModel.issuedDocs
    val expiredDocs = viewModel.expiredDocs
    val docsToShow = if (selectedTab.value == "Issued") issuedDocs else expiredDocs

    val filteredDocs = docsToShow.filter {
        it.service_name.contains(searchQuery.value, ignoreCase = true)
    }
    LaunchedEffect(Unit) {
        Log.d("DOC_SCREEN", "Issued Count: ${viewModel.issuedDocs.size}")
        Log.d("DOC_SCREEN", "Expired Count: ${viewModel.expiredDocs.size}")
    }

    val base64 = viewModel.base64Pdf.value
    LaunchedEffect(base64) {
        base64?.let {
            navController.currentBackStackEntry?.savedStateHandle?.set("pdf_base64", it)
            navController.navigate("pdf_view_screen")
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Punjab e-Locker", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // Search
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search Documents") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // Tabs
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Issued", "Expired").forEach { tab ->
                Text(
                    tab,
                    fontWeight = if (selectedTab.value == tab) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab.value == tab) Color.Blue else Color.Gray,
                    modifier = Modifier.clickable { selectedTab.value = tab }
                )
            }
        }

        // Card List
        if (filteredDocs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No documents found.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)) {
                items(filteredDocs) { doc ->
                    DocumentCard(
                        serviceName = doc.service_name,
                        applicationId = doc.applicationID,
                        docSrNo = doc.doc_sr_no,
                        issuedOn = doc.issued_on,
                        isExpired = selectedTab.value == "Expired",
                        onViewClick = {
                            val request = ViewDocumentRequest(docSrNo = "ES${doc.doc_sr_no}")
                            viewModel.fetchBase64Pdf(request, navController)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentCard(
    serviceName: String,
    applicationId: String,
    docSrNo: String,
    issuedOn: String,
    isExpired: Boolean = false,
    onViewClick: () -> Unit
) {
    val backgroundColor = if (!isExpired) Color.White else Color(0xFFF0F0F0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer { this.alpha = alpha },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Logo
            Image(
                painter = painterResource(id = R.drawable.ic_certi),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )

            Spacer(Modifier.width(12.dp))

            // Info
            Column(Modifier.weight(1f)) {
                Text(serviceName, fontWeight = FontWeight.Bold)
                Text("App ID: $applicationId", fontSize = 12.sp)
                Text("Doc SR: $docSrNo", fontSize = 12.sp)
                Text("Issued On: $issuedOn", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "View",
                    color = Color(0xFF1E88E5),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onViewClick() }

                )
            }

            Spacer(Modifier.width(12.dp))

            // Right Logo
            Image(
                painter = painterResource(id = R.drawable.ic_ashoka),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
