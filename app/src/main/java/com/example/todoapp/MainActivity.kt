package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Manual DI for simplicity (Use Hilt in production)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "todo-database"
        ).build()

        val viewModel = TaskViewModel(db.taskDao())

        setContent {
            ToDoAppTheme {
                // Navigation logic (e.g., NavHost) would go here.
                // For now, displaying the ListScreen directly:
                ListScreen(
                    viewModel = viewModel,
                    onNavigateToTask = { taskId ->
                        // Navigate to Detail/Edit Screen
                    }
                )
            }
        }
    }
}