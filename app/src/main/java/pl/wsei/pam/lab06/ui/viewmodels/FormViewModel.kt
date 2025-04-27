package pl.wsei.pam.lab06.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.Priority
import pl.wsei.pam.lab06.TodoTask
import pl.wsei.pam.lab06.data.database.LocalDateConverter
import pl.wsei.pam.lab06.data.repository.TodoTaskRepository
import pl.wsei.pam.lab06.data.utils.CurrentDateProvider
import java.time.LocalDate
import pl.wsei.pam.lab06.Lab06Activity
import android.app.Application
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class FormViewModel(
    private val repository: TodoTaskRepository,
    private val dateProvider: CurrentDateProvider,
    application: Application
) : AndroidViewModel(application) {

    var todoTaskUiState by mutableStateOf(TodoTaskUiState())
        private set

    var isEditMode by mutableStateOf(false)
        private set

    suspend fun save() {
        if (validate()) {
            val task = todoTaskUiState.todoTask.toTodoTask()
            if (isEditMode) {
                repository.updateItem(task)
            } else {
                repository.insertItem(task)
            }
            
            try {
                pl.wsei.pam.lab06.MainActivity.container.notificationHandler.showSimpleNotification()
            } catch (e: Exception) {
                android.util.Log.e("FormViewModel", "Error showing notification", e)
            }
        }
    }

    fun updateUiState(todoTaskForm: TodoTaskForm) {
        todoTaskUiState = TodoTaskUiState(todoTask = todoTaskForm, isValid = validate(todoTaskForm))
    }

    private fun validate(uiState: TodoTaskForm = todoTaskUiState.todoTask): Boolean {
        return with(uiState) {
            title.isNotBlank() && LocalDateConverter.fromMillis(deadline).isAfter(dateProvider.currentDate)
        }
    }
    
    fun loadTask(id: Int) {
        viewModelScope.launch {
            try {
                val task = repository.getItemAsStream(id).filterNotNull().first()
                updateUiState(task.toTodoTaskForm())
                isEditMode = true
            } catch (e: Exception) {
                isEditMode = false
            }
        }
    }
}

data class TodoTaskUiState(
    var todoTask: TodoTaskForm = TodoTaskForm(),
    val isValid: Boolean = false
)

data class TodoTaskForm(
    val id: Int = 0,
    val title: String = "",
    val deadline: Long = LocalDateConverter.toMillis(LocalDate.now().plusDays(1)),
    val isDone: Boolean = false,
    val priority: String = Priority.Low.name
)

fun TodoTask.toTodoTaskUiState(isValid: Boolean = false): TodoTaskUiState = TodoTaskUiState(
    todoTask = this.toTodoTaskForm(),
    isValid = isValid
)

fun TodoTaskForm.toTodoTask(): TodoTask = TodoTask(
    id = id,
    title = title,
    deadline = LocalDateConverter.fromMillis(deadline),
    isDone = isDone,
    priority = Priority.valueOf(priority)
)

fun TodoTask.toTodoTaskForm(): TodoTaskForm = TodoTaskForm(
    id = id,
    title = title,
    deadline = LocalDateConverter.toMillis(deadline),
    isDone = isDone,
    priority = priority.name
) 