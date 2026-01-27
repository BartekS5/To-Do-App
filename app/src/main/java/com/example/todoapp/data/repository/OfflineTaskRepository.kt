package com.example.todoapp.data.repository

import com.example.todoapp.data.TaskDao
import com.example.todoapp.data.Task
import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(private val taskDao: TaskDao) : TaskRepository {

    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()
    override fun getTaskStream(id: Int): Flow<Task?> = taskDao.getTaskFlow(id)
    override fun searchTasksStream(query: String): Flow<List<Task>> = taskDao.searchTasks(query)

    override suspend fun insertTask(task: Task) : Long = taskDao.insertTask(task)
    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    override suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    // Snapshot
    override suspend fun getTask(id: Int): Task? = taskDao.getTaskById(id)
}