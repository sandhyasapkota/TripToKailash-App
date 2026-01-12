package com.example.triptokailash.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.google.firebase.auth.FirebaseAuth

class ForgetPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripToKailashTheme {
                ForgetPasswordScreen()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ForgetPasswordScreen() {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reset Your Password",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter your email and we'll send you a link to get back into your account.",
                style = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Please enter your email address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }


                    isLoading = true

                    // --- Use Firebase to send the password reset email ---
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            // Stop loading regardless of the result
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Password reset link sent to your email.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to send reset email. Please check the address and try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(15.dp),

                enabled = !isLoading
            ) {
                // Show a loading indicator or the text
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Send Reset Link", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            // Add Go to Login button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Navigate to Login screen
                    val intent = android.content.Intent(context, Login::class.java)
                    context.startActivity(intent)
                    if (context is android.app.Activity) {
                        context.finish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("Go Back to Login", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        // Top-left back button overlay
        // Box(modifier = Modifier.fillMaxSize()) {
        //     IconButton(
        //         onClick = {
        //             if (context is android.app.Activity) {
        //                 context.finish()
        //             }
        //         },
        //         modifier = Modifier
        //             .align(Alignment.TopStart)
        //             .padding(16.dp)
        //     ) {
        //         Icon(
        //             imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
        //             contentDescription = "Back",
        //             tint = Color(0xFF1C3A6F)
        //         )
        //     }
        // }
    }
}
