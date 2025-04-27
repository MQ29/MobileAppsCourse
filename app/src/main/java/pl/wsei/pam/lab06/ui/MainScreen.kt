package pl.wsei.pam.lab06.ui

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import pl.wsei.pam.lab06.TodoApplication
import pl.wsei.pam.lab06.ui.screens.FormScreen
import pl.wsei.pam.lab06.ui.screens.ListScreen
import pl.wsei.pam.lab06.ui.screens.SettingsScreen
import android.util.Log
import pl.wsei.pam.lab06.ui.viewmodels.ListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.wsei.pam.lab06.ui.viewmodels.AppViewModelProvider
import pl.wsei.pam.lab06.MainActivity
import pl.wsei.pam.lab06.Lab06Activity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(context: TodoApplication? = null) {
    val navController = rememberNavController()
    
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true) {
        if (postNotificationPermission.status != PermissionStatus.Granted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    
    val viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
    
    LaunchedEffect(key1 = Unit) {
        try {
            val closestTask = viewModel.getTaskWithClosestDeadline()
            val alarmTime = viewModel.getAlarmTimeForTask(closestTask)
            
            if (closestTask != null && alarmTime != null) {
                (MainActivity.appContext as Lab06Activity).scheduleRepeatingAlarm(
                    time = alarmTime,
                    taskId = closestTask.id,
                    taskTitle = closestTask.title,
                    intervalMillis = 4 * 60 * 60 * 1000
                )
            }
        } catch (e: Exception) {
            Log.e("MainScreen", "Error setting up alarm", e)
        }
    }
    
    NavHost(navController = navController, startDestination = "list") {
        composable(route = "list") { 
            ListScreen(navController = navController) 
        }
        composable(
            route = "form/{taskId}",
            arguments = listOf(navArgument("taskId") { 
                type = NavType.IntType 
                defaultValue = -1
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            if (taskId != -1) {
                FormScreen(navController = navController, taskId = taskId)
            } else {
                FormScreen(navController = navController)
            }
        }
        composable(route = "form") { 
            FormScreen(navController = navController) 
        }
        composable(route = "settings") {
            SettingsScreen(navController = navController)
        }
    }
} 