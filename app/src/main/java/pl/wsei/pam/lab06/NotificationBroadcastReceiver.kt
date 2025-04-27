package pl.wsei.pam.lab06

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import androidx.core.app.NotificationCompat

class NotificationBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        android.util.Log.d("NotificationReceiver", "Odebrano broadcast intent")
        
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(intent?.getStringExtra(titleExtra)) 
            .setContentText(intent?.getStringExtra(messageExtra))
            .build()
            
        android.util.Log.d("NotificationReceiver", "Utworzono notification: ${intent?.getStringExtra(titleExtra)}")
        
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
        
        android.util.Log.d("NotificationReceiver", "Powiadomienie wysłane")
    }
} 