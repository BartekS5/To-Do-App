package com.example.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Category
import com.example.todoapp.data.Task
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedCategories = MutableStateFlow<Set<String>>(emptySet())

    val tasks = combine(
        taskRepository.getAllTasksStream(),
        categoryRepository.getAllCategoriesStream(),
        searchQuery,
        userPreferencesRepository.showCompletedTasks,
        selectedCategories
    ) { tasks, categoriesList, query, showCompleted, categories ->
        val hiddenCategoryNames = categoriesList
            .filter { !it.isVisible }
            .map { it.name }
            .toSet()

        tasks.filter { task ->
            val matchesSearch = task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)
            val matchesCompletion = if (showCompleted) true else !task.isCompleted
            val matchesCategory = if (categories.isEmpty()) true else task.category in categories
            val isGloballyVisible = task.category !in hiddenCategoryNames

            matchesSearch && matchesCompletion && matchesCategory && isGloballyVisible
        }.sortedBy { it.dueTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.DeleteTask -> viewModelScope.launch { taskRepository.deleteTask(event.task) }
            is TaskEvent.ToggleComplete -> viewModelScope.launch {
                taskRepository.updateTask(event.task.copy(isCompleted = !event.task.isCompleted))
            }
            is TaskEvent.SetSearchQuery -> searchQuery.value = event.query

            is TaskEvent.SaveTask -> viewModelScope.launch { taskRepository.insertTask(event.task) }
            is TaskEvent.SaveCategory -> viewModelScope.launch { categoryRepository.insertCategory(event.category) }
        }
    }
}

sealed interface TaskEvent {
    data class DeleteTask(val task: Task): TaskEvent
    data class ToggleComplete(val task: Task): TaskEvent
    data class SetSearchQuery(val query: String): TaskEvent
    data class SaveTask(val task: Task): TaskEvent
    data class SaveCategory(val category: Category): TaskEvent
}