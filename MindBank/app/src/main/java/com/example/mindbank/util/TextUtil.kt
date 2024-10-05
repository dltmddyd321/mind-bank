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