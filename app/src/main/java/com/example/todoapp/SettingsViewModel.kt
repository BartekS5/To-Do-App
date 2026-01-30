package com.example.todoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Category
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val notificationOffset: Flow<Int> = userPreferencesRepository.notificationOffset
    val allCategories: Flow<List<Category>> = categoryRepository.getAllCategoriesStream()

    fun setNotificationOffset(minutes: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveNotificationOffset(minutes)
        }
    }

    fun toggleCategoryVisibility(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category.copy(isVisible = !category.isVisible))
        }
    }
}