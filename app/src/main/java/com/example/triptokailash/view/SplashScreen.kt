package com.example.triptokailash.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triptokailash.R
import com.example.triptokailash.ui.theme.TripToKailashTheme
import kotlinx.coroutines.delay

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripToKailashTheme {
                SplashScreenContent {
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // 3-second delay
        onTimeout()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF143EAD) // Dark blue background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                    modifier = Modifier.height(200.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,

            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Spiritual Journey Begins Here",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TripToKailashTheme {
        SplashScreenContent { }
    }
}
