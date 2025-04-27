package pl.wsei.pam.lab06.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.wsei.pam.lab06.MainActivity
import pl.wsei.pam.lab06.data.repository.TodoTaskRepository

object AppViewModelProvider {
    
    val Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val container = MainActivity.container
            val repository = container.todoRepository
            val dateProvider = container.currentDateProvider
            val application = MainActivity.appContext as Application

            if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
                return FormViewModel(repository, dateProvider, application) as T
            }

            if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
                return ListViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 