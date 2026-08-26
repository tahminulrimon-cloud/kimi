package com.regt17fd.manpower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.BackgroundPrimary

/** Rounded, dark-to-gold gradient button with a ripple — spec section 5 "Buttons". */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = BackgroundPrimary),
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(listOf(BackgroundPrimary, AccentGold)),
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 4.dp))
    }
}
