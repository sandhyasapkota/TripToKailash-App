package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.triptokailash.R
import com.example.triptokailash.model.PackageModel
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.viewmodel.PackageViewModel

class PackageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        setContent {
            TripToKailashTheme {
                PackagesScreen(viewModel = viewModel(), isAdmin = isAdmin)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(viewModel: PackageViewModel, isAdmin: Boolean) {
    val context = LocalContext.current
    val packages by viewModel.allPackages.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripToKailash", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = {
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }) { Text("Home", color = MaterialTheme.colorScheme.onPrimary) }
                    TextButton(onClick = {
                        context.startActivity(Intent(context, ProfileActivity::class.java))
                    }) { Text("Profile", color = MaterialTheme.colorScheme.onPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 8.dp)
                ) {
                    Text(
                        text = "Our Pilgrimage Packages",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Embark on a journey of a lifetime.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isLoading && packages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (packages.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize().padding(50.dp), contentAlignment = Alignment.Center) {
                        Text("No packages available at the moment. Check back soon!", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(packages, key = { it.packageId ?: "" }) { pkg ->
                    PackageCard(packageInfo = pkg, viewModel = viewModel, isAdmin = isAdmin)
                }
            }
        }
    }
}

@Composable
fun PackageCard(packageInfo: PackageModel, viewModel: PackageViewModel, isAdmin: Boolean) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            AsyncImage(
                model = packageInfo.imageUrl,
                contentDescription = packageInfo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.logoo) // Fallback image
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = packageInfo.title ?: "No Title",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = packageInfo.duration ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Nrs. ${packageInfo.price}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = {
                            val intent = Intent(context, ViewPackages::class.java)
                            intent.putExtra("PACKAGE_ID", packageInfo.packageId)
                            intent.putExtra("PACKAGE_TITLE", packageInfo.title ?: "")
                            intent.putExtra("PACKAGE_DURATION", packageInfo.duration ?: "")
                            intent.putExtra("PACKAGE_PRICE", packageInfo.price?.toString() ?: "")
                            intent.putExtra("PACKAGE_IMAGE", R.drawable.logoo) // Or use a mapping if you have image resource
                            intent.putExtra("PACKAGE_DESCRIPTION", packageInfo.description ?: "")
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("View Details")
                    }
                }

                if (isAdmin) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Admin Controls:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(context, AddPackageActivity::class.java)
                                intent.putExtra("PACKAGE_ID_TO_EDIT", packageInfo.packageId)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Edit")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                packageInfo.packageId?.let { id ->
                                    viewModel.deletePackage(id) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PackagesScreenPreview() {
    TripToKailashTheme {
        // Preview requires a ViewModel instance, but it's okay to leave it empty for visual layout.
        // PackagesScreen(viewModel = viewModel(), isAdmin = true)
    }
}
