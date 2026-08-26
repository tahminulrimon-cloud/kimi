package com.regt17fd.manpower.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Roboto (the platform default) for body text per spec section 5. The spec
 * also asks for a "stencil-style font for headers only" — rather than
 * bundling a licensed stencil font file sight-unseen, headers use the
 * platform sans-serif at a heavy weight + widened letter-spacing, which
 * reads as a military stencil at a glance and needs no font asset. Swap in
 * a real stencil `FontFamily` here later if the unit wants one exactly.
 */
val HeaderFontFamily = FontFamily.SansSerif

val ManpowerTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HeaderFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        letterSpacing = 1.5.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = HeaderFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 1.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = HeaderFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
    ),
)
