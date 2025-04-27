package pl.wsei.pam.lab06

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import pl.wsei.pam.lab06.data.AppContainer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            container = (this.application as TodoApplication).container
            appContext = applicationContext
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing container", e)
        }
    }
    
    companion object {
        lateinit var container: AppContainer
        lateinit var appContext: Context
    }
} 