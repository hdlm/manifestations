package com.budoxr.manifestations.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
// 1. Primarios (Debe ser un tono claro para contrastar el fondo oscuro)
    primary = Color(0xFF90CAF9), // Un azul claro y brillante
    onPrimary = Color(0xFF0D47A1), // Texto oscuro sobre el primario claro
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFBBDEFB),

    // 2. Secundarios
    secondary = Color(0xFFFFB74D), // Un naranja más claro
    onSecondary = Color(0xFFE65100),
    secondaryContainer = Color(0xFFE65100),
    onSecondaryContainer = Color(0xFFFFCCBC),

    // 3. Fondo y Superficie
    background = darkBackground,
    onBackground = Color(0xFFE4E1E6), // Texto claro sobre fondo oscuro
    surface = darkBackground,
    onSurface = Color(0xFFE4E1E6),

    // 4. Error
    error = Color(0xFFCF6679),
    onError = dark
)

private val LightColorScheme = lightColorScheme(
    // 1. Primarios (Botones, AppBar, Iconos importantes)
    primary = brandPrimary,           // El color principal de la marca (saturado)
    onPrimary = bright,          // Texto e iconos sobre el primario
    primaryContainer = brandPrimary, // Un tono más claro para contenedores
    onPrimaryContainer = bright, // Texto sobre el contenedor primario

    // 2. Secundarios (Filtros, acciones secundarias)
    secondary = brandSecondary,       // El color secundario para energía
    onSecondary = dark,
    secondaryContainer = brandSecondary,
    onSecondaryContainer = dark,

    tertiary = brandTertiary,
    onTertiary = dark,

    // 3. Fondo y Superficie (La mayor parte de la UI)
    background = lightBackground,   // Blanco limpio
    onBackground = Color(0xFF1C1B1F), // Texto oscuro sobre fondo claro
    surface = lightBackground,
    onSurface = Color(0xFF1C1B1F),

    // 4. Error (Alertas)
    error = alert,
    onError = bright
)

private val shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)


@Composable
fun ManifestationsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        shapes = shapes,
        typography = Typography,
        content = content
    )
}