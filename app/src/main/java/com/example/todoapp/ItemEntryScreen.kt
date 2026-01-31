package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.alarm.AlarmScheduler
import com.example.todoapp.data.Task
import com.example.todoapp.AppViewModelProvider
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val itemUiState by viewModel.uiState.collectAsState()

    // File Picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.addAttachment(context, it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemUiState.isEntryValid) "Edit Task" else "New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.saveTask(context)
                            navigateBack()
                        }
                    }) {
                        Icon(Icons.Filled.Save, "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = itemUiState.title,
                onValueChange = { viewModel.updateUiState(itemUiState.copy(title = it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = itemUiState.description,
                onValueChange = { viewModel.updateUiState(itemUiState.copy(description = it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Category (Simple implementation)
            OutlinedTextField(
                value = itemUiState.category,
                onValueChange = { viewModel.updateUiState(itemUiState.copy(category = it)) },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date & Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Due: ${formatDate(itemUiState.dueTime)}",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showDateTimePicker(context, itemUiState.dueTime) { viewModel.updateUiState(itemUiState.copy(dueTime = it)) } }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }
            }

            // Notifications
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Enable Notification", modifier = Modifier.weight(1f))
                Switch(
                    checked = itemUiState.isNotificationEnabled,
                    onCheckedChange = { viewModel.updateUiState(itemUiState.copy(isNotificationEnabled = it)) }
                )
            }

            // Attachments
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Attachments", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { launcher.launch("*/*") }) {
                    Icon(Icons.Default.AttachFile, "Add Attachment")
                }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itemUiState.attachments) { uriStr ->
                    SuggestionChip(
                        onClick = { openFile(context, uriStr.toString()) },
                        label = { Text("File") }
                    )
                }
            }
        }
    }
}

fun showDateTimePicker(context: Context, current: Long, onDateSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = current }
    DatePickerDialog(context, { _, year, month, day ->
        TimePickerDialog(context, { _, hour, minute ->
            calendar.set(year, month, day, hour, minute)
            onDateSelected(calendar.timeInMillis)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}

fun formatDate(timestamp: Long): String = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(timestamp))

fun openFile(context: Context, uriString: String) {
    val file = java.io.File(uriString)
    if (!file.exists()) {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        // 1. Get the content URI using FileProvider
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)

        // 2. Determine the MIME type (e.g., "image/jpeg")
        // FileProvider determines this automatically if the file has an extension
        val mimeType = context.contentResolver.getType(uri) ?: "*/*"

        // 3. Set Data AND Type explicitly
        intent.setDataAndType(uri, mimeType)

        // 4. Grant permissions
        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show()
    }
}

