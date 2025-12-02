package com.example.habitmate.ui.hydration

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.habitmate.R
import com.example.habitmate.ui.main.MainActivity

class HydrationReminderReceiver : BroadcastReceiver() {
    
    companion object {
        private const val CHANNEL_ID = "hydration_exact_reminder_channel"
        private const val NOTIFICATION_ID = 1003
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        showNotification(context)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hydration Exact Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Exact time reminders to drink water"
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification(context: Context) {
        // Create an intent to open the app when the notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("It's time for your scheduled water intake")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}