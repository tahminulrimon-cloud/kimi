package com.regt17fd.manpower.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.regt17fd.manpower.ui.theme.AccentGold

/**
 * Regiment-colored (gold underline), searchable when the option list is
 * long — spec section 5 "Dropdowns". [labelOf] extracts the display string
 * for each option so this works with any enum/data class.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropdown(
    label: String,
    options: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    labelOf: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val filtered = if (options.size > 10 && query.isNotBlank()) {
        options.filter { labelOf(it).contains(query, ignoreCase = true) }
    } else {
        options
    }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = if (expanded && options.size > 10) query else selected?.let(labelOf).orEmpty(),
            onValueChange = { query = it },
            readOnly = options.size <= 10,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = AccentGold,
                unfocusedIndicatorColor = AccentGold.copy(alpha = 0.5f),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
        ) {
            Box(modifier = Modifier.heightIn(max = 320.dp)) {
                LazyColumn {
                    items(filtered) { option ->
                        DropdownMenuItem(
                            text = { Text(labelOf(option)) },
                            onClick = {
                                onSelect(option)
                                query = ""
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }
}
