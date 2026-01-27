package com.example.todoapp.data.repository

import com.example.todoapp.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasksStream(): Flow<List<Task>>
    fun getTaskStream(id: Int): Flow<Task?>
    fun searchTasksStream(query: String): Flow<List<Task>>

    suspend fun getTask(id: Int): Task?
    suspend fun insertTask(task: Task) : Long
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
}