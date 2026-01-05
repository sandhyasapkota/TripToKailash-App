package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triptokailash.R
import com.example.triptokailash.model.UserModel
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.viewmodel.UserViewModel

class RegisterActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripToKailashTheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreen() {
    val userViewModel: UserViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))) {
            Image(
                painter = painterResource(id = R.drawable.background1),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = "Register",
                    style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = if (passwordVisible) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.nonvisible),
                                contentDescription = "Toggle password visibility",
                                tint = Color.White
                            )
                        }
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = if (confirmPasswordVisible) painterResource(id = R.drawable.visible) else painterResource(id = R.drawable.nonvisible),
                                contentDescription = "Toggle password visibility",
                                tint = Color.White
                            )
                        }
                    },
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        } else {
                            userViewModel.register(email, password){
                                success,msg,userId->
                                if (success){
                                    val model = UserModel(
                                        userId = userId,
                                        userEmail = email,
                                        userName =  name,
                                        password = password
                                    )
                                    userViewModel.addUserToDatabase(userId, model){
                                            success,msg->
                                        if (success){
                                            Toast.makeText(context,
                                                "Registration Successful!",
                                                Toast.LENGTH_SHORT).show()
                                            val intent = Intent(context, Login::class.java)
                                            context.startActivity(intent)
                                        }
                                    }
                                }else{
                                    Toast.makeText(context,
                                        msg,
                                        Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, Login::class.java)
                                    context.startActivity(intent)




                                }
                            }

//                            val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
//                            val editor = sharedPreferences.edit()
//                            editor.putString("name", name)
//                            editor.putString("email", email)
//                            editor.putString("password", password)
//                            editor.apply()
//
//                            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(context, Login::class.java)
//                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Register", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)) {
                            append("Already have an account? ")
                        }
                        pushStringAnnotation(tag = "LOGIN", annotation = "LOGIN")
                        withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                            append("Login here")
                        }
                        pop()
                    },
                    onClick = { offset ->
                        val annotatedString = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)) {
                                append("Already have an account? ")
                            }
                            pushStringAnnotation(tag = "LOGIN", annotation = "LOGIN")
                            withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                                append("Login here")
                            }
                            pop()
                        }
                        annotatedString.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                            .firstOrNull()?.let {
                                val intent = Intent(context, Login::class.java)
                                context.startActivity(intent)
                            }
                    }
                )
            }
        }
    }
}
