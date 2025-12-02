package com.example.habitmate.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.habitmate.R
import com.example.habitmate.data.DataManager
import com.example.habitmate.ui.main.MainActivity

class HabitProgressWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val dataManager = DataManager(context)
            val progress = dataManager.getTodayProgress()
            val progressPercentage = (progress * 100).toInt()
            
            val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
            
            // Update progress text
            views.setTextViewText(R.id.widget_progress_text, "$progressPercentage%")
            
            // Update progress bar
            views.setProgressBar(R.id.widget_progress_bar, 100, progressPercentage, false)
            
            // Set click intent
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

