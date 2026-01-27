package com.example.todoapp.data

import android.content.Context
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.OfflineCategoryRepository
import com.example.todoapp.data.repository.OfflineTaskRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.repository.UserPreferencesRepository
import com.example.todoapp.data.repository.dataStore

interface AppContainer {
    val taskRepository: TaskRepository
    val categoryRepository: CategoryRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val taskRepository: TaskRepository by lazy {
        OfflineTaskRepository(database.taskDao())
    }

    override val categoryRepository: CategoryRepository by lazy {
        OfflineCategoryRepository(database.categoryDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}