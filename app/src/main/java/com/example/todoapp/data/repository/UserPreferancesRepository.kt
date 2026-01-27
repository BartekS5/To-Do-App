package com.example.todoapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    private val NOTIFICATION_OFFSET = intPreferencesKey("notification_offset")

    val showCompletedTasks: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[SHOW_COMPLETED] ?: true }

    val notificationOffset: Flow<Int> = dataStore.data
        .map { preferences -> preferences[NOTIFICATION_OFFSET] ?: 5 } // Default 5 minutes

    suspend fun saveShowCompleted(show: Boolean) {
        dataStore.edit { preferences -> preferences[SHOW_COMPLETED] = show }
    }

    suspend fun saveNotificationOffset(minutes: Int) {
        dataStore.edit { preferences -> preferences[NOTIFICATION_OFFSET] = minutes }
    }
}