package pl.wsei.pam.lab06

import android.content.Context
import android.app.NotificationManager
import androidx.core.app.NotificationCompat

class NotificationHandler(private val context: Context) {
    private val notificationManager = 
        context.getSystemService(NotificationManager::class.java)
    fun showSimpleNotification() {
        val notification = NotificationCompat.Builder(context, channelID)
            .setContentTitle("Proste powiadomienie")
            .setContentText("Tekst powiadomienia")
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationID, notification)
    }
} 