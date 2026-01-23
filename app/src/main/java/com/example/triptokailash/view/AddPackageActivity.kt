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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
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
import com.example.triptokailash.ui.theme.TripToKailashTheme
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
                title = { Text(if (isEditMode) "Edit Package" else "Add New Package") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Image Section ---
            item {
                Spacer(Modifier.height(16.dp))
                AsyncImage(
                    model = imageUrl.ifEmpty { null },
                    contentDescription = "Package Image",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                
                // Image URL input field
                var urlInput by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("Or enter image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPickImage,
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (isLoading) "Uploading..." else "Select from Device", color = MaterialTheme.colorScheme.onPrimary)
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
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Use URL", color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // --- Basic Info Section ---
            item { SectionTitle("Basic Information") }
            item { PackageInputField(value = title, onValueChange = { title = it }, label = "Title", imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { PackageInputField(value = description, onValueChange = { description = it }, label = "Description", isMultiLine = true, imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { PackageInputField(value = price, onValueChange = { price = it }, label = "Price (e.g., 50000.0)", keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { PackageInputField(value = duration, onValueChange = { duration = it }, label = "Duration (e.g., 7 Days, 6 Nights)", imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) }) }
            item { PackageInputField(value = category, onValueChange = { category = it }, label = "Category (e.g., Spiritual, Trekking)", imeAction = ImeAction.Done, onImeAction = { focusManager.clearFocus() }) }

            // --- Itinerary Section (with Add/Remove functionality) ---
            item { SectionTitle("Itinerary") }
            itemsIndexed(itineraryDays) { index, day ->
                ListItem(text = "Day ${day.day}: ${day.title}", onRemove = {
                    itineraryDays = itineraryDays.toMutableList().also { it.removeAt(index) }
                })
            }
            item { ItineraryItemEditor { newDay -> itineraryDays = itineraryDays + newDay.copy(day = itineraryDays.size + 1)} }

            // --- Inclusions Section (with Add/Remove functionality) ---
            item { SectionTitle("Inclusions") }
            itemsIndexed(inclusions) { index, item ->
                ListItem(text = item, onRemove = { inclusions = inclusions.toMutableList().also { it.removeAt(index) } })
            }
            item { ListItemEditor(listName = "Inclusion") { newItem -> inclusions = inclusions + newItem } }

            // --- Exclusions Section (with Add/Remove functionality) ---
            item { SectionTitle("Exclusions") }
            itemsIndexed(exclusions) { index, item ->
                ListItem(text = item, onRemove = { exclusions = exclusions.toMutableList().also { it.removeAt(index) } })
            }
            item { ListItemEditor(listName = "Exclusion") { newItem -> exclusions = exclusions + newItem } }

            // --- Save Button ---
            item {
                Spacer(Modifier.height(32.dp))
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading
                ) {
                    Text(if (isEditMode) "Save Changes" else "Add Package", fontSize = 18.sp)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// --- Reusable Composables for the Form ---

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp).fillMaxWidth()
    )
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
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
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun ListItem(text: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ListItemEditor(listName: String, onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            label = { Text("Add new $listName") },
            singleLine = true,
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
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        IconButton(onClick = {
            if (text.isNotBlank()) {
                onAdd(text)
                text = ""
            }
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ItineraryItemEditor(onAdd: (ItineraryDay) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        PackageInputField(value = title, onValueChange = { title = it }, label = "Day Title", imeAction = ImeAction.Next, onImeAction = { focusManager.moveFocus(FocusDirection.Down) })
        PackageInputField(value = description, onValueChange = { description = it }, label = "Day Description", isMultiLine = true, imeAction = ImeAction.Done, onImeAction = { focusManager.clearFocus() })
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    onAdd(ItineraryDay(day = 0, title = title, description = description))
                    title = ""
                    description = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Add Day to Itinerary", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddPackageScreenPreview() {
    TripToKailashTheme {
        AddPackageScreen(viewModel = viewModel(), onPickImage = {}) {}
    }
}
