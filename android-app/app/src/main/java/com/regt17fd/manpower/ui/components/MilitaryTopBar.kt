package com.regt17fd.manpower.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.CardBackground
import com.regt17fd.manpower.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Top bar: date/time in military DTG format + the signed-in user's name/rank (spec section 2). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilitaryTopBar(title: String, userLine: String) {
    val dtg = remember(title) {
        SimpleDateFormat("ddHHmm'Z' MMM yy", Locale.US).format(Date()).uppercase(Locale.US)
    }
    CenterAlignedTopAppBar(
        title = {
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge, color = AccentGold)
                Text(text = "$dtg  •  $userLine", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CardBackground),
    )
}
