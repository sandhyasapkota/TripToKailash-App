package com.example.triptokailash.view

import android.R.attr.description
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triptokailash.R
import com.example.triptokailash.ui.theme.TripToKailashTheme

class ViewPackages : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val title = intent.getStringExtra("PACKAGE_TITLE") ?: ""
        val duration = intent.getStringExtra("PACKAGE_DURATION") ?: ""
        val price = intent.getStringExtra("PACKAGE_PRICE") ?: ""
        val imageRes = intent.getIntExtra("PACKAGE_IMAGE", R.drawable.logoo)
        val description = intent.getStringExtra("PACKAGE_DESCRIPTION") ?: ""
        setContent {
            TripToKailashTheme {
                ViewPackagesScreen(title, duration, price, imageRes, description) { finish() }
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
    imageRes: Int,
    description: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Itinerary", "Inclusions")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Trip To Kailash" , fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            modifier = Modifier.padding(top = 10.dp),
                                    contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tabTitle) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> OverviewTab(title, duration, price, imageRes, description)
                    1 -> ItineraryTab()
                    2 -> InclusionsTab()
                }
            }

            Button(
                onClick = {
                    val intent = Intent(context, AppointmentFormActivity::class.java).apply {
                        putExtra("PACKAGE_TITLE", title)
                        putExtra("PACKAGE_PRICE", price)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Book Now", fontSize = 18.sp, color = MaterialTheme.colorScheme.onTertiary)
            }
        }
    }
}

@Composable
fun OverviewTab(title: String, duration: String, price: String, imageRes: Int, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Duration: $duration",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Price: $price",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (description.isNotBlank()) description else """
        • Embark on a once-in-a-lifetime spiritual journey to the sacred Mount Kailash and Lake Mansarovar.

        • This package offers a carefully curated itinerary, comfortable accommodations, and expert guidance to ensure a safe and memorable pilgrimage.

        • Experience the breathtaking landscapes of the Himalayas, participate in traditional rituals, and immerse yourself in the rich culture of Tibet and Nepal.

        • Our team provides 24/7 support, medical assistance, and all necessary permits for a worry-free adventure.

        • Join fellow travelers on this transformative yatra and return with blessings and unforgettable memories.
    """.trimIndent(),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ItineraryTab() {
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

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(itinerary) { dayPlan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${dayPlan.day}: ${dayPlan.title}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayPlan.details,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun InclusionsTab() {
    // Default inclusions for all packages (same as in AddPackageActivity)
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

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Text("What's Included", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(inclusions) { item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Check, contentDescription = "Included", tint = Color(0xFF27AE60))
                Spacer(modifier = Modifier.width(8.dp))
                Text(item, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("What's Not Included", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(exclusions) { item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Excluded", tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text(item, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewPackagesScreenPreview() {
    TripToKailashTheme {
        ViewPackagesScreen(
            title = "Kailash Yatra",
            duration = "14 days",
            price = "Nrs. 1,50,000",
            imageRes = R.drawable.background,
            description = "This is a sample overview description for the Kailash Yatra package."
        ) {}
    }
}