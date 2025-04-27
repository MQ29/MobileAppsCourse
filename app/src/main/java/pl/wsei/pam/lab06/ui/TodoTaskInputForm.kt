package pl.wsei.pam.lab06.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.wsei.pam.lab06.Priority
import pl.wsei.pam.lab06.data.database.LocalDateConverter
import pl.wsei.pam.lab06.ui.viewmodels.TodoTaskForm
import pl.wsei.pam.lab06.ui.viewmodels.TodoTaskUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Tytuł zadania", style = MaterialTheme.typography.bodyLarge)
        TextField(
            value = item.title,
            onValueChange = {
                onValueChange(item.copy(title = it))
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val datePickerState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            yearRange = IntRange(2000, 2030),
            initialSelectedDateMillis = item.deadline,
        )
        var showDialog by remember {
            mutableStateOf(false)
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    showDialog = true
                }),
            text = "Deadline: ${LocalDateConverter.fromMillis(item.deadline)}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )
        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    showDialog = false
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        datePickerState.selectedDateMillis?.let {
                            onValueChange(item.copy(deadline = it))
                        }
                    }) {
                        Text("Wybierz")
                    }
                }
            ) {
                DatePicker(state = datePickerState, showModeToggle = true)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Priorytet", style = MaterialTheme.typography.bodyLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = item.priority == Priority.High.name,
                    onClick = { onValueChange(item.copy(priority = Priority.High.name)) }
                )
                Text("Wysoki")
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = item.priority == Priority.Medium.name,
                    onClick = { onValueChange(item.copy(priority = Priority.Medium.name)) }
                )
                Text("Średni")
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButton(
                    selected = item.priority == Priority.Low.name,
                    onClick = { onValueChange(item.copy(priority = Priority.Low.name)) }
                )
                Text("Niski")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { onValueChange(item.copy(isDone = it)) }
            )
            Text("Zadanie ukończone", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TodoTaskInputBody(
    todoUiState: TodoTaskUiState,
    onItemValueChange: (TodoTaskForm) -> Unit,
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoTaskInputForm(
            item = todoUiState.todoTask,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Zapisz")
        }
    }
} 