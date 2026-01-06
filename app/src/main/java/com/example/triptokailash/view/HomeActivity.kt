package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triptokailash.R
import com.example.triptokailash.ui.theme.TripToKailashTheme
import com.example.triptokailash.view.ExperiencesActivity
import com.example.triptokailash.view.MyAppointmentsActivity
import com.example.triptokailash.view.PackageActivity
import com.example.triptokailash.view.ProfileActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripToKailashTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            HeroSection()
        }
        item {
            WhyChooseUsSection()
        }
    }
}

@Composable
fun HeroSection() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .paint(
                painter = painterResource(id = R.drawable.homepage),
                contentScale = ContentScale.Crop
            )
    ) {
        // Scrim: A dark gradient overlay for better text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.7f)),
                        startY = 0f
                    )
                )
        )
        
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TripToKailash",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.White
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(
                    onClick = { context.startActivity(Intent(context, MyAppointmentsActivity::class.java)) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("My Trips", color = Color.White, fontSize = 12.sp)
                }
                TextButton(
                    onClick = { context.startActivity(Intent(context, ExperiencesActivity::class.java)) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Experiences", color = Color.White, fontSize = 12.sp)
                }
                TextButton(
                    onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Profile", color = Color.White, fontSize = 12.sp)
                }
            }
        }
        
        // Hero Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = "Journey to the\nHoly Mount Kailash",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Discover handcrafted packages for your spiritual pilgrimage.",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { context.startActivity(Intent(context, PackageActivity::class.java)) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5A623)),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Explore Packages", fontSize = 16.sp, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { context.startActivity(Intent(context, ShareExperienceActivity::class.java)) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Share Your Experience", fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun WhyChooseUsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Why Travel With Us?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureCard(
                painter = painterResource(id = R.drawable.hiking),
                title = "Expert Guides",
                description = "Our certified guides have decades of experience leading pilgrims safely.",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                painter = painterResource(id = R.drawable.healthandsafety),
                title = "Safety First",
                description = "We carry satellite phones, medical kits, and oxygen for your well-being.",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                painter = painterResource(id = R.drawable.diamond),
                title = "Authentic Experience",
                description = "A culturally rich journey focused on spiritual fulfillment, not just tourism.",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeatureCard(painter: Painter, title: String, description: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(painter = painter, contentDescription = title, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    TripToKailashTheme {
        HomeScreen()
    }
}
