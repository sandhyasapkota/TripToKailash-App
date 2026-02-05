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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom colors for the app
data class AppColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val primaryBlue: Color,
    val primaryBlueLight: Color,
    val accentOrange: Color,
    val accentOrangeLight: Color,
    val success: Color,
    val error: Color,
    val warning: Color,
    val gradientStart: Color,
    val gradientMiddle: Color,
    val gradientEnd: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        textPrimary = TextPrimaryLight,
        textSecondary = TextSecondaryLight,
        cardBackground = CardBackgroundLight,
        cardBorder = CardBorderLight,
        primaryBlue = PrimaryBlue,
        primaryBlueLight = PrimaryBlueLight,
        accentOrange = AccentOrange,
        accentOrangeLight = AccentOrangeLight,
        success = SuccessGreen,
        error = ErrorRed,
        warning = WarningYellow,
        gradientStart = GradientStart,
        gradientMiddle = GradientMiddle,
        gradientEnd = GradientEnd
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    secondary = PurpleGrey80,
    tertiary = AccentOrange,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    onPrimary = Color.White,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PurpleGrey40,
    tertiary = AccentOrange,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    onPrimary = Color.White,
    error = ErrorRed
)

private val LightAppColors = AppColors(
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    cardBackground = CardBackgroundLight,
    cardBorder = CardBorderLight,
    primaryBlue = PrimaryBlue,
    primaryBlueLight = PrimaryBlueLight,
    accentOrange = AccentOrange,
    accentOrangeLight = AccentOrangeLight,
    success = SuccessGreen,
    error = ErrorRed,
    warning = WarningYellow,
    gradientStart = GradientStart,
    gradientMiddle = GradientMiddle,
    gradientEnd = GradientEnd
)

private val DarkAppColors = AppColors(
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    cardBackground = CardBackgroundDark,
    cardBorder = CardBorderDark,
    primaryBlue = PrimaryBlueDark,
    primaryBlueLight = PrimaryBlueLight,
    accentOrange = AccentOrange,
    accentOrangeLight = AccentOrangeLight,
    success = SuccessGreenLight,
    error = ErrorRedLight,
    warning = WarningYellow,
    gradientStart = GradientStart,
    gradientMiddle = GradientMiddle,
    gradientEnd = GradientEnd
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

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