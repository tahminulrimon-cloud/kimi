package com.regt17fd.manpower.ui.personnel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.regt17fd.manpower.ui.theme.AccentGold
import com.regt17fd.manpower.ui.theme.CardBackground
import com.regt17fd.manpower.ui.theme.TextPrimary
import com.regt17fd.manpower.ui.theme.TextSecondary

@Composable
fun PersonnelListScreen(
    viewModel: PersonnelListViewModel = hiltViewModel(),
    onOpenDetail: (String) -> Unit,
    onCreateNew: () -> Unit,
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = onCreateNew, containerColor = AccentGold) {
                    Icon(Icons.Filled.Add, contentDescription = "Add personnel")
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Search name, service number, rank") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(results, key = { it.serviceNumber }) { person ->
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDetail(person.serviceNumber) },
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${person.rank.displayName} ${person.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                            )
                            Text(
                                text = "${person.serviceNumber}  •  ${person.subUnit.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}
