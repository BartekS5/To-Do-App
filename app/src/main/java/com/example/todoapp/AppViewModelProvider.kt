package com.example.todoapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.data.AppContainer
import com.example.todoapp.ui.theme.ToDoAppTheme

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = todoApplication()

            TaskViewModel(
                taskRepository = app.container.taskRepository,
                categoryRepository = app.container.categoryRepository
            )
        }

        initializer {
            ItemEntryViewModel(
                this.createSavedStateHandle(),
                todoApplication().container.taskRepository,
                todoApplication().container.userPreferencesRepository
            )
        }

        initializer {
            SettingsViewModel(
                todoApplication().container.userPreferencesRepository,
                todoApplication().container.categoryRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ToDoApplication].
 */
fun CreationExtras.todoApplication(): ToDoApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)