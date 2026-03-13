package com.example.habittracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FirstColor,
    secondary = SecondColor,
    tertiary = ThirdColor,

    background = Color(0xFF1B1C2E),
    surface = Color(0xFF242640),

    onPrimary = Color(0xFF22233A),
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color(0xFFE4E4FF),
    onSurface = Color(0xFFE4E4FF),
    error = Color(0xFFD32F2F)
)

private val LightColorScheme = lightColorScheme(
    primary = FirstColor,
    secondary = SecondColor,
    tertiary = ThirdColor,

    background = Color(0xFFF6F5FF),
    surface = Color(0xFFF7F6FF),

    onPrimary = Color(0xFFD2D3EF),
    onSecondary = Color.White,
    onTertiary = Color(0xFF2B2B3C),

    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    error = Color(0xFFFF5252)
)

@Composable
fun HabitTrackerTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}