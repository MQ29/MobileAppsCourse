package pl.wsei.pam.lab06.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import android.util.Log
import pl.wsei.pam.lab06.MainActivity

@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentDestination?.route ?: ""
    
    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Wróć"
                    )
                }
            }
        },
        actions = {
            if (!currentRoute.startsWith("form")) {
                IconButton(
                    onClick = {
                        try {
                            MainActivity.container.notificationHandler.showSimpleNotification()
                            
                            navController.navigate("settings") {
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            Log.e("AppTopBar", "Błąd przy wysyłaniu powiadomienia", e)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Ustawienia"
                    )
                }
                
                if (currentRoute != "list") {
                    IconButton(
                        onClick = {
                            navController.navigate("list") {
                                popUpTo("list") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Strona główna"
                        )
                    }
                }
            }
        }
    )
} 