package com.regt17fd.manpower.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/** Dashboard numbers animate from 0 up to their value on first composition. */
@Composable
fun CountUpText(targetValue: Int, style: TextStyle = MaterialTheme.typography.headlineMedium) {
    val animated by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 700),
        label = "count-up",
    )
    Text(text = animated.toString(), style = style)
}
