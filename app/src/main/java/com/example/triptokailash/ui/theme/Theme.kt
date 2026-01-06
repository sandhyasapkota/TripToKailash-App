package com.example.triptokailash.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Custom colors for the app
data class AppColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBackground: Color,
    val primaryBlue: Color,
    val accentOrange: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        cardBackground = CardBackgroundLight,
        primaryBlue = PrimaryBlue,
        accentOrange = AccentOrange
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    onPrimary = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    onPrimary = Color.White
)

private val LightAppColors = AppColors(
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    cardBackground = CardBackgroundLight,
    primaryBlue = PrimaryBlue,
    accentOrange = AccentOrange
)

private val DarkAppColors = AppColors(
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    cardBackground = CardBackgroundDark,
    primaryBlue = PrimaryBlueDark,
    accentOrange = AccentOrange
)

@Composable
fun TripToKailashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Extension for easy access to custom colors
object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}