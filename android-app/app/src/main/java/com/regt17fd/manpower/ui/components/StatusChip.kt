package com.regt17fd.manpower.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.regt17fd.manpower.data.model.AttendanceStatus
import com.regt17fd.manpower.ui.theme.DangerRed
import com.regt17fd.manpower.ui.theme.SuccessGreen
import com.regt17fd.manpower.ui.theme.WarningOrange

/** Maps an attendance status to the green/amber/red family used throughout the UI. */
fun AttendanceStatus.chipColor(): Color = when (this) {
    AttendanceStatus.PRESENT -> SuccessGreen
    AttendanceStatus.ANNUAL_LEAVE, AttendanceStatus.CASUAL_LEAVE,
    AttendanceStatus.TD, AttendanceStatus.COURSE, AttendanceStatus.DETACHED,
    AttendanceStatus.MATERNITY_PATERNITY,
    -> WarningOrange
    else -> DangerRed
}

@Composable
fun StatusChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
