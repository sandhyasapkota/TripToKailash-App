package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.triptokailash.R
import com.example.triptokailash.ui.theme.*

class ViewPackages : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val title = intent.getStringExtra("PACKAGE_TITLE") ?: ""
        val duration = intent.getStringExtra("PACKAGE_DURATION") ?: ""
        val price = intent.getStringExtra("PACKAGE_PRICE") ?: ""
        val imageUrl = intent.getStringExtra("PACKAGE_IMAGE_URL") ?: ""
        val description = intent.getStringExtra("PACKAGE_DESCRIPTION") ?: ""
        setContent {
            TripToKailashTheme {
                ViewPackagesScreen(title, duration, price, imageUrl, description) { finish() }
            }
        }
    }
}

data class ItineraryDay(
    val day: String,
    val title: String,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPackagesScreen(
    title: String,
    duration: String,
    price: String,
    imageUrl: String,
    description: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Itinerary", "Inclusions")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title.ifEmpty { "Package Details" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = duration,
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
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
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
            // Sticky Book Now button
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Starting from",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "₹${price.ifEmpty { "N/A" }}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "per person",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            val intent = Intent(context, AppointmentFormActivity::class.java).apply {
                                putExtra("PACKAGE_TITLE", title)
                                putExtra("PACKAGE_PRICE", price)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        )
                    ) {
                        Icon(Icons.Default.EventAvailable, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Enhanced Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    val icon = when (index) {
                        0 -> Icons.Outlined.Info
                        1 -> Icons.Outlined.Map
                        else -> Icons.Outlined.Checklist
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tabTitle, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(icon, contentDescription = tabTitle, modifier = Modifier.size(20.dp)) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> EnhancedOverviewTab(title, duration, price, imageUrl, description)
                    1 -> EnhancedItineraryTab()
                    2 -> EnhancedInclusionsTab()
                }
            }
        }
    }
}

