package com.cycletracker.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Catppuccin Macchiato — dark scheme
private val MacchiatoColors = darkColorScheme(
    primary              = Macchiato.Mauve,
    onPrimary            = Macchiato.Crust,
    primaryContainer     = Macchiato.Surface1,
    onPrimaryContainer   = Macchiato.Mauve,

    secondary            = Macchiato.Pink,
    onSecondary          = Macchiato.Crust,
    secondaryContainer   = Macchiato.Surface0,
    onSecondaryContainer = Macchiato.Pink,

    tertiary             = Macchiato.Peach,
    onTertiary           = Macchiato.Crust,
    tertiaryContainer    = Macchiato.Surface0,
    onTertiaryContainer  = Macchiato.Peach,

    error                = Macchiato.Red,
    onError              = Macchiato.Crust,
    errorContainer       = Color(0xFF4A1520),
    onErrorContainer     = Macchiato.Red,

    background           = Macchiato.Base,
    onBackground         = Macchiato.Text,
    surface              = Macchiato.Mantle,
    onSurface            = Macchiato.Text,
    surfaceVariant       = Macchiato.Surface0,
    onSurfaceVariant     = Macchiato.Subtext1,
    outline              = Macchiato.Overlay1,
    outlineVariant       = Macchiato.Surface2,

    inverseSurface       = Macchiato.Text,
    inverseOnSurface     = Macchiato.Base,
    inversePrimary       = Latte.Mauve,
    scrim                = Color.Black
)

// Catppuccin Latte — light scheme
private val LatteColors = lightColorScheme(
    primary              = Latte.Mauve,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFEDDBFF),
    onPrimaryContainer   = Latte.Mauve,

    secondary            = Latte.Pink,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFFFDDF4),
    onSecondaryContainer = Latte.Pink,

    tertiary             = Latte.Peach,
    onTertiary           = Color.White,
    tertiaryContainer    = Color(0xFFFFE5D4),
    onTertiaryContainer  = Latte.Peach,

    error                = Latte.Red,
    onError              = Color.White,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Latte.Red,

    background           = Latte.Base,
    onBackground         = Latte.Text,
    surface              = Latte.Mantle,
    onSurface            = Latte.Text,
    surfaceVariant       = Latte.Crust,
    onSurfaceVariant     = Latte.Subtext0,
    outline              = Latte.Overlay1,
    outlineVariant       = Latte.Surface1,

    inverseSurface       = Latte.Text,
    inverseOnSurface     = Latte.Base,
    inversePrimary       = Macchiato.Mauve,
    scrim                = Color.Black
)

@Composable
fun CycleTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use the Catppuccin palette — no dynamic color override
    val colorScheme = if (darkTheme) MacchiatoColors else LatteColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
