package com.example.balikmeram.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Renkleri artık Color.kt dosyasından otomatik alıyor, çakışma yok!
private val PremiumDarkColorScheme = darkColorScheme(
    primary = OceanTeal,
    onPrimary = Color.White,
    primaryContainer = DeepSeaBlue,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = Color.White,
    background = OceanBackground,
    onBackground = TextWhite,
    surface = CardSurface,
    onSurface = TextWhite,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextMuted
)

@Composable
fun BalikMeramTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepSeaBlue.toArgb()
            window.navigationBarColor = DeepSeaBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = PremiumDarkColorScheme,
        typography = Typography,
        content = content
    )
}