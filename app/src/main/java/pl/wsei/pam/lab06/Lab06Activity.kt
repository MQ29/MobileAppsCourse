package pl.wsei.pam.lab06

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column    
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import java.time.LocalDate
import pl.wsei.pam.lab06.ui.screens.FormScreen
import pl.wsei.pam.lab06.ui.screens.ListScreen
import pl.wsei.pam.lab06.ui.screens.SettingsScreen
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import pl.wsei.pam.lab06.ui.MainScreen
import pl.wsei.pam.lab06.ui.viewmodels.AppViewModelProvider
import pl.wsei.pam.lab06.ui.viewmodels.ListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.wsei.pam.lab06.data.AppContainer
import pl.wsei.pam.lab06.ui.theme.Lab06Theme

const val notificationID = 121
const val channelID = "Lab06 channel"
const val titleExtra = "title"
const val messageExtra = "message"

class Lab06Activity : ComponentActivity() {

    private var currentAlarmPendingIntent: PendingIntent? = null
    
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        createNotificationChannel()
        
        try {
            MainActivity.container = (this.application as TodoApplication).container
            MainActivity.appContext = applicationContext
            
            scheduleAlarm(2_000)
            
        } catch (e: Exception) {
            Log.e("Lab06Activity", "Error in initialization", e)
        }
        
        setContent {
            Lab06Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
    
    private fun createNotificationChannel() {
        val name = "Lab06 channel"
        val descriptionText = "Lab06 is channel for notifications for approaching tasks."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    fun setupTaskAlarm() {
        try {
            val viewModel = ListViewModel(MainActivity.container.todoRepository)
            val closestTask = viewModel.getTaskWithClosestDeadline()
            val alarmTime = viewModel.getAlarmTimeForTask(closestTask)
            
            if (closestTask != null && alarmTime != null) {
                cancelCurrentAlarm()
                
                scheduleRepeatingAlarm(
                    time = alarmTime,
                    taskId = closestTask.id,
                    taskTitle = closestTask.title,
                    intervalMillis = 4 * 60 * 60 * 1000
                )
            }
        } catch (e: Exception) {
            Log.e("Lab06Activity", "Error setting up alarm", e)
        }
    }
    
    private fun cancelCurrentAlarm() {
        currentAlarmPendingIntent?.let { pendingIntent ->
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }
    }
    
    fun scheduleRepeatingAlarm(time: Long, taskId: Int, taskTitle: String, intervalMillis: Long) {
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        intent.putExtra(titleExtra, "Termin zadania zbliża się!")
        intent.putExtra(messageExtra, "Zadanie '$taskTitle' wkrótce wymaga ukończenia")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        currentAlarmPendingIntent = pendingIntent

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    intervalMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                time,
                intervalMillis,
                pendingIntent
            )
        }
    }

    fun scheduleAlarm(time: Long){
        Log.d("Lab06Activity", "Ustawianie alarmu za ${time} ms")
        
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        intent.putExtra(titleExtra, "Deadline")
        intent.putExtra(messageExtra, "Zbliża się termin zakończenia zadania")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        android.os.Handler(mainLooper).postDelayed({
            Log.d("Lab06Activity", "Handler uruchomiony po ${time} ms")
            try {
                val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, channelID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Bezpośrednie powiadomienie")
                    .setContentText("To powiadomienie zostało wysłane bezpośrednio z aktywności")
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .build()
                
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
                notificationManager.notify(notificationID + 1, notification)
                
                Log.d("Lab06Activity", "Wysłano bezpośrednie powiadomienie")
            } catch (e: Exception) {
                Log.e("Lab06Activity", "Błąd przy wysyłaniu powiadomienia", e)
            }
        }, time)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        
        Log.d("Lab06Activity", "Alarm zostanie uruchomiony o: ${System.currentTimeMillis() + time} ms")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + time,
            pendingIntent
        )
        Log.d("Lab06Activity", "Alarm ustawiony pomyślnie")
    }
    
    companion object {
        lateinit var container: AppContainer
        lateinit var appContext: Context
    }
}

