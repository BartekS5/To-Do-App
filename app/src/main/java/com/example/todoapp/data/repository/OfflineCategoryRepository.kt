package com.example.todoapp.data.repository

import com.example.todoapp.data.CategoryDao
import com.example.todoapp.data.Category
import kotlinx.coroutines.flow.Flow

class OfflineCategoryRepository(private val categoryDao: CategoryDao) : CategoryRepository {
    override fun getAllCategoriesStream(): Flow<List<Category>> = categoryDao.getAllCategories()
    override fun getVisibleCategoriesStream(): Flow<List<Category>> = categoryDao.getVisibleCategories()

    override suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    override suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    override suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
}