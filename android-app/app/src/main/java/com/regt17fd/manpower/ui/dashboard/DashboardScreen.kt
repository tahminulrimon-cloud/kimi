package com.regt17fd.manpower.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.regt17fd.manpower.ui.components.MilitaryTopBar
import com.regt17fd.manpower.ui.components.StatCard
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.AccentRed
import com.regt17fd.manpower.ui.theme.CardBackground
import com.regt17fd.manpower.ui.theme.DangerRed
import com.regt17fd.manpower.ui.theme.SuccessGreen
import com.regt17fd.manpower.ui.theme.TextPrimary
import com.regt17fd.manpower.ui.theme.TextSecondary
import com.regt17fd.manpower.ui.theme.WarningOrange

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onOpenPersonnel: () -> Unit,
    onOpenAttendanceEntry: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { MilitaryTopBar(title = "17 FD REGT ARTY — MANPOWER STATE", userLine = state.userLine) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Daily Entry") },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                onClick = onOpenAttendanceEntry,
                containerColor = AccentGold,
                elevation = FloatingActionButtonDefaults.elevation(),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    StatCard(
                        label = "Strength ${state.totalPresent}/${state.totalAuthorised}",
                        value = state.totalPresent,
                        accentColor = AccentGold,
                    )
                }
                item { StatCard(label = "On Duty", value = state.onDuty, accentColor = SuccessGreen) }
                item { StatCard(label = "On Leave", value = state.onLeave, accentColor = WarningOrange) }
                item { StatCard(label = "Sick", value = state.sick, accentColor = WarningOrange) }
                item { StatCard(label = "TD / Course", value = state.tdOrCourse, accentColor = WarningOrange) }
                item {
                    StatCard(
                        label = "AWOL",
                        value = state.awol,
                        accentColor = if (state.awol > 0) DangerRed else TextSecondary,
                    )
                }
            }

            Text(
                text = "SUB-UNIT BREAKDOWN",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(state.subUnitBreakdown) { row ->
                    SubUnitCard(row, onClick = onOpenPersonnel)
                }
            }
        }
    }
}

@Composable
private fun SubUnitCard(row: SubUnitBreakdown, onClick: () -> Unit) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = row.subUnit.displayName, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                Text(
                    text = "${row.present}/${row.authorised}  (${row.percentage}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (row.isCriticalShortage) DangerRed else TextSecondary,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { row.percentage / 100f },
                color = if (row.isCriticalShortage) AccentRed else SuccessGreen,
                trackColor = CardBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
            )
            if (row.isCriticalShortage) {
                Text(
                    text = "⚠ Critical shortage — below 70% manning",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentRed,
                )
            }
        }
    }
}
