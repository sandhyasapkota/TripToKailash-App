package com.example.triptokailash.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.triptokailash.model.ItineraryDay
import com.example.triptokailash.model.PackageModel
import com.example.triptokailash.ui.theme.*
import com.example.triptokailash.utils.ImageUtils
import com.example.triptokailash.viewmodel.PackageViewModel

class AddPackageActivity : ComponentActivity() {

    private val viewModel: PackageViewModel by viewModels()
    private lateinit var imageUtils: ImageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ImageUtils
        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            if (uri != null) {
                viewModel.uploadImage(this, uri)
            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show()
            }
        }

        // Check if we are in "Edit Mode"
        val packageIdToEdit = intent.getStringExtra("PACKAGE_ID_TO_EDIT")
        if (packageIdToEdit != null) {
            viewModel.getPackage(packageIdToEdit)
        }

        setContent {
            TripToKailashTheme {
                AddPackageScreen(viewModel = viewModel, onPickImage = {
                    // Launch the ImageUtils image picker
                    imageUtils.launchImagePicker()
                }) { finish() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPackageScreen(viewModel: PackageViewModel, onPickImage: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isLoading by viewModel.loading.collectAsState()
    // `package` is a keyword, so we escape it with backticks
    val editingPackage by viewModel.`package`.collectAsState()

    // State for all form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var itineraryDays by remember { mutableStateOf(listOf<ItineraryDay>()) }
    
    // Default inclusions for all packages
    val defaultInclusions = listOf(
        "Accommodation in hotels/guesthouses",
        "Daily breakfast and dinner",
        "Transportation throughout the trip",
        "Professional guide services",
        "All necessary permits and entry fees",
        "First aid kit and emergency support"
    )
    
    // Default exclusions for all packages
    val defaultExclusions = listOf(
        "International/domestic flights",
        "Travel insurance",
        "Personal expenses and tips",
        "Lunch during the trip",
        "Any expenses not mentioned in inclusions",
        "Additional activities not in itinerary"
    )
    
    var inclusions by remember { mutableStateOf(defaultInclusions) }
    var exclusions by remember { mutableStateOf(defaultExclusions) }
    val imageUrl by viewModel.imageUrl.collectAsState()

    val isEditMode = editingPackage != null

    // This effect runs when `editingPackage` changes, pre-filling the form.
    LaunchedEffect(editingPackage) {
        editingPackage?.let {
            title = it.title ?: ""
            description = it.description ?: ""
            price = it.price?.toString() ?: ""
            duration = it.duration ?: ""
            category = it.category ?: ""
            itineraryDays = it.itinerary ?: emptyList()
            inclusions = it.inclusions ?: emptyList()
            exclusions = it.exclusions ?: emptyList()
            viewModel.setImageUrl(it.imageUrl ?: "")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            if (isEditMode) "Edit Package" else "Add New Package",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (isEditMode && title.isNotBlank()) {
                            Text(
                                title,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = { 
                    IconButton(onClick = onBack) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") 
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
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        val finalImageUrl = viewModel.imageUrl.value
                        if (title.isBlank() || price.isBlank() || duration.isBlank() || finalImageUrl.isBlank()) {
                            Toast.makeText(context, "Please fill Title, Price, Duration, and select an Image.", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        val packageModel = (editingPackage ?: PackageModel()).copy(
                            title = title,
                            description = description,
                            price = price.toDoubleOrNull(),
                            duration = duration,
                            category = category,
                            imageUrl = finalImageUrl,
                            itinerary = itineraryDays,
                            inclusions = inclusions,
                            exclusions = exclusions
                        )

                        if (isEditMode) {
                            viewModel.updatePackage(packageModel) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) onBack()
                            }
                        } else {
                            viewModel.addPackage(packageModel) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) onBack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            if (isEditMode) Icons.Default.Save else Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (isEditMode) "Save Changes" else "Add Package",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
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
            // --- Image Section ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        Box {
                            AsyncImage(
                                model = imageUrl.ifEmpty { null },
                                contentDescription = "Package Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                            if (imageUrl.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.Image,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "No image selected",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                        
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Image URL input field
                            var urlInput by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                label = { Text("Image URL") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Link,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Uri,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )
                            
                            Spacer(Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onPickImage,
                                    enabled = !isLoading,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(if (isLoading) "Uploading..." else "Gallery")
                                }
                                
                                Button(
                                    onClick = {
                                        if (urlInput.isNotBlank()) {
                                            viewModel.setImageUrl(urlInput.trim())
                                            urlInput = ""
                                        }
                                    },
                                    enabled = urlInput.isNotBlank(),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Use URL")
                                }
                            }
                        }
                    }
                }
            }

            // --- Basic Info Section ---
            item {
                FormSectionHeader(
                    icon = Icons.Outlined.Info,
                    title = "Basic Information"
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EnhancedPackageInputField(
                            icon = Icons.Outlined.Title,
                            value = title,
                            onValueChange = { title = it },
                            label = "Package Title",
                            imeAction = ImeAction.Next,
                            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        EnhancedPackageInputField(
                            icon = Icons.Outlined.Description,
                            value = description,
                            onValueChange = { description = it },
                            label = "Description",
                            isMultiLine = true,
                            imeAction = ImeAction.Next,
                            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        
                        Spacer(Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EnhancedPackageInputField(
                                icon = Icons.Outlined.CurrencyRupee,
                                value = price,
                                onValueChange = { price = it },
                                label = "Price",
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                                modifier = Modifier.weight(1f)
                            )
                            
                            EnhancedPackageInputField(
                                icon = Icons.Outlined.Schedule,
                                value = duration,
                                onValueChange = { duration = it },
                                label = "Duration",
                                imeAction = ImeAction.Next,
                                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        
                        EnhancedPackageInputField(
                            icon = Icons.Outlined.Category,
                            value = category,
                            onValueChange = { category = it },
                            label = "Category (e.g., Spiritual, Adventure)",
                            imeAction = ImeAction.Done,
                            onImeAction = { focusManager.clearFocus() }
                        )
                    }
                }
            }

            // --- Itinerary Section ---
            item {
                FormSectionHeader(
                    icon = Icons.Outlined.Map,
                    title = "Itinerary",
                    count = itineraryDays.size
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (itineraryDays.isEmpty()) {
                            EmptyStateMessage(
                                icon = Icons.Outlined.Map,
                                message = "No itinerary days added yet"
                            )
                        }
                        
                        itineraryDays.forEachIndexed { index, day ->
                            EnhancedListItem(
                                icon = Icons.Default.Flag,
                                iconColor = MaterialTheme.colorScheme.primary,
                                text = "Day ${day.day}: ${day.title}",
                                subtitle = day.description?.take(50)?.plus(if ((day.description?.length ?: 0) > 50) "..." else ""),
                                onRemove = {
                                    itineraryDays = itineraryDays.toMutableList().also { it.removeAt(index) }
                                }
                            )
                            if (index < itineraryDays.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                        
                        if (itineraryDays.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                        
                        EnhancedItineraryItemEditor { newDay -> 
                            itineraryDays = itineraryDays + newDay.copy(day = itineraryDays.size + 1)
                        }
                    }
                }
            }

            // --- Inclusions Section ---
            item {
                FormSectionHeader(
                    icon = Icons.Outlined.CheckCircle,
                    title = "Inclusions",
                    count = inclusions.size
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.05f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        inclusions.forEachIndexed { index, item ->
                            EnhancedListItem(
                                icon = Icons.Default.Check,
                                iconColor = SuccessGreen,
                                text = item,
                                onRemove = { inclusions = inclusions.toMutableList().also { it.removeAt(index) } }
                            )
                        }
                        
                        if (inclusions.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                        
                        EnhancedListItemEditor(
                            listName = "Inclusion",
                            icon = Icons.Default.Add,
                            iconColor = SuccessGreen
                        ) { newItem -> inclusions = inclusions + newItem }
                    }
                }
            }

            // --- Exclusions Section ---
            item {
                FormSectionHeader(
                    icon = Icons.Outlined.Cancel,
                    title = "Exclusions",
                    count = exclusions.size
                )
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.05f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        exclusions.forEachIndexed { index, item ->
                            EnhancedListItem(
                                icon = Icons.Default.Close,
                                iconColor = ErrorRed,
                                text = item,
                                onRemove = { exclusions = exclusions.toMutableList().also { it.removeAt(index) } }
                            )
                        }
                        
                        if (exclusions.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                        
                        EnhancedListItemEditor(
                            listName = "Exclusion",
                            icon = Icons.Default.Add,
                            iconColor = ErrorRed
                        ) { newItem -> exclusions = exclusions + newItem }
                    }
                }
            }

            // Spacer for bottom bar
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// --- Enhanced Reusable Composables for the Form ---

@Composable
fun FormSectionHeader(icon: ImageVector, title: String, count: Int? = null) {
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
        if (count != null) {
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "$count",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun EnhancedPackageInputField(
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isMultiLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        minLines = if (isMultiLine) 3 else 1,
        maxLines = if (isMultiLine) 5 else 1,
        singleLine = !isMultiLine,
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
fun EnhancedListItem(
    icon: ImageVector,
    iconColor: Color,
    text: String,
    subtitle: String? = null,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun EnhancedListItemEditor(listName: String, icon: ImageVector, iconColor: Color, onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("Add new $listName") },
            leadingIcon = {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (text.isNotBlank()) {
                        onAdd(text)
                        text = ""
                    }
                    focusManager.clearFocus()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = iconColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
        Spacer(Modifier.width(8.dp))
        FilledIconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onAdd(text)
                    text = ""
                }
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = iconColor
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

@Composable
fun EnhancedItineraryItemEditor(onAdd: (ItineraryDay) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Day Title") },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Title,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Day Description") },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        )
        
        Spacer(Modifier.height(12.dp))
        
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onAdd(ItineraryDay(day = 0, title = title, description = description))
                    title = ""
                    description = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add Day to Itinerary", fontWeight = FontWeight.SemiBold)
        }
    }
}

// Keep old composables for backward compatibility
@Composable
fun SectionTitle(title: String) {
    val icon = when {
        title.contains("Basic", ignoreCase = true) -> Icons.Outlined.Info
        title.contains("Itinerary", ignoreCase = true) -> Icons.Outlined.Map
        title.contains("Inclusion", ignoreCase = true) -> Icons.Outlined.CheckCircle
        title.contains("Exclusion", ignoreCase = true) -> Icons.Outlined.Cancel
        else -> Icons.Outlined.Label
    }
    FormSectionHeader(icon = icon, title = title)
}

@Composable
fun PackageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isMultiLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    val icon = when {
        label.contains("Title", ignoreCase = true) -> Icons.Outlined.Title
        label.contains("Description", ignoreCase = true) -> Icons.Outlined.Description
        label.contains("Price", ignoreCase = true) -> Icons.Outlined.CurrencyRupee
        label.contains("Duration", ignoreCase = true) -> Icons.Outlined.Schedule
        label.contains("Category", ignoreCase = true) -> Icons.Outlined.Category
        else -> Icons.Outlined.Edit
    }
    EnhancedPackageInputField(icon, value, onValueChange, label, isMultiLine, keyboardType, imeAction, onImeAction)
}

@Composable
fun ListItem(text: String, onRemove: () -> Unit) {
    EnhancedListItem(
        icon = Icons.Default.Check,
        iconColor = MaterialTheme.colorScheme.primary,
        text = text,
        onRemove = onRemove
    )
}

@Composable
fun ListItemEditor(listName: String, onAdd: (String) -> Unit) {
    EnhancedListItemEditor(
        listName = listName,
        icon = Icons.Default.Add,
        iconColor = MaterialTheme.colorScheme.primary,
        onAdd = onAdd
    )
}

@Composable
fun ItineraryItemEditor(onAdd: (ItineraryDay) -> Unit) {
    EnhancedItineraryItemEditor(onAdd)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddPackageScreenPreview() {
    TripToKailashTheme {
        AddPackageScreen(viewModel = viewModel(), onPickImage = {}) {}
    }
}
