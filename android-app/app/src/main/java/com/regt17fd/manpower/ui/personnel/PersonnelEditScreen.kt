package com.regt17fd.manpower.ui.personnel

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.regt17fd.manpower.data.model.Appointment
import com.regt17fd.manpower.data.model.BloodGroup
import com.regt17fd.manpower.data.model.MedicalCategory
import com.regt17fd.manpower.data.model.Rank
import com.regt17fd.manpower.data.model.Section
import com.regt17fd.manpower.data.model.SubUnit
import com.regt17fd.manpower.data.model.Trade
import com.regt17fd.manpower.ui.components.AppDropdown
import com.regt17fd.manpower.ui.components.GoldButton
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.AccentRed
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonnelEditScreen(
    serviceNumber: String?,
    viewModel: PersonnelEditViewModel = hiltViewModel(),
    onSaved: () -> Unit,
) {
    val state by viewModel.formState.collectAsState()

    LaunchedEffect(serviceNumber) {
        if (serviceNumber != null) viewModel.loadForEdit(serviceNumber)
    }
    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.update { f -> f.copy(photoUri = it.toString()) } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = if (state.isEditingExisting) "EDIT PERSONNEL" else "NEW PERSONNEL",
            style = MaterialTheme.typography.headlineMedium,
            color = AccentGold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.serviceNumber,
            onValueChange = { v -> viewModel.update { it.copy(serviceNumber = v) } },
            label = { Text("Service Number") },
            enabled = !state.isEditingExisting,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppDropdown(
            label = "Rank",
            options = Rank.entries,
            selected = state.rank,
            onSelect = { v -> viewModel.update { it.copy(rank = v) } },
            labelOf = { it.displayName },
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.name,
            onValueChange = { v -> viewModel.update { it.copy(name = v) } },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppDropdown("Trade/Arm", Trade.entries, state.trade, { v -> viewModel.update { it.copy(trade = v) } }, { it.displayName })
        Spacer(modifier = Modifier.height(8.dp))
        AppDropdown("Appointment", Appointment.entries, state.appointment, { v -> viewModel.update { it.copy(appointment = v) } }, { it.displayName })
        Spacer(modifier = Modifier.height(8.dp))
        AppDropdown("Sub-Unit", SubUnit.entries, state.subUnit, { v -> viewModel.update { it.copy(subUnit = v) } }, { it.displayName })
        Spacer(modifier = Modifier.height(8.dp))
        AppDropdown("Section/Platoon", Section.entries, state.section, { v -> viewModel.update { it.copy(section = v) } }, { it.displayName })
        Spacer(modifier = Modifier.height(8.dp))

        DatePickerField(
            label = "Date of Joining",
            millis = state.dateOfJoining,
            onPicked = { v -> viewModel.update { it.copy(dateOfJoining = v) } },
        )
        Spacer(modifier = Modifier.height(8.dp))
        DatePickerField(
            label = "Date of Birth",
            millis = state.dateOfBirth,
            onPicked = { v -> viewModel.update { it.copy(dateOfBirth = v) } },
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppDropdown("Blood Group", BloodGroup.entries, state.bloodGroup, { v -> viewModel.update { it.copy(bloodGroup = v) } }, { it.displayName })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.contactNumber,
            onValueChange = { v -> viewModel.update { it.copy(contactNumber = v) } },
            label = { Text("Contact Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.nextOfKin,
            onValueChange = { v -> viewModel.update { it.copy(nextOfKin = v) } },
            label = { Text("Next of Kin") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.nokContact,
            onValueChange = { v -> viewModel.update { it.copy(nokContact = v) } },
            label = { Text("NOK Contact") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))

        AppDropdown("Medical Category", MedicalCategory.entries, state.medicalCategory, { v -> viewModel.update { it.copy(medicalCategory = v) } }, { it.displayName })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.weaponNumber,
            onValueChange = { v -> viewModel.update { it.copy(weaponNumber = v) } },
            label = { Text("Weapon Number (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(text = if (state.photoUri != null) "Photo selected" else "No photo", modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { photoPicker.launch("image/*") }) { Text("Choose Photo") }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = AccentRed)
        }

        Spacer(modifier = Modifier.height(16.dp))
        GoldButton(text = "SAVE", onClick = { viewModel.save() })
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(label: String, millis: Long, onPicked: (Long) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val formatted = remember(millis) { SimpleDateFormat("dd MMM yyyy", Locale.US).format(millis) }

    OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
        Text("$label: $formatted")
    }

    if (showDialog) {
        val state = rememberDatePickerState(initialSelectedDateMillis = millis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let(onPicked)
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = state)
        }
    }
}
