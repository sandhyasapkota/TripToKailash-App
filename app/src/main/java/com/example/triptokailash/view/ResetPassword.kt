//package com.example.triptokailash.view
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.edit
//import com.example.triptokailash.R
//import com.example.triptokailash.ui.theme.TripToKailashTheme
//
//class ResetPassword : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val userEmail = intent.getStringExtra("USER_EMAIL")
//
//        setContent {
//            TripToKailashTheme {
//                if (userEmail != null) {
//                    ResetPasswordScreen(userEmail = userEmail) {
//                        // When the password is updated successfully, navigate to the Login screen
//                        val intent = Intent(this@ResetPassword, Login::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                        finish()
//                    }
//                } else {
//                    Text("Error: User email not found.", color = Color.Red)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//@Preview
//
//fun ResetPasswordScreen(userEmail: String, onPasswordUpdated: () -> Unit) {
//    var newPassword by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//    var newPasswordVisible by remember { mutableStateOf(false) }
//    var confirmPasswordVisible by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    Scaffold { contentPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(contentPadding)
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Reset Password",
//                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            OutlinedTextField(
//                value = newPassword,
//                onValueChange = { newPassword = it },
//                label = { Text("New Password") },
//                modifier = Modifier.fillMaxWidth(),
//                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
//                        Icon(
//                            painter = if (newPasswordVisible) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.nonvisible),
//                            contentDescription = "Toggle new password visibility"
//                        )
//                    }
//                },
//                shape = RoundedCornerShape(15.dp)
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = confirmPassword,
//                onValueChange = { confirmPassword = it },
//                label = { Text("Confirm New Password") },
//                modifier = Modifier.fillMaxWidth(),
//                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
//                        Icon(
//                            painter = if (confirmPasswordVisible) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.nonvisible),
//                            contentDescription = "Toggle confirm password visibility"
//                        )
//                    }
//                },
//                shape = RoundedCornerShape(15.dp)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = {
//                    if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
//                        Toast.makeText(context, "Please fill both fields", Toast.LENGTH_SHORT).show()
//                    } else if (newPassword != confirmPassword) {
//                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
//                    } else {
//                        val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
//                        val savedEmail = sharedPreferences.getString("email", null)
//
//                        if (userEmail == savedEmail) {
//                            sharedPreferences.edit {
//                                putString("password", newPassword)
//                            }
//                            Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_LONG).show()
//                            // This triggers the navigation defined in the Activity
//                            onPasswordUpdated()
//                        } else {
//                            Toast.makeText(context, "Could not update password. Please try again.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                shape = RoundedCornerShape(15.dp)
//            ) {
//                Text("Update Password", fontSize = 18.sp)
//            }
//        }
//    }
//}
