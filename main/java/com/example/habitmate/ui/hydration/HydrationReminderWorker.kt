package com.example.habitmate.ui.hydration

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

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    companion object {
        private const val CHANNEL_ID = "hydration_reminder_channel"
        private const val NOTIFICATION_ID = 1001
        
        fun scheduleReminder(context: Context, intervalMinutes: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val reminderRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                intervalMinutes.toLong(), TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "hydration_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderRequest
            )
        }
        
        fun scheduleExactTimeReminder(context: Context, delayMinutes: Int) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            
            val exactTimeRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setInitialDelay(delayMinutes.toLong(), TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                "hydration_exact_time_reminder",
                ExistingWorkPolicy.REPLACE,
                exactTimeRequest
            )
        }
        
        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("hydration_reminder")
            WorkManager.getInstance(context).cancelUniqueWork("hydration_exact_time_reminder")
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
                "Hydration Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to drink water"
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
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("Don't forget to drink a glass of water")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}