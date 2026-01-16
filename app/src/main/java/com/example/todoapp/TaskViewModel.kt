package com.example.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val hideCompleted = MutableStateFlow(false)
    val selectedCategories = MutableStateFlow<Set<String>>(emptySet())

    val tasks = combine(
        dao.getAllTasks(),
        searchQuery,
        hideCompleted,
        selectedCategories
    ) { tasks, query, hide, categories ->
        tasks.filter { task ->
            val matchesSearch = task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)
            val matchesCompletion = if (hide) !task.isCompleted else true
            val matchesCategory = if (categories.isEmpty()) true else task.category in categories

            matchesSearch && matchesCompletion && matchesCategory
        }.sortedBy { it.dueTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> viewModelScope.launch { dao.deleteTask(event.task) }
            is TaskEvent.ToggleComplete -> viewModelScope.launch {
                dao.updateTask(event.task.copy(isCompleted = !event.task.isCompleted))
            }
            is TaskEvent.SetSearchQuery -> searchQuery.value = event.query
            is TaskEvent.ToggleHideCompleted -> hideCompleted.value = event.hide
        }
    }
}

sealed interface TaskEvent {
    data class DeleteTask(val task: Task): TaskEvent
    data class ToggleComplete(val task: Task): TaskEvent
    data class SetSearchQuery(val query: String): TaskEvent
    data class ToggleHideCompleted(val hide: Boolean): TaskEvent
}