@Composable
fun TodoTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") { ListScreen(navController = navController) }
        composable("form") { FormScreen(navController = navController) }
        composable(
            route = "form/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            FormScreen(navController = navController, taskId = taskId)
        }
        composable("settings") { SettingsScreen(navController = navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabAppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean,
    route: String
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navController.navigate(route) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (route !== "form") {
                OutlinedButton(
                    onClick = { navController.navigate("list") }
                )
                {
                    Text(
                        text = "Zapisz",
                        fontSize = 18.sp
                    )
                }
            } else {
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "")
                }
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "")
                }
            }
        }
    )
}

enum class Priority {
    High, Medium, Low
}

data class TodoTask(
    val id: Int = 0,
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean,
    val priority: Priority
)

fun todoTasks(): List<TodoTask> {
    return listOf(
        TodoTask(
            id = 1,
            title = "Programming", 
            deadline = LocalDate.of(2024, 4, 18), 
            isDone = false, 
            priority = Priority.Low
        ),
        TodoTask(
            id = 2,
            title = "Teaching", 
            deadline = LocalDate.of(2024, 5, 12), 
            isDone = false, 
            priority = Priority.High
        ),
        TodoTask(
            id = 3,
            title = "Learning", 
            deadline = LocalDate.of(2024, 6, 28), 
            isDone = true, 
            priority = Priority.Low
        ),
        TodoTask(
            id = 4,
            title = "Cooking", 
            deadline = LocalDate.of(2024, 8, 18), 
            isDone = false, 
            priority = Priority.Medium
        )
    )
}

@Composable
fun ListItem(item: TodoTask, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxSize()
            .heightIn(120.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                
                val priorityColor = when(item.priority) {
                    Priority.High -> MaterialTheme.colorScheme.error
                    Priority.Medium -> MaterialTheme.colorScheme.tertiary
                    Priority.Low -> MaterialTheme.colorScheme.primary
                }
                
                Text(
                    text = item.priority.toString(),
                    color = priorityColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Spacer(modifier = Modifier.padding(8.dp))
            
            Row {
                Text(
                    text = "Deadline: ${item.deadline}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = if (item.isDone) "Completed" else "Pending",
                    color = if (item.isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task",
                        modifier = Modifier.scale(1.5f)
                    )
                },
                onClick = {
                    navController.navigate("form")
                }
            )
        },
        topBar = {
            LabAppTopBar(
                navController = navController,
                title = "List",
                showBackIcon = false,
                route = "form"
            )
        },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(items = todoTasks()) { item ->
                    ListItem(item = item)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavController, taskId: Int?) {
    var taskTitle by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var selectedPriority by remember { mutableStateOf(Priority.Medium) }
    var isCompleted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LabAppTopBar(
                navController = navController,
                title = "Form",
                showBackIcon = true,
                route = "list"
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.padding(8.dp))
                
                DatePickerDialogSample(onDateSelected = { selectedDate = it })
                
                Spacer(modifier = Modifier.padding(8.dp))
                
                Text("Priority", style = MaterialTheme.typography.bodyLarge)
                
                Row {
                    RadioButton(
                        selected = selectedPriority == Priority.High,
                        onClick = { selectedPriority = Priority.High }
                    )
                    Text("High", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                    
                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    RadioButton(
                        selected = selectedPriority == Priority.Medium,
                        onClick = { selectedPriority = Priority.Medium }
                    )
                    Text("Medium", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                    
                    Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    RadioButton(
                        selected = selectedPriority == Priority.Low,
                        onClick = { selectedPriority = Priority.Low }
                    )
                    Text("Low", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                }
                
                Spacer(modifier = Modifier.padding(8.dp))
                
                Row {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                    Text("Task completed", modifier = Modifier.padding(start = 8.dp, top = 14.dp))
                }
                
                Spacer(modifier = Modifier.padding(16.dp))
                
                Button(
                    onClick = {
                        navController.navigate("list")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Task")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TodoTheme {
        MainScreen()
    }
} 