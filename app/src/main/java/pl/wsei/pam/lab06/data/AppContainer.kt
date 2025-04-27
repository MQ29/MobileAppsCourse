package pl.wsei.pam.lab06.data

import android.content.Context
import pl.wsei.pam.lab06.data.database.AppDatabase
import pl.wsei.pam.lab06.data.repository.DatabaseTodoTaskRepository
import pl.wsei.pam.lab06.data.repository.TodoTaskRepository
import pl.wsei.pam.lab06.data.utils.CurrentDateProvider
import pl.wsei.pam.lab06.data.utils.SystemCurrentDateProvider
import pl.wsei.pam.lab06.NotificationHandler

interface AppContainer {
    val todoRepository: TodoTaskRepository
    val currentDateProvider: CurrentDateProvider
    val notificationHandler: NotificationHandler
}

class AppDataContainer(private val context: Context): AppContainer {
    override val todoRepository: TodoTaskRepository by lazy {
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())
    }
    
    override val currentDateProvider: CurrentDateProvider by lazy {
        SystemCurrentDateProvider()
    }
    
    override val notificationHandler: NotificationHandler by lazy {
        NotificationHandler(context)
    }
} 