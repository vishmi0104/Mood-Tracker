package com.example.habitmate.ui.mood

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.habitmate.R
import com.example.habitmate.ui.main.MainActivity
import java.util.concurrent.TimeUnit

class MoodReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    companion object {
        private const val CHANNEL_ID = "mood_reminder_channel"
        private const val NOTIFICATION_ID = 1002
        
        fun scheduleReminder(context: Context, intervalHours: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val reminderRequest = PeriodicWorkRequestBuilder<MoodReminderWorker>(
                intervalHours.toLong(), TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.HOURS)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "mood_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderRequest
            )
        }
        
        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("mood_reminder")
        }
    }
    
    override fun doWork(): Result {
        return try {
            createNotificationChannel()
            showNotification()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mood Check-in Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to log your mood"
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification() {
        // Create an intent to open the app when the notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pass extra to indicate we want to go to the mood fragment
            putExtra("navigate_to", "mood")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mood)
            .setContentTitle("😊 How are you feeling today?")
            .setContentText("Take a moment to log your mood")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}