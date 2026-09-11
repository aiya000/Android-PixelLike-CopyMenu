package io.github.aiya000.copymenu

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Colors picked to match the reference UI. */
object CopyMenuColors {
    val CardBackground = Color(0xFFDCDAF1)
    val PanelInner = Color(0xFFD7D5EE)
    val OnSurface = Color(0xFF16161F)
    val Cursor = Color(0xFF3A3A55)
    val Scrim = Color(0x66000000)
}

/** Gradient used by the card border and the circular buttons. */
val AccentBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF9BA6F3), Color(0xFFD3A6EB)),
)

/** Gradient used by the editing panel. */
val PanelBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF8490E4), Color(0xFFB292DC)),
)
