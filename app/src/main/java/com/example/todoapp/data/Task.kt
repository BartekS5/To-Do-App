package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val creationTime: Long = System.currentTimeMillis(),
    val dueTime: Long,
    val isCompleted: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val notificationTimeBefore: Int = 15,
    val category: String,
    val attachmentUris: String = ""
)