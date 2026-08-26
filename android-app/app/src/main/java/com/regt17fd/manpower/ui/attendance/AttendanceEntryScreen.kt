package com.regt17fd.manpower.ui.attendance

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.regt17fd.manpower.data.model.AttendanceStatus
import com.regt17fd.manpower.data.model.DutyAssignment
import com.regt17fd.manpower.data.model.SubUnit
import com.regt17fd.manpower.ui.components.AppDropdown
import com.regt17fd.manpower.ui.components.StatusChip
import com.regt17fd.manpower.ui.components.chipColor
import com.regt17fd.manpower.ui.theme.CardBackground
import com.regt17fd.manpower.ui.theme.TextPrimary
import com.regt17fd.manpower.ui.theme.TextSecondary

private val TABS = listOf("By Individual", "By Sub-Unit", "By Status")

@Composable
fun AttendanceEntryScreen(viewModel: AttendanceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<RosterRow?>(null) }
    var statusFilter by remember { mutableStateOf<AttendanceStatus?>(null) }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "DAILY ENTRY — ${state.date}",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp),
            )
            TabRow(selectedTabIndex = selectedTab, containerColor = CardBackground) {
                TABS.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> RosterList(rows = state.roster, onClick = { editing = it })
                1 -> SubUnitTree(rows = state.roster, onClick = { editing = it })
                2 -> StatusFilterView(
                    rows = state.roster,
                    filter = statusFilter,
                    onFilterChange = { statusFilter = it },
                    onClick = { editing = it },
                )
            }
        }
    }

    editing?.let { row ->
        AttendanceEditDialog(
            row = row,
            onDismiss = { editing = null },
            onSave = { status, duty, location, remarks ->
                viewModel.setStatus(row.personnel, status, duty, location, remarks)
                editing = null
            },
        )
    }
}

@Composable
private fun RosterList(rows: List<RosterRow>, onClick: (RosterRow) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rows, key = { it.personnel.serviceNumber }) { row -> RosterRowCard(row, onClick) }
    }
}

@Composable
private fun SubUnitTree(rows: List<RosterRow>, onClick: (RosterRow) -> Unit) {
    val grouped = rows.groupBy { it.personnel.subUnit }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SubUnit.entries.forEach { su ->
            val group = grouped[su].orEmpty()
            if (group.isNotEmpty()) {
                item {
                    Text(
                        text = "${su.displayName} (${group.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                }
                items(group, key = { it.personnel.serviceNumber }) { row -> RosterRowCard(row, onClick) }
            }
        }
    }
}

@Composable
private fun StatusFilterView(
    rows: List<RosterRow>,
    filter: AttendanceStatus?,
    onFilterChange: (AttendanceStatus?) -> Unit,
    onClick: (RosterRow) -> Unit,
) {
    Column {
        AppDropdown(
            label = "Filter by Status",
            options = listOf<AttendanceStatus?>(null) + AttendanceStatus.entries,
            selected = filter,
            onSelect = onFilterChange,
            labelOf = { it?.displayName ?: "All" },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        val filtered = if (filter == null) rows else rows.filter { it.record?.status == filter }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.personnel.serviceNumber }) { row -> RosterRowCard(row, onClick) }
        }
    }
}

@Composable
private fun RosterRowCard(row: RosterRow, onClick: (RosterRow) -> Unit) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick(row) },
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "${row.personnel.rank.displayName} ${row.personnel.name}",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(text = row.personnel.serviceNumber, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            val status = row.record?.status
            StatusChip(text = status?.displayName ?: "Not recorded", color = status?.chipColor() ?: TextSecondary)
        }
    }
}

@Composable
private fun AttendanceEditDialog(
    row: RosterRow,
    onDismiss: () -> Unit,
    onSave: (AttendanceStatus, DutyAssignment, String?, String?) -> Unit,
) {
    var status by remember { mutableStateOf(row.record?.status ?: AttendanceStatus.PRESENT) }
    var duty by remember { mutableStateOf(row.record?.dutyAssignment ?: DutyAssignment.NORMAL_DUTY) }
    var location by remember { mutableStateOf(row.record?.location.orEmpty()) }
    var remarks by remember { mutableStateOf(row.record?.remarks.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${row.personnel.rank.displayName} ${row.personnel.name}") },
        text = {
            Column {
                AppDropdown("Status", AttendanceStatus.entries, status, { status = it }, { it.displayName })
                Spacer(modifier = Modifier.height(8.dp))
                AppDropdown("Duty Assignment", DutyAssignment.entries, duty, { duty = it }, { it.displayName })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (if detached/TD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { if (it.length <= 100) remarks = it },
                    label = { Text("Remarks (max 100 chars)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(status, duty, location.ifBlank { null }, remarks.ifBlank { null }) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