@Composable
fun EnhancedOverviewTab(title: String, duration: String, price: String, imageUrl: String, description: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Hero Image
        item {
            Box {
                AsyncImage(
                    model = imageUrl.ifEmpty { R.drawable.logoo },
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 100f
                            )
                        )
                )
                // Price badge
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen)
                ) {
                    Text(
                        "₹${price.ifEmpty { "N/A" }}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Quick Info Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-30).dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickInfoCard(
                    icon = Icons.Default.Schedule,
                    label = "Duration",
                    value = duration.ifEmpty { "14 Days" },
                    modifier = Modifier.weight(1f)
                )
                QuickInfoCard(
                    icon = Icons.Default.Terrain,
                    label = "Difficulty",
                    value = "Moderate",
                    modifier = Modifier.weight(1f)
                )
                QuickInfoCard(
                    icon = Icons.Default.Groups,
                    label = "Group Size",
                    value = "10-20",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Title and Description
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = title.ifEmpty { "Kailash Mansarovar Yatra" },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Highlights
                Text(
                    "Trip Highlights",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // Highlight chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Sacred", "Scenic", "Adventure", "Cultural").forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null
                    )
                }
            }
        }

        // Description
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "About This Journey",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (description.isNotBlank()) description else """
Embark on a once-in-a-lifetime spiritual journey to the sacred Mount Kailash and Lake Mansarovar.

This package offers a carefully curated itinerary, comfortable accommodations, and expert guidance to ensure a safe and memorable pilgrimage.

Experience the breathtaking landscapes of the Himalayas, participate in traditional rituals, and immerse yourself in the rich culture of Tibet and Nepal.

Our team provides 24/7 support, medical assistance, and all necessary permits for a worry-free adventure.

Join fellow travelers on this transformative yatra and return with blessings and unforgettable memories.
                    """.trimIndent(),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun QuickInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EnhancedItineraryTab() {
    val itinerary = listOf(
        ItineraryDay("Day 1", "Arrival in Kathmandu", "Arrive at Tribhuvan International Airport. Our representative will meet you and transfer you to your hotel. Evening briefing about the trip."),
        ItineraryDay("Day 2", "Kathmandu Sightseeing & Visa", "Visit Pashupatinath Temple and Budhanilkantha. We will also handle the final preparations for your China visa."),
        ItineraryDay("Day 3", "Drive to Syabrubesi (1,550m)", "A 7-8 hour drive from Kathmandu takes us to Syabrubesi, the starting point of our journey in Nepal."),
        ItineraryDay("Day 4", "Cross Border & Drive to Kerung (2,800m)", "Cross the Rasuwagadhi border into Tibet, China. Complete immigration formalities and drive to the town of Kerung."),
        ItineraryDay("Day 5", "Acclimatization Day in Kerung", "Rest day in Kerung to acclimatize to the high altitude. Explore the local town."),
        ItineraryDay("Day 6", "Drive to Saga (4,640m)", "A long drive through the vast Tibetan plateau, offering stunning views of the Himalayas."),
        ItineraryDay("Day 7", "Drive to Lake Mansarovar (4,590m)", "Drive to the holy Lake Mansarovar. On the way, you'll get the first glimpse of Mount Kailash. Perform a holy dip and puja."),
        ItineraryDay("Day 8", "Mansarovar to Darchen (4,670m)", "Explore the shores of Lake Mansarovar. After lunch, a short drive to Darchen, the base camp for the Kailash Parikrama."),
        ItineraryDay("Day 9", "Kailash Parikrama Day 1: Trek to Dirapuk (5,050m)", "Start the holy trek. A challenging day of walking to Dirapuk Monastery, with breathtaking views of the north face of Mt. Kailash."),
        ItineraryDay("Day 10", "Kailash Parikrama Day 2: Trek to Zuthulphuk (4,820m)", "The toughest day of the Yatra. Cross the Dolma La Pass at 5,630m. Visit Gauri Kund before descending to Zuthulphuk."),
        ItineraryDay("Day 11", "Kailash Parikrama Day 3 & Drive to Saga", "End the Parikrama near Darchen. From there, we drive back towards Saga."),
        ItineraryDay("Day 12", "Drive back to Kerung", "Retrace our steps back across the Tibetan plateau to the border town of Kerung."),
        ItineraryDay("Day 13", "Drive back to Kathmandu", "Cross the border back into Nepal and drive to Kathmandu. Farewell dinner in the evening."),
        ItineraryDay("Day 14", "Departure", "Transfer to the airport for your onward journey, carrying with you the blessings of Mount Kailash.")
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(itinerary) { index, dayPlan ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Timeline
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (index == 0) MaterialTheme.colorScheme.onPrimary
                                   else MaterialTheme.colorScheme.primary
                        )
                    }
                    if (index < itinerary.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(80.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        )
                    }
                }
                
                // Content
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AssistChip(
                                onClick = { },
                                label = { Text(dayPlan.day, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.primary
                                ),
                                border = null
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = dayPlan.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = dayPlan.details,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedInclusionsTab() {
    val inclusions = listOf(
        "Accommodation in hotels/guesthouses",
        "Daily breakfast, lunch, and dinner",
        "Transportation throughout the trip (private coach)",
        "Professional English-speaking guide services",
        "All necessary permits and entry fees",
        "First aid kit and emergency support",
        "Welcome and farewell dinners",
        "Bottled mineral water during travel",
        "Oxygen cylinders and medical support",
        "All sightseeing and excursions as per itinerary",
        "Group visa and border assistance",
        "24/7 customer support during the trip"
    )

    val exclusions = listOf(
        "International/domestic flights",
        "Travel insurance",
        "Personal expenses and tips",
        "Lunch during the trip",
        "Any expenses not mentioned in inclusions",
        "Additional activities not in itinerary"
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Inclusions Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "What's Included",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                "${inclusions.size} items",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    inclusions.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Included",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                item,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // Exclusions Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ErrorRed.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ErrorRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "What's Not Included",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                            Text(
                                "${exclusions.size} items",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    exclusions.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Excluded",
                                tint = ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                item,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Keep old composables for backward compatibility
@Composable
fun OverviewTab(title: String, duration: String, price: String, imageUrl: String, description: String) {
    EnhancedOverviewTab(title, duration, price, imageUrl, description)
}

@Composable
fun ItineraryTab() {
    EnhancedItineraryTab()
}

@Composable
fun InclusionsTab() {
    EnhancedInclusionsTab()
}

@Preview(showBackground = true)
@Composable
fun ViewPackagesScreenPreview() {
    TripToKailashTheme {
        ViewPackagesScreen(
            title = "Kailash Yatra",
            duration = "14 days",
            price = "1,50,000",
            imageUrl = "",
            description = "This is a sample overview description for the Kailash Yatra package."
        ) {}
    }
}