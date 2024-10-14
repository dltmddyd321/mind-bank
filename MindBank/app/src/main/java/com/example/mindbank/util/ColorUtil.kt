package com.example.mindbank.util

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

fun Color.toHex(): String {
    val red = (red * 255).roundToInt()
    val green = (green * 255).roundToInt()
    val blue = (blue * 255).roundToInt()
    val alpha = (alpha * 255).roundToInt()

    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}

fun hexToColor(hex: String): Color =
    Color(android.graphics.Color.parseColor(hex))

fun isDarkColor(color: Color): Boolean {
    val r = color.red
    val g = color.green
    val b = color.blue
    val luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b
    return luminance < 0.5
}