package com.example.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.AppViewModelProvider
import com.example.todoapp.ui.theme.ToDoAppTheme

@Composable
fun TodoAppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    ToDoAppTheme {
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = modifier
        ) {
            composable("list") {
                val viewModel: TaskViewModel = viewModel(factory = AppViewModelProvider.Factory)
                ListScreen(
                    viewModel = viewModel,
                    onNavigateToTask = { taskId ->
                        if (taskId != null) {
                            navController.navigate("detail/$taskId")
                        } else {
                            navController.navigate("entry")
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable(
                route = "entry?taskId={taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType; defaultValue = 0 })
            ) {
                ItemEntryScreen(
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() }
                )
            }

            composable(
                route = "detail/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.IntType })
            ) {
                TaskDetailScreen(
                    navigateToEditItem = { taskId ->
                        navController.navigate("entry?taskId=$taskId")
                    },
                    onNavigateUp = { navController.navigateUp() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
