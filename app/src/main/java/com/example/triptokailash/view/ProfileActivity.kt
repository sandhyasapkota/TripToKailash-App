package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.triptokailash.ui.theme.*
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
                title = {
                    Column {
                        Text("My Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            email,
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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header with gradient background
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        EnhancedProfileImage(
                            imageUrl = profilePictureUrl,
                            isLoading = isLoading,
                            onImageClick = onPickImage
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = name.ifEmpty { "Your Name" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Personal Information Section
            item {
                ProfileSectionHeader(
                    icon = Icons.Outlined.Person,
                    title = "Personal Information"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EnhancedProfileTextField(
                            icon = Icons.Outlined.Badge,
                            label = "Full Name",
                            value = name,
                            onValueChange = { name = it },
                            imeAction = ImeAction.Next,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        EnhancedProfileTextField(
                            icon = Icons.Outlined.Email,
                            label = "Email Address",
                            value = email,
                            onValueChange = {},
                            readOnly = true,
                            imeAction = ImeAction.Next,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    }
                }
            }

            // Contact Information Section
            item {
                Spacer(Modifier.height(16.dp))
                ProfileSectionHeader(
                    icon = Icons.Outlined.ContactPhone,
                    title = "Contact Information"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EnhancedProfileTextField(
                            icon = Icons.Outlined.LocationOn,
                            label = "Address",
                            value = address,
                            onValueChange = { address = it },
                            imeAction = ImeAction.Next,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        EnhancedProfileTextField(
                            icon = Icons.Outlined.Phone,
                            label = "Phone Number",
                            value = contactNumber,
                            onValueChange = { contactNumber = it },
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done,
                            onNext = { focusManager.clearFocus() }
                        )
                    }
                }
            }

            // Gender Section
            item {
                Spacer(Modifier.height(16.dp))
                ProfileSectionHeader(
                    icon = Icons.Outlined.Wc,
                    title = "Gender"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    EnhancedGenderSelector(
                        selectedGender = gender,
                        onGenderSelected = { gender = it }
                    )
                }
            }

            // Action Buttons
            item {
                Spacer(Modifier.height(32.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = {
                            viewModel.logout { _, _ ->
                                val intent = Intent(context, Login::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(ErrorRed, ErrorRed))
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
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
fun EnhancedProfileImage(imageUrl: String, isLoading: Boolean = false, onImageClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                    ),
                    shape = CircleShape
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(enabled = !isLoading) { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
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
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Camera badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(enabled = !isLoading) { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Change Photo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    
    Text(
        text = if (isLoading) "Uploading..." else "Tap to change photo",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun EnhancedProfileTextField(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onNext: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = label,
                tint = if (readOnly) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                       else MaterialTheme.colorScheme.primary
            )
        },
        singleLine = true,
        isError = isError,
        readOnly = readOnly,
        enabled = !readOnly,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() },
            onDone = { onNext() }
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun EnhancedGenderSelector(selectedGender: String, onGenderSelected: (String) -> Unit) {
    val genders = listOf(
        Triple("Male", Icons.Default.Male, InfoBlue),
        Triple("Female", Icons.Default.Female, Color(0xFFE91E63)),
        Triple("Other", Icons.Default.Transgender, AccentGold)
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        genders.forEach { (gender, icon, color) ->
            val isSelected = gender == selectedGender
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
                label = "genderColor"
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onGenderSelected(gender) }
                    .background(animatedColor.copy(alpha = if (isSelected) 0.15f else 0.5f))
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) color else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = gender,
                    tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = gender,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Keep old composables for backward compatibility
@Composable
fun ProfileImage(imageUrl: String, isLoading: Boolean = false, onImageClick: () -> Unit) {
    EnhancedProfileImage(imageUrl, isLoading, onImageClick)
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
    val icon = when (label.lowercase()) {
        "name" -> Icons.Outlined.Badge
        "email" -> Icons.Outlined.Email
        "address" -> Icons.Outlined.LocationOn
        "contact number" -> Icons.Outlined.Phone
        else -> Icons.Outlined.Info
    }
    EnhancedProfileTextField(icon, label, value, onValueChange, isError, readOnly, keyboardType, imeAction, onNext)
}

@Composable
fun GenderSelector(selectedGender: String, onGenderSelected: (String) -> Unit, isError: Boolean = false) {
    EnhancedGenderSelector(selectedGender, onGenderSelected)
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    TripToKailashTheme {
        ProfileScreen(viewModel = viewModel(), onBack = {}, onPickImage = {})
    }
}
