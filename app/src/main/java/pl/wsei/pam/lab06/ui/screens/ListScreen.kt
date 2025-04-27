package pl.wsei.pam.lab06.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.Priority
import pl.wsei.pam.lab06.TodoTask
import pl.wsei.pam.lab06.ui.AppTopBar
import pl.wsei.pam.lab06.ui.viewmodels.AppViewModelProvider
import pl.wsei.pam.lab06.ui.viewmodels.ListViewModel
import androidx.compose.foundation.layout.Arrangement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Dodaj zadanie",
                        modifier = Modifier.scale(1.5f)
                    )
                },
                onClick = {
                    navController.navigate("form")
                }
            )
        },
        topBar = {
            AppTopBar(
                title = "Lista zadań",
                navController = navController,
                canNavigateBack = false
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(
                    items = listUiState.items,
                    key = { it.id }
                ) { task ->
                    TaskItemWithActions(
                        task = task,
                        onDelete = {
                            coroutineScope.launch {
                                viewModel.deleteTask(task)
                            }
                        },
                        onEdit = {
                            navController.navigate("form/${task.id}")
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TaskItemWithActions(
    task: TodoTask,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .clickable { onEdit() }
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                
                val priorityColor = when(task.priority) {
                    Priority.High -> MaterialTheme.colorScheme.error
                    Priority.Medium -> MaterialTheme.colorScheme.tertiary
                    Priority.Low -> MaterialTheme.colorScheme.primary
                }
                
                Text(
                    text = task.priority.toString(),
                    color = priorityColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deadline: ${task.deadline}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (task.isDone) "Ukończone" else "W trakcie",
                        color = if (task.isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    androidx.compose.material3.IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Usuń",
                            tint = Color.Red,
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                }
            }
        }
    }
} 