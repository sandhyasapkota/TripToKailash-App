package com.example.triptokailash.view

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.triptokailash.model.UserModel
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.utils.ImageUtils
import com.example.triptokailash.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : ComponentActivity() {
    private lateinit var imageUtils: ImageUtils
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            uri?.let {
                // Upload to Cloudinary using ViewModel
                userViewModel.uploadProfileImage(this, it) { success, imageUrl ->
                    if (success && imageUrl != null) {
                        // Update profile picture URL in Firebase Database
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@uploadProfileImage
                        FirebaseDatabase.getInstance().getReference("users/$userId")
                            .child("profilePictureUrl")
                            .setValue(imageUrl)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile image updated!", Toast.LENGTH_SHORT).show()
                                // Refresh user data to update UI
                                userViewModel.getUserById(userId)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        
        setContent {
            TripToKailashTheme {
                ProfileScreen(
                    viewModel = userViewModel,
                    onBack = { finish() },
                    onPickImage = { imageUtils.launchImagePicker() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: UserViewModel, onBack: () -> Unit, onPickImage: () -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Get the current user from Firebase Auth
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var profilePictureUrl by remember { mutableStateOf("") }

    // Observe user data from ViewModel
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.loading.observeAsState(false)
    val uploadedImageUrl by viewModel.profileImageUrl.observeAsState("")

    // Fetch user data when the screen is first composed
    LaunchedEffect(firebaseUser) {
        firebaseUser?.uid?.let {
            viewModel.getUserById(it)
        }
    }

    // Update local state when user data changes
    LaunchedEffect(user) {
        user?.let {
            name = it.userName ?: ""
            address = it.address ?: ""
            contactNumber = it.contactNumber ?: ""
            gender = it.gender ?: ""
            profilePictureUrl = it.profilePictureUrl ?: ""
        }
        email = firebaseUser?.email ?: ""
    }
    
    // Update profile picture when new image is uploaded
    LaunchedEffect(uploadedImageUrl) {
        if (uploadedImageUrl.isNotEmpty()) {
            profilePictureUrl = uploadedImageUrl
        }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Profile",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                ProfileImage(
                    imageUrl = profilePictureUrl,
                    isLoading = isLoading,
                    onImageClick = onPickImage
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { ProfileTextField(label = "Name", value = name, onValueChange = { name = it }, imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) }) }
            // Email is read-only
            item { ProfileTextField(label = "Email", value = email, onValueChange = {}, readOnly = true, imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { ProfileTextField(label = "Address", value = address, onValueChange = { address = it }, imeAction = ImeAction.Next, onNext = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { ProfileTextField(label = "Contact Number", value = contactNumber, onValueChange = { contactNumber = it }, keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done, onNext = { focusManager.clearFocus() }) }
            item { GenderSelector(selectedGender = gender, onGenderSelected = { gender = it }) }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val updatedUser = UserModel(
                            userId = firebaseUser?.uid,
                            userName = name,
                            userEmail = email,
                            address = address,
                            contactNumber = contactNumber,
                            gender = gender,
                            profilePictureUrl = profilePictureUrl
                        )
                        firebaseUser?.uid?.let {
                            viewModel.updateProfile(it, updatedUser) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.logout { _, _ ->
                            val intent = Intent(context, Login::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout", fontSize = 18.sp, color = MaterialTheme.colorScheme.onError)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String, isLoading: Boolean = false, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = !isLoading) { onImageClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (imageUrl.isNotEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    Text(
        text = if (isLoading) "Uploading..." else "Tap to change photo",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()) {
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
            singleLine = true,
            isError = isError,
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext() },
                onDone = { onNext() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun GenderSelector(selectedGender: String, onGenderSelected: (String) -> Unit, isError: Boolean = false) {
    val genders = listOf("Male", "Female", "Other")
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = "Gender",
            fontWeight = FontWeight.SemiBold,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            genders.forEach { gender ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (gender == selectedGender),
                        onClick = { onGenderSelected(gender) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = gender,
                        modifier = Modifier.padding(start = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    TripToKailashTheme {
        ProfileScreen(viewModel = viewModel(), onBack = {}, onPickImage = {})
    }
}
