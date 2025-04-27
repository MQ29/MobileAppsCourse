package pl.wsei.pam.lab06.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import pl.wsei.pam.lab06.TodoTask
import pl.wsei.pam.lab06.data.repository.TodoTaskRepository
import java.time.LocalDate
import java.time.ZoneId

class ListViewModel(
    private val repository: TodoTaskRepository
) : ViewModel() {

    var tasks by mutableStateOf<List<TodoTask>>(emptyList())
        private set

    init {
        getAllTasks()
    }

    fun getAllTasks() {
        viewModelScope.launch {
            repository.getAllAsStream().collectLatest { tasksList ->
                tasks = tasksList
                checkAndUpdateAlarm()
            }
        }
    }

    private fun checkAndUpdateAlarm() {
    }

    fun deleteTask(task: TodoTask) {
        viewModelScope.launch {
            repository.deleteItem(task)
        }
    }
    
    fun getTaskWithClosestDeadline(): TodoTask? {
        return tasks
            .filter { !it.isDone }
            .filter { it.deadline.isAfter(LocalDate.now()) }
            .minByOrNull { it.deadline }
    }
    
    fun getAlarmTimeForTask(task: TodoTask?): Long? {
        if (task == null) return null
        
        val deadlineMillis = task.deadline
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
            
        val alarmTimeMillis = deadlineMillis - (24 * 60 * 60 * 1000)
        
        return alarmTimeMillis
    }

    val listUiState: StateFlow<ListUiState>
        get() {
            return repository.getAllAsStream().map { ListUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = ListUiState()
                )
        }
    
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ListUiState(val items: List<TodoTask> = listOf()) 