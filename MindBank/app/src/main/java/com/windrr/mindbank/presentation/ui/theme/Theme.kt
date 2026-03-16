package com.windrr.mindbank.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SpaceDarkColorScheme = darkColorScheme(
    primary = SpacePurple,
    secondary = SpaceLavender,
    tertiary = SpaceCoral,
    background = SpaceNavy,
    surface = SpaceSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = SpaceCloud,
    onSurface = SpaceCloud,
    outline = SpaceBorder
)

private val SpaceLightColorScheme = lightColorScheme(
    primary = SpacePurple,
    secondary = SpaceLavender,
    tertiary = SpaceCoral,
    background = Color.White,
    surface = Color(0xFFF8FAFC),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = SpaceNavy,
    onSurface = SpaceNavy,
    outline = SpaceBorder
)

@Composable
fun MindBankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for consistent space theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        darkTheme -> SpaceDarkColorScheme
        else -> SpaceLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Set status bar and navigation bar colors for better visibility
            window.statusBarColor = if (darkTheme) SpaceNavy.toArgb() else Color.White.toArgb()
            window.navigationBarColor = if (darkTheme) SpaceSurface.toArgb() else Color.White.toArgb()
            
            // Configure status bar appearance
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            
            // Enable edge-to-edge display
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SpaceTheme(
    content: @Composable () -> Unit
) {
    MindBankTheme(
        darkTheme = true, // Force dark theme for space theme
        dynamicColor = false,
        content = content
    )
}