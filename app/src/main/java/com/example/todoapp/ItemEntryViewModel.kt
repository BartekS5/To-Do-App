package com.example.todoapp

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.alarm.AlarmScheduler
import com.example.todoapp.data.Task
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class ItemEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val taskId: Int = savedStateHandle["taskId"] ?: 0

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        if (taskId != 0) {
            viewModelScope.launch {
                taskRepository.getTask(taskId)?.let { task ->
                    _uiState.value = task.toUiState()
                }
            }
        }
    }

    fun updateUiState(newState: TaskUiState) {
        _uiState.value = newState
    }

    fun addAttachment(context: Context, uri: Uri) {
        // Copy file to internal storage
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "attach_${UUID.randomUUID()}"
        val file = File(context.filesDir, fileName)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val currentAttachments = _uiState.value.attachments.toMutableList()
        currentAttachments.add(file.absolutePath)
        updateUiState(_uiState.value.copy(attachments = currentAttachments))
    }

    suspend fun saveTask(context: Context) {
        val task = _uiState.value.toTask(taskId)
        taskRepository.insertTask(task)

        if (task.isNotificationEnabled) {
            val offset = userPreferencesRepository.notificationOffset.first()
            AlarmScheduler(context).schedule(task, offset)
        } else {
            AlarmScheduler(context).cancel(task)
        }
    }
}

data class TaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val dueTime: Long = System.currentTimeMillis(),
    val isNotificationEnabled: Boolean = false,
    val attachments: List<String> = emptyList(),
    val isEntryValid: Boolean = false
)

fun Task.toUiState(): TaskUiState = TaskUiState(
    title = title,
    description = description,
    category = category,
    dueTime = dueTime,
    isNotificationEnabled = isNotificationEnabled,
    attachments = if (attachmentUris.isEmpty()) emptyList() else attachmentUris.split(","),
    isEntryValid = true
)

fun TaskUiState.toTask(id: Int): Task = Task(
    id = id,
    title = title,
    description = description,
    category = category,
    dueTime = dueTime,
    isNotificationEnabled = isNotificationEnabled,
    attachmentUris = attachments.joinToString(",")
)

