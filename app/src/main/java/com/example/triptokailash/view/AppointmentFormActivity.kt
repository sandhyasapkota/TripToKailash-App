package com.example.triptokailash.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triptokailash.model.AppointmentModel
import com.example.triptokailash.ui.theme.*
import com.example.triptokailash.viewmodel.AppointmentViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AppointmentFormActivity : ComponentActivity() {
    private val viewModel: AppointmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val packageTitle = intent.getStringExtra("PACKAGE_TITLE") ?: "Kailash Yatra"
        val packagePrice = intent.getStringExtra("PACKAGE_PRICE")
        val appointmentId = intent.getStringExtra("APPOINTMENT_ID")
        val existingNumberOfPeople = intent.getStringExtra("NUMBER_OF_PEOPLE")

        setContent {
            TripToKailashTheme {
                AppointmentFormScreen(
                    packageTitle = packageTitle,
                    packagePrice = packagePrice,
                    appointmentId = appointmentId,
                    existingNumberOfPeople = existingNumberOfPeople,
                    viewModel = viewModel
                ) {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(
    packageTitle: String,
    packagePrice: String?,
    appointmentId: String? = null,
    existingNumberOfPeople: String? = null,
    viewModel: AppointmentViewModel,
    onBack: () -> Unit
) {
    val isEditMode = appointmentId != null
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var numberOfPeople by remember { mutableStateOf(existingNumberOfPeople ?: "1") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val calendar = Calendar.getInstance()

    // Load existing appointment data if editing
    val existingAppointment by viewModel.appointment.observeAsState()

    LaunchedEffect(appointmentId) {
        if (appointmentId != null) {
            viewModel.getAppointmentById(appointmentId)
        }
    }

    LaunchedEffect(existingAppointment) {
        existingAppointment?.let { appointment ->
            if (isEditMode) {
                selectedDate = appointment.appointmentDate ?: ""
                selectedTime = appointment.appointmentTime ?: ""
                numberOfPeople = appointment.numberOfPeople?.toString() ?: "1"
            }
        }
    }

    // Pre-fill user email from Firebase Auth
    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.let {
            email = it.email ?: ""
        }
    }

    // Calculate total price
    val totalPrice = remember(numberOfPeople, packagePrice) {
        val basePrice = packagePrice?.toDoubleOrNull() ?: 0.0
        val people = numberOfPeople.toIntOrNull() ?: 1
        basePrice * people
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (isEditMode) "Edit Booking" else "Book Trip",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            packageTitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
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
        bottomBar = {
            // Price summary and confirm button
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Price breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total (${numberOfPeople.toIntOrNull() ?: 1} person${if ((numberOfPeople.toIntOrNull() ?: 1) != 1) "s" else ""})",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "₹${totalPrice.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            when {
                                name.isBlank() && !isEditMode -> {
                                    Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                                }
                                email.isBlank() -> {
                                    Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                                }
                                selectedDate.isBlank() -> {
                                    Toast.makeText(context, "Please select a travel date", Toast.LENGTH_SHORT).show()
                                }
                                selectedTime.isBlank() -> {
                                    Toast.makeText(context, "Please select a time", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                                    if (userId == null) {
                                        Toast.makeText(context, "You must be logged in to book", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    isLoading = true

                                    if (isEditMode && appointmentId != null) {
                                        // Update existing appointment
                                        val updatedAppointment = AppointmentModel(
                                            appointmentId = appointmentId,
                                            userId = userId,
                                            packageName = packageTitle,
                                            appointmentDate = selectedDate,
                                            appointmentTime = selectedTime,
                                            numberOfPeople = numberOfPeople.toIntOrNull() ?: 1,
                                            price = totalPrice,
                                            status = existingAppointment?.status ?: "Pending"
                                        )

                                        viewModel.updateAppointment(updatedAppointment) { success, message ->
                                            isLoading = false
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                            if (success) {
                                                onBack()
                                            }
                                        }
                                    } else {
                                        // Create new appointment
                                        val appointment = AppointmentModel(
                                            userId = userId,
                                            packageName = packageTitle,
                                            appointmentDate = selectedDate,
                                            appointmentTime = selectedTime,
                                            numberOfPeople = numberOfPeople.toIntOrNull() ?: 1,
                                            price = totalPrice,
                                            status = "Pending"
                                        )

                                        viewModel.addAppointment(appointment) { success, message ->
                                            isLoading = false
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                            if (success) {
                                                val intent = Intent(context, HomeActivity::class.java)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                                context.startActivity(intent)
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                if (isEditMode) Icons.Default.Save else Icons.Default.CheckCircle,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (isEditMode) "Update Booking" else "Confirm Booking",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
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
            // Package Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(GradientStart, GradientEnd)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Landscape,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                packageTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "₹${packagePrice ?: "N/A"} per person",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Personal Details Section (only for new bookings)
            if (!isEditMode) {
                item {
                    SectionHeader(
                        icon = Icons.Outlined.Person,
                        title = "Personal Details"
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            EnhancedFormField(
                                icon = Icons.Outlined.Badge,
                                label = "Full Name",
                                value = name,
                                onValueChange = { name = it },
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                            )

                            Spacer(Modifier.height(12.dp))

                            EnhancedFormField(
                                icon = Icons.Outlined.Email,
                                label = "Email Address",
                                value = email,
                                onValueChange = { email = it },
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                            )

                            Spacer(Modifier.height(12.dp))

                            EnhancedFormField(
                                icon = Icons.Outlined.LocationOn,
                                label = "Address",
                                value = address,
                                onValueChange = { address = it },
                                imeAction = ImeAction.Done,
                                onImeAction = { focusManager.clearFocus() }
                            )
                        }
                    }
                }
            }

            // Trip Details Section
            item {
                SectionHeader(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Trip Details"
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Date Picker
                        DatePickerField(
                            label = "Travel Date",
                            value = selectedDate,
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        calendar.set(year, month, dayOfMonth)
                                        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                        selectedDate = sdf.format(calendar.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).apply {
                                    datePicker.minDate = System.currentTimeMillis()
                                }.show()
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Time Picker
                        TimePickerField(
                            label = "Preferred Time",
                            value = selectedTime,
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        calendar.set(Calendar.MINUTE, minute)
                                        selectedTime = sdf.format(calendar.time)
                                    },
                                    9, 0, false
                                ).show()
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Number of People
                        Text(
                            "Number of Travelers",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("numberOfPeopleLabel")
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    val current = numberOfPeople.toIntOrNull() ?: 1
                                    if (current > 1) numberOfPeople = (current - 1).toString()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                numberOfPeople,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .testTag("numberOfPeopleField")
                            )

                            IconButton(
                                onClick = {
                                    val current = numberOfPeople.toIntOrNull() ?: 1
                                    if (current < 20) numberOfPeople = (current + 1).toString()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Special Requests (only for new bookings)
            if (!isEditMode) {
                item {
                    SectionHeader(
                        icon = Icons.Outlined.Notes,
                        title = "Special Requests (Optional)"
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Any special requirements or notes...") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            // Spacer for bottom bar
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EnhancedFormField(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun DatePickerField(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("dateField"),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        readOnly = true,
        enabled = false,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun TimePickerField(label: String, value: String, onClick: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        label = { Text(label) },
        leadingIcon = {
            Icon(
                Icons.Default.Schedule,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        readOnly = true,
        enabled = false,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary
        )
    )
}

// Keep old composable for backward compatibility
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    val icon = when (label.lowercase()) {
        "name" -> Icons.Outlined.Badge
        "address" -> Icons.Outlined.LocationOn
        "contact email" -> Icons.Outlined.Email
        "number of people" -> Icons.Outlined.People
        else -> Icons.Outlined.Notes
    }
    if (singleLine) {
        EnhancedFormField(icon, label, value, onValueChange, keyboardType, imeAction, onImeAction)
    } else {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() }
            )
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppointmentFormScreenPreview() {
    TripToKailashTheme {
        AppointmentFormScreen(packageTitle = "Kailash Yatra", packagePrice = "150000", viewModel = AppointmentViewModel()) {}
    }
}
