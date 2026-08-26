package com.regt17fd.manpower.ui.personnel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.regt17fd.manpower.ui.components.StatusChip
import com.regt17fd.manpower.ui.components.chipColor
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.CardBackground
import com.regt17fd.manpower.ui.theme.TextPrimary
import com.regt17fd.manpower.ui.theme.TextSecondary

private val TABS = listOf("Basic Info", "Attendance Log", "Service History", "Documents")

@Composable
fun PersonnelDetailScreen(
    viewModel: PersonnelDetailViewModel = hiltViewModel(),
    onEdit: (String) -> Unit,
) {
    val personnel by viewModel.personnel.collectAsState()
    val history by viewModel.attendanceHistory.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButton = {
            if (isAdmin && personnel != null) {
                FloatingActionButton(onClick = { onEdit(personnel!!.serviceNumber) }, containerColor = AccentGold) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val p = personnel
            if (p == null) {
                Text("Loading…", modifier = Modifier.padding(16.dp), color = TextSecondary)
                return@Column
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${p.rank.displayName} ${p.name}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AccentGold,
                )
                Text(text = p.serviceNumber, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }

            TabRow(selectedTabIndex = selectedTab, containerColor = CardBackground) {
                TABS.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> BasicInfoTab(p)
                1 -> AttendanceLogTab(history)
                else -> Text(
                    text = "Not yet available in this build — planned for a later release.",
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun BasicInfoTab(p: com.regt17fd.manpower.data.local.entity.Personnel) {
    val fields = listOf(
        "Trade/Arm" to p.trade.displayName,
        "Appointment" to p.appointment.displayName,
        "Sub-Unit" to p.subUnit.displayName,
        "Section" to p.section.displayName,
        "Blood Group" to p.bloodGroup.displayName,
        "Medical Category" to p.medicalCategory.displayName,
        "Contact Number" to p.contactNumber,
        "Next of Kin" to p.nextOfKin,
        "NOK Contact" to p.nokContact,
        "Weapon Number" to (p.weaponNumber ?: "—"),
    )
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(fields) { (label, value) ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(text = value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun AttendanceLogTab(history: List<com.regt17fd.manpower.data.local.entity.AttendanceRecord>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(history) { record ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(text = record.date, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                StatusChip(text = record.status.displayName, color = record.status.chipColor())
            }
        }
        if (history.isEmpty()) {
            item { Text("No attendance recorded yet.", color = TextSecondary) }
        }
    }
}
