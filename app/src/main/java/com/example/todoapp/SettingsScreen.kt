package com.example.todoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.data.Category
import com.example.todoapp.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val notificationOffset by viewModel.notificationOffset.collectAsState(initial = 5)
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val showCompleted by viewModel.showCompleted.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Text("Display Options", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Completed Tasks", modifier = Modifier.weight(1f))
                Switch(
                    checked = showCompleted,
                    onCheckedChange = { viewModel.setShowCompleted(it) }
                )
            }

            Text("Notifications", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Notify me $notificationOffset minutes before due time")
            Slider(
                value = notificationOffset.toFloat(),
                onValueChange = { viewModel.setNotificationOffset(it.toInt()) },
                valueRange = 0f..60f,
                steps = 11
            )

            Text("Category Visibility", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(categories) { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(category.name, modifier = Modifier.weight(1f))
                        Switch(
                            checked = category.isVisible,
                            onCheckedChange = { viewModel.toggleCategoryVisibility(category) }
                        )
                    }
                }
            }
        }
    }
}