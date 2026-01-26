package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triptokailash.model.AppointmentModel
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.viewmodel.AppointmentViewModel
import com.google.firebase.auth.FirebaseAuth

class AppointmentFormActivity : ComponentActivity() {
    private val viewModel: AppointmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val packageTitle = intent.getStringExtra("PACKAGE_TITLE") ?: "Kailash Yatra"
        val packagePrice = intent.getStringExtra("PACKAGE_PRICE")

        setContent {
            TripToKailashTheme {
                AppointmentFormScreen(packageTitle = packageTitle, packagePrice = packagePrice, viewModel = viewModel) {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(packageTitle: String, packagePrice: String?, viewModel: AppointmentViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var numberOfPeople by remember { mutableStateOf("1") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TripToKailash", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Trip To Kailash",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Confirmation",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { FormField(label = "Name", value = name, onValueChange = { name = it }, imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { FormField(label = "Address", value = address, onValueChange = { address = it }, imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { FormField(label = "Contact Email", value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email, imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { FormField(label = "Number of People", value = numberOfPeople, onValueChange = { numberOfPeople = it }, keyboardType = KeyboardType.Number, imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { FormField(label = "Description", value = description, onValueChange = { description = it }, singleLine = false, minLines = 4, imeAction = ImeAction.Done, onImeAction = { focusManager.clearFocus() }) }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (name.isBlank() || address.isBlank() || email.isBlank()) {
                            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        } else {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId == null) {
                                Toast.makeText(context, "You must be logged in to book an appointment.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val appointment = AppointmentModel(
                                userId = userId,
                                packageName = packageTitle,
                                appointmentDate = "2024-10-26", // Placeholder, you can add a date picker
                                appointmentTime = "10:00 AM", // Placeholder, you can add a time picker
                                numberOfPeople = numberOfPeople.toIntOrNull() ?: 1,
                                price = packagePrice?.toDoubleOrNull(),
                                status = "Pending"
                            )

                            viewModel.addAppointment(appointment) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    val intent = Intent(context, HomeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Confirm", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

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
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
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
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
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
