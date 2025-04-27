package pl.wsei.pam.lab06.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.ui.AppTopBar
import pl.wsei.pam.lab06.ui.TodoTaskInputBody
import pl.wsei.pam.lab06.ui.viewmodels.AppViewModelProvider
import pl.wsei.pam.lab06.ui.viewmodels.FormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    taskId: Int? = null,
    viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(taskId) {
        if (taskId != null && taskId > 0) {
            viewModel.loadTask(taskId)
        }
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = if (viewModel.isEditMode) "Edytuj zadanie" else "Dodaj nowe zadanie",
                navController = navController,
                canNavigateBack = true
            )
        }
    ) { paddingValues ->
        TodoTaskInputBody(
            todoUiState = viewModel.todoTaskUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(paddingValues),
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.save()
                    navController.navigateUp()
                }
            }
        )
    }
} 