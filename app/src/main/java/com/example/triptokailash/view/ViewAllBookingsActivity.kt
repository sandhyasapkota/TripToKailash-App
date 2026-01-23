package com.example.triptokailash.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triptokailash.model.AppointmentModel
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.viewmodel.AppointmentViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.triptokailash.viewmodel.UserViewModel
class ViewAllBookingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripToKailashTheme {
                ViewAllBookingsScreen { finish() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllBookingsScreen(onBack: () -> Unit) {
    val viewModel: AppointmentViewModel = viewModel()
    val allAppointments by viewModel.allAppointments.observeAsState(emptyList())
    val isLoading by viewModel.loading.observeAsState(false)

    // This is the correct way to fetch data once.
    LaunchedEffect(Unit) {
        viewModel.loadAllAppointments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All User Bookings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (isLoading && allAppointments.isNullOrEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (allAppointments.isNullOrEmpty()) {
                Text(
                    "No bookings have been made yet.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allAppointments!!, key = { it.appointmentId ?: "" }) { appointment ->
                        AdminBookingCard(appointment = appointment)
                    }
                }
            }
        }
    }
}


@Composable
fun AdminBookingCard(appointment: AppointmentModel) {
    val userViewModel: UserViewModel = viewModel()
    var userName by remember { mutableStateOf<String?>(null) }
    var userEmail by remember { mutableStateOf<String?>(null) }

    // Fetch user details if not already loaded
    LaunchedEffect(appointment.userId) {
        appointment.userId?.let { userId ->
            userViewModel.getUserById(userId)
        }
    }
    val user by userViewModel.user.observeAsState()
    LaunchedEffect(user) {
        userName = user?.userName
        userEmail = user?.userEmail
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                appointment.packageName ?: "Package",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            BookingInfoRow("User:", userName ?: appointment.userId ?: "N/A")
            if (userEmail != null) BookingInfoRow("Email:", userEmail!!)
            BookingInfoRow("Date:", "${appointment.appointmentDate} at ${appointment.appointmentTime}")
            BookingInfoRow("Status:", appointment.status ?: "Pending")
        }
    }
}

@Composable
fun BookingInfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, color = MaterialTheme.colorScheme.onSurface)
    }
}
