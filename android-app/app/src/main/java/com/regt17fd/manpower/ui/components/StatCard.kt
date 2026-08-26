package com.regt17fd.manpower.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.CardBackground
import com.regt17fd.manpower.ui.theme.TextSecondary

/** A single dashboard summary tile — spec's "Summary Cards" (horizontal scroll). */
@Composable
fun StatCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    accentColor: Color = AccentGold,
) {
    OutlinedCard(
        modifier = modifier
            .defaultMinSize(minWidth = 120.dp)
            .height(96.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, accentColor),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            CountUpText(targetValue = value, style = MaterialTheme.typography.headlineMedium.copy(color = accentColor))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}
