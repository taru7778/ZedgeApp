package com.zedge.contentstudio.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.zedge.contentstudio.data.ThemeConfig

// ---------------------------------------------------------------------------
// v26 Theme Studio: the palette is user-configurable (per Zedge account, saved in
// Firebase dashboardSettings/theme - same schema as the web panel). The Brand*
// colours below read from the live palette, so every screen re-skins instantly.
// ---------------------------------------------------------------------------

object ThemeState {
    var config: ThemeConfig by mutableStateOf(ThemeConfig())
        private set
    var palette: Palette by mutableStateOf(Palette.of(ThemeConfig()))
        private set
    fun set(cfg: ThemeConfig) {
        if (cfg != config) { config = cfg; palette = Palette.of(cfg) }
    }
}

fun parseHex(hex: String?, fallback: Color): Color {
    val h = ThemeConfig.normHex(hex, "")
    if (h.isEmpty()) return fallback
    return Color(0xFF000000L or h.substring(1).toLong(16))
}

fun mixColor(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f,
)

class Palette(val primary: Color, val accent: Color, val bg: Color, val surface: Color, val text: Color) {
    val isDark: Boolean = bg.luminance() < 0.35f
    val onPrimary: Color = if (primary.luminance() > 0.25f) (if (surface.luminance() < 0.1f) surface else Color(0xFF1C1A12)) else Color.White
    val onAccent: Color = if (accent.luminance() > 0.36f) Color(0xFF1C1A12) else Color.White
    /** Dark "ink" used for header-like blocks and dark buttons (elevated surface in dark mode). */
    val ink: Color = if (isDark) mixColor(surface, text, 0.14f) else text
    val muted: Color = mixColor(text, surface, 0.45f)
    val line: Color = mixColor(surface, text, 0.12f)

    fun scheme(): ColorScheme = if (isDark) darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = mixColor(surface, primary, 0.30f),
        onPrimaryContainer = mixColor(primary, Color.White, 0.40f),
        secondary = accent,
        onSecondary = onAccent,
        secondaryContainer = mixColor(surface, accent, 0.25f),
        onSecondaryContainer = mixColor(accent, Color.White, 0.45f),
        tertiary = mixColor(text, accent, 0.40f),
        background = bg,
        onBackground = text,
        surface = surface,
        onSurface = text,
        surfaceVariant = mixColor(surface, text, 0.08f),
        onSurfaceVariant = mixColor(text, surface, 0.30f),
        surfaceContainerLow = mixColor(surface, text, 0.02f),
        surfaceContainer = mixColor(surface, text, 0.05f),
        surfaceContainerHigh = mixColor(surface, text, 0.08f),
        surfaceContainerHighest = mixColor(surface, text, 0.11f),
        outline = mixColor(surface, text, 0.24f),
        outlineVariant = line,
        error = Color(0xFFFF7A7A),
    ) else lightColorScheme(
        primary = ink,
        onPrimary = primary,
        primaryContainer = primary,
        onPrimaryContainer = onPrimary,
        secondary = accent,
        onSecondary = onAccent,
        secondaryContainer = mixColor(surface, primary, 0.30f),
        onSecondaryContainer = text,
        tertiary = mixColor(text, accent, 0.30f),
        background = bg,
        onBackground = text,
        surface = surface,
        onSurface = text,
        surfaceVariant = mixColor(surface, primary, 0.15f),
        onSurfaceVariant = mixColor(text, surface, 0.35f),
        surfaceContainerLow = mixColor(surface, primary, 0.06f),
        surfaceContainer = mixColor(surface, primary, 0.10f),
        surfaceContainerHigh = mixColor(surface, primary, 0.15f),
        surfaceContainerHighest = mixColor(surface, primary, 0.21f),
        outline = mixColor(surface, text, 0.20f),
        outlineVariant = line,
        error = Danger,
    )

    companion object {
        fun of(cfg: ThemeConfig): Palette {
            val c = cfg.sanitized()
            return Palette(
                primary = parseHex(c.primary, Color(0xFFFFD400)),
                accent = parseHex(c.accent, Color(0xFFFFAB00)),
                bg = parseHex(c.bg, Color(0xFFFFFDF6)),
                surface = parseHex(c.surface, Color(0xFFFFFFFF)),
                text = parseHex(c.text, Color(0xFF211D12)),
            )
        }
    }
}

// Brand palette - live values (default = the Sunflower theme, identical to the old constants)
val BrandYellow: Color get() = ThemeState.palette.primary
val BrandAmber: Color get() = ThemeState.palette.accent
val BrandDark: Color get() = ThemeState.palette.onPrimary
val BrandBg: Color get() = ThemeState.palette.bg
val BrandMuted: Color get() = ThemeState.palette.muted
val BrandCard: Color get() = ThemeState.palette.surface
val BrandLine: Color get() = ThemeState.palette.line

// Content type accents (semantic - not themed)
val TypeAudio = Color(0xFF8B5CF6)
val TypeWallpaper = Color(0xFF38BDF8)
val Type24h = Color(0xFFFBBF24)
val TypeDual = Color(0xFF34D399)
val TypeBattery = Color(0xFFF87171)
val TypeLive = Color(0xFFF472B6)
val TypeCharging = Color(0xFF2DD4BF)
val Ok = Color(0xFF22C55E)
val Warn = Color(0xFFF59E0B)
val Danger = Color(0xFFEF4444)

fun typeColor(type: String?): Color = when (type) {
    "AUDIO", "RINGTONE" -> TypeAudio
    "WALLPAPER" -> TypeWallpaper
    "WALLPAPER_24H" -> Type24h
    "WALLPAPER_DUAL" -> TypeDual
    "WALLPAPER_BATTERY" -> TypeBattery
    "LIVE_WALLPAPER" -> TypeLive
    "CHARGING_ANIMATION" -> TypeCharging
    else -> BrandMuted
}

fun appShapes(radius: Float): Shapes = Shapes(
    extraSmall = RoundedCornerShape((8f * radius).dp),
    small = RoundedCornerShape((12f * radius).dp),
    medium = RoundedCornerShape((16f * radius).dp),
    large = RoundedCornerShape((20f * radius).dp),
    extraLarge = RoundedCornerShape((28f * radius).dp),
)

val AppShapes: Shapes = appShapes(1f)

/** Compact, phone-friendly type scale. Everything is a step smaller than Material defaults. */
val AppTypography = Typography(
    displaySmall = TextStyle(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp),
    headlineSmall = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp),
    titleLarge = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
    titleSmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 11.5.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 11.5.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = 10.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp),
)

/**
 * v26: theme driven by the user's ThemeConfig (null = default Sunflower).
 * Font size and density are applied through LocalDensity so every dp/sp in the app scales.
 */
@Composable
fun ContentStudioTheme(config: ThemeConfig? = null, content: @Composable () -> Unit) {
    val cfg = (config ?: ThemeConfig()).sanitized()
    ThemeState.set(cfg)
    val palette = ThemeState.palette
    val scheme = remember(cfg) { palette.scheme() }
    val shapes = remember(cfg.radius) { appShapes(cfg.radius) }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = scheme.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = scheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !palette.isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !palette.isDark
        }
    }
    val base = LocalDensity.current
    val densityScale = if (cfg.density == "compact") 0.92f else 1f
    val density = remember(base, cfg.fontScale, densityScale) { Density(base.density * densityScale, base.fontScale * cfg.fontScale) }
    CompositionLocalProvider(LocalDensity provides density) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography, shapes = shapes, content = content)
    }
}
