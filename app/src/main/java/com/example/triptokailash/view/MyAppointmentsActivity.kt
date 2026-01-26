package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triptokailash.model.AppointmentModel
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.viewmodel.AppointmentViewModel

class MyAppointmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripToKailashTheme {
                MyAppointmentsScreen { finish() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(onBack: () -> Unit) {
    val viewModel: AppointmentViewModel = viewModel()
    val appointments by viewModel.appointments.observeAsState(emptyList())
    val isLoading by viewModel.loading.observeAsState(false)
    var showDialog by remember { mutableStateOf(false) }
    var selectedAppointmentId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Load appointments whenever the screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadAppointments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Appointments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (appointments.isEmpty()) {
                Text(
                    text = "You have no appointments.",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(appointments) { appointment ->
                        AppointmentCard(appointment = appointment, onCancel = {
                            selectedAppointmentId = appointment.appointmentId
                            showDialog = true
                        }, onEdit = {
                            val intent = Intent(context, AppointmentFormActivity::class.java).apply {
                                putExtra("APPOINTMENT_ID", appointment.appointmentId)
                                putExtra("PACKAGE_TITLE", appointment.packageName)
                                putExtra("PACKAGE_PRICE", appointment.price.toString())
                                putExtra("NUMBER_OF_PEOPLE", appointment.numberOfPeople.toString())
                            }
                            context.startActivity(intent)
                        })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel Appointment", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Are you sure you want to cancel this appointment?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        selectedAppointmentId?.let {
                            viewModel.cancelAppointment(it) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    viewModel.loadAppointments() // Refresh the list
                                }
                            }
                        }
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentModel, onCancel: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = appointment.packageName ?: "Package Name",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppointmentInfoRow(label = "Date:", value = appointment.appointmentDate ?: "N/A")
            AppointmentInfoRow(label = "Time:", value = appointment.appointmentTime ?: "N/A")
            AppointmentInfoRow(label = "Price:", value = "Nrs. ${appointment.price}")
            AppointmentInfoRow(label = "Status:", value = appointment.status ?: "Pending", statusColor = getStatusColor(appointment.status))

            if (appointment.status == "Pending" || appointment.status == "Confirmed") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentInfoRow(label: String, value: String, statusColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(100.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            color = statusColor,
            fontWeight = if (label == "Status:") FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun getStatusColor(status: String?): Color {
    return when (status) {
        "Confirmed" -> Color(0xFF27AE60) // Green
        "Pending" -> Color(0xFFE67E22)   // Orange
        "Cancelled" -> Color(0xFFC0392B) // Red
        else -> Color.Gray
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppointmentsScreenPreview() {
    TripToKailashTheme {
        MyAppointmentsScreen { }
    }
}
