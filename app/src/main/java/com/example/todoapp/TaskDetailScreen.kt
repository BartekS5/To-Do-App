package com.example.todoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.AppViewModelProvider
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navigateToEditItem: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigateToEditItem(viewModel.taskId ?: 0) }) {
                Icon(Icons.Default.Edit, "Edit")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(uiState.title, style = MaterialTheme.typography.headlineMedium)
            Text("Category: ${uiState.category}", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Created: ${formatDate(uiState.creationTime)}")
            Text("Due: ${formatDate(uiState.dueTime)}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(uiState.description, style = MaterialTheme.typography.bodyLarge)

            if(uiState.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Attachments:", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.attachments) { path ->
                        TextButton(onClick = { openFile(context, path) }) {
                            Text("View Attachment")
                        }
                    }
                }
            }
        }
    }
}