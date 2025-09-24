package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de cores para o tema escuro (nosso tema principal)
private val DarkColorScheme = darkColorScheme(
    primary = HalloweenOrange,
    secondary = HalloweenPurple,
    tertiary = HalloweenAccent,
    background = HalloweenDark,
    surface = HalloweenDark,
    onPrimary = HalloweenWhite,
    onSecondary = HalloweenWhite,
    onTertiary = HalloweenDark,
    onBackground = HalloweenWhite,
    onSurface = HalloweenLightGray,
    error = HalloweenRed,
    onError = HalloweenWhite
)

// Paleta de cores para o tema claro (se o usuário não estiver em modo escuro)
private val LightColorScheme = lightColorScheme(
    primary = HalloweenOrange,
    secondary = HalloweenPurple,
    tertiary = HalloweenAccent,
    background = HalloweenWhite,
    surface = Color(0xFFFFF3E0), // Um creme claro para superfícies
    onPrimary = HalloweenWhite,
    onSecondary = HalloweenWhite,
    onTertiary = HalloweenDark,
    onBackground = HalloweenDark,
    onSurface = HalloweenDark,
    error = HalloweenRed,
    onError = HalloweenWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desativamos a cor dinâmica para forçar nosso tema
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Cor da barra de status
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}