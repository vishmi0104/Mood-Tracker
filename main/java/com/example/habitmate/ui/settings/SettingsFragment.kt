package com.example.habitmate.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.habitmate.R
import com.example.habitmate.data.DataManager
import com.example.habitmate.data.NotificationSettings
import com.example.habitmate.data.User
import com.example.habitmate.ui.auth.AuthActivity
import com.example.habitmate.ui.hydration.HydrationReminderWorker
import com.example.habitmate.ui.mood.MoodReminderWorker
import com.example.habitmate.widget.HabitProgressWidget

class SettingsFragment : Fragment() {
    
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var notificationsSwitch: Switch
    private lateinit var darkModeSwitch: Switch
    private lateinit var habitRemindersSwitch: Switch
    private lateinit var hydrationRemindersSwitch: Switch
    private lateinit var moodCheckinsSwitch: Switch
    private lateinit var achievementNotificationsSwitch: Switch
    private lateinit var exportDataButton: Button
    private lateinit var importDataButton: Button
    private lateinit var resetDataButton: Button
    private lateinit var aboutButton: Button
    private lateinit var logoutButton: Button
    private lateinit var versionTextView: TextView
    private lateinit var oceanSettingsInsightsButton: Button
    private lateinit var oceanSettingsTipsButton: Button
    private lateinit var gradientOceanPinkButton: Button
    
    private lateinit var dataManager: DataManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_enhanced, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        
        userNameTextView = view.findViewById(R.id.tv_user_name)
        userEmailTextView = view.findViewById(R.id.tv_user_email)
        notificationsSwitch = view.findViewById(R.id.switch_notifications)
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode)
        habitRemindersSwitch = view.findViewById(R.id.switch_habit_reminders)
        hydrationRemindersSwitch = view.findViewById(R.id.switch_hydration_reminders)
        moodCheckinsSwitch = view.findViewById(R.id.switch_mood_checkins)
        achievementNotificationsSwitch = view.findViewById(R.id.switch_achievement_notifications)
        exportDataButton = view.findViewById(R.id.btn_export_data)
        importDataButton = view.findViewById(R.id.btn_import_data)
        resetDataButton = view.findViewById(R.id.btn_reset_data)
        aboutButton = view.findViewById(R.id.btn_about)
        logoutButton = view.findViewById(R.id.btn_logout)
        versionTextView = view.findViewById(R.id.tv_version)
        oceanSettingsInsightsButton = view.findViewById(R.id.btn_ocean_settings_insights)
        oceanSettingsTipsButton = view.findViewById(R.id.btn_ocean_settings_tips)
        gradientOceanPinkButton = view.findViewById(R.id.btn_gradient_ocean_pink)
        
        setupViews()
        setupOceanButtons()
        setupGradientButton()
        loadUserData()
        loadSettings()
    }
    
    private fun setupOceanButtons() {
        oceanSettingsInsightsButton.setOnClickListener {
            showAppUsageInsights()
        }
        
        oceanSettingsTipsButton.setOnClickListener {
            showWellnessTips()
        }
    }
    
    private fun setupGradientButton() {
        gradientOceanPinkButton.setOnClickListener {
            // Show wellness motivation tips
            val wellnessTips = listOf(
                "Small consistent actions lead to big changes.",
                "Your mental health is just as important as physical health.",
                "It's okay to have off days - be kind to yourself.",
                "Progress, not perfection, is the goal.",
                "Taking care of yourself enables you to better care for others.",
                "Mindfulness can reduce stress and improve focus.",
                "Quality sleep is foundational to good health.",
                "Connecting with others boosts happiness and longevity.",
                "Regular movement, even light activity, benefits your body.",
                "Gratitude shifts your focus to what's going well."
            )
            
            val randomTip = wellnessTips.random()
            
            AlertDialog.Builder(requireContext())
                .setTitle("🌊 Ocean Wellness Boost 🌸")
                .setMessage("${randomTip}\n\nYou're doing great by taking steps toward better wellness. Remember that self-care isn't selfish - it's essential.")
                .setPositiveButton("Feeling Inspired!") { _, _ ->
                    Toast.makeText(context, "Wellness boost activated! ✨", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }
    
    private fun showAppUsageInsights() {
        val user = dataManager.getUser()
        val habits = dataManager.getHabits()
        val habitEntries = dataManager.getHabitEntries()
        val moodEntries = dataManager.getMoodEntries()
        val hydrationData = dataManager.getHydrationData()
        
        val accountAge = if (user != null) {
            val days = (System.currentTimeMillis() - user.createdAt) / (1000 * 60 * 60 * 24)
            "$days days"
        } else {
            "Unknown"
        }
        
        val totalHabits = habits.size
        val activeHabits = habits.count { it.isActive }
        val totalCompletions = habitEntries.count { it.isCompleted }
        val moodCount = moodEntries.size
        val totalWaterIntake = hydrationData.glassesToday
        
        // Prepare stats for modern dialog
        val stats = mapOf(
            "Account Age" to "$accountAge",
            "Total Habits Created" to "$totalHabits",
            "Active Habits" to "$activeHabits",
            "Total Habit Completions" to "$totalCompletions",
            "Mood Entries Recorded" to "$moodCount",
            "Water Glasses This Week" to "$totalWaterIntake"
        )
        
        val message = "Explore your HabitMate journey and discover how consistent wellness practices are shaping your life."
        
        com.example.habitmate.utils.ModernInsightDialog.Builder(requireContext())
            .setTitle("🌊 App Usage Insights")
            .setMessage(message)
            .setIcon(R.drawable.ic_settings)
            .setStats(stats)
            .setPositiveButton("OK")
            .show()
    }
    
    private fun showWellnessTips() {
        val tips = listOf(
            "Maintain a consistent sleep schedule for better health.",
            "Take short breaks every hour during work to reduce stress.",
            "Practice mindfulness for 5-10 minutes daily to improve focus.",
            "Set realistic goals and celebrate small achievements.",
            "Stay connected with friends and family for emotional wellness.",
            "Include physical activity in your daily routine, even if minimal.",
            "Limit screen time before bed for better sleep quality."
        )
        
        val randomTip = tips.random()
        
        AlertDialog.Builder(requireContext())
            .setTitle("🌊 Wellness Tips")
            .setMessage(randomTip)
            .setPositiveButton("Done") { _, _ ->
                Toast.makeText(context, "Wellness tip completed!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Skip", null)
            .show()
    }
    
    private fun setupViews() {
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationSetting(isChecked)
            if (!isChecked) {
                HydrationReminderWorker.cancelReminder(requireContext())
                MoodReminderWorker.cancelReminder(requireContext())
            }
        }
        
        habitRemindersSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveHabitRemindersSetting(isChecked)
        }
        
        hydrationRemindersSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveHydrationRemindersSetting(isChecked)
            if (isChecked) {
                val hydrationData = dataManager.getHydrationData()
                HydrationReminderWorker.scheduleReminder(requireContext(), hydrationData.reminderInterval)
            } else {
                HydrationReminderWorker.cancelReminder(requireContext())
            }
        }
        
        moodCheckinsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveMoodCheckinsSetting(isChecked)
            if (isChecked) {
                // Schedule mood check-in reminders (every 6 hours by default)
                MoodReminderWorker.scheduleReminder(requireContext(), 6)
            } else {
                MoodReminderWorker.cancelReminder(requireContext())
            }
        }
        
        achievementNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveAchievementNotificationsSetting(isChecked)
        }
        
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveDarkModeSetting(isChecked)
            Toast.makeText(context, "Dark mode setting saved. Restart app to apply changes.", Toast.LENGTH_LONG).show()
        }
        
        exportDataButton.setOnClickListener {
            exportUserData()
        }
        
        importDataButton.setOnClickListener {
            importUserData()
        }
        
        resetDataButton.setOnClickListener {
            showResetDataConfirmation()
        }
        
        aboutButton.setOnClickListener {
            showAboutDialog()
        }
        
        logoutButton.setOnClickListener {
            logout()
        }
        
        versionTextView.text = "Version 2.0.0"
    }
    
    private fun loadUserData() {
        val user = dataManager.getUser()
        if (user != null) {
            userNameTextView.text = user.name
            userEmailTextView.text = user.email
        }
    }
    
    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("habitmate_prefs", android.content.Context.MODE_PRIVATE)
        notificationsSwitch.isChecked = prefs.getBoolean("notifications_enabled", true)
        darkModeSwitch.isChecked = prefs.getBoolean("dark_mode_enabled", false)
        
        // Load notification settings
        val notificationSettings = dataManager.getNotificationSettings()
        habitRemindersSwitch.isChecked = notificationSettings.habitReminders
        hydrationRemindersSwitch.isChecked = notificationSettings.hydrationReminders
        moodCheckinsSwitch.isChecked = notificationSettings.moodCheckIns
        achievementNotificationsSwitch.isChecked = notificationSettings.achievementNotifications
    }
    
    private fun saveNotificationSetting(enabled: Boolean) {
        val prefs = requireContext().getSharedPreferences("habitmate_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        
        if (enabled) {
            val hydrationData = dataManager.getHydrationData()
            HydrationReminderWorker.scheduleReminder(requireContext(), hydrationData.reminderInterval)
            
            // Schedule mood check-in reminders
            MoodReminderWorker.scheduleReminder(requireContext(), 6)
        }
    }
    
    private fun saveHabitRemindersSetting(enabled: Boolean) {
        val notificationSettings = dataManager.getNotificationSettings()
        val updatedSettings = notificationSettings.copy(habitReminders = enabled)
        dataManager.saveNotificationSettings(updatedSettings)
    }
    
    private fun saveHydrationRemindersSetting(enabled: Boolean) {
        val notificationSettings = dataManager.getNotificationSettings()
        val updatedSettings = notificationSettings.copy(hydrationReminders = enabled)
        dataManager.saveNotificationSettings(updatedSettings)
    }
    
    private fun saveMoodCheckinsSetting(enabled: Boolean) {
        val notificationSettings = dataManager.getNotificationSettings()
        val updatedSettings = notificationSettings.copy(moodCheckIns = enabled)
        dataManager.saveNotificationSettings(updatedSettings)
    }
    
    private fun saveAchievementNotificationsSetting(enabled: Boolean) {
        val notificationSettings = dataManager.getNotificationSettings()
        val updatedSettings = notificationSettings.copy(achievementNotifications = enabled)
        dataManager.saveNotificationSettings(updatedSettings)
    }
    
    private fun saveDarkModeSetting(enabled: Boolean) {
        val prefs = requireContext().getSharedPreferences("habitmate_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }
    
    private fun exportUserData() {
        val user = dataManager.getUser()
        val habits = dataManager.getHabits()
        val moodEntries = dataManager.getMoodEntries()
        val hydrationData = dataManager.getHydrationData()
        
        val exportText = buildString {
            appendLine("=== HabitMate Data Export ===")
            appendLine("Export Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
            appendLine()
            
            appendLine("=== User Information ===")
            appendLine("Name: ${user?.name ?: "N/A"}")
            appendLine("Email: ${user?.email ?: "N/A"}")
            appendLine()
            
            appendLine("=== Habits (${habits.size}) ===")
            habits.forEach { habit ->
                appendLine("- ${habit.name}: ${habit.frequency.name} (${if (habit.isActive) "Active" else "Inactive"})")
            }
            appendLine()
            
            appendLine("=== Mood Entries (${moodEntries.size}) ===")
            moodEntries.forEach { entry ->
                appendLine("- ${entry.date}: ${entry.mood.emoji} ${entry.mood.name}${if (entry.note.isNotEmpty()) " - ${entry.note}" else ""}")
            }
            appendLine()
            
            appendLine("=== Hydration Data ===")
            appendLine("Daily Goal: ${hydrationData.goal} glasses")
            appendLine("Reminder Interval: ${hydrationData.reminderInterval} minutes")
            appendLine()
            
            appendLine("=== End of Export ===")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, exportText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Export HabitMate Data"))
    }
    
    private fun importUserData() {
        Toast.makeText(context, "Import feature coming soon! For now, you can manually add your data.", Toast.LENGTH_LONG).show()
    }
    
    private fun showResetDataConfirmation() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Reset All Data")
            .setMessage("Are you sure you want to reset all your data? This action cannot be undone.")
            .setPositiveButton("Reset") { _, _ ->
                resetAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun resetAllData() {
        val prefs = requireContext().getSharedPreferences("habitmate_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        HydrationReminderWorker.cancelReminder(requireContext())
        MoodReminderWorker.cancelReminder(requireContext())
        
        Toast.makeText(context, "All data has been reset!", Toast.LENGTH_SHORT).show()
        
        // Navigate to auth screen
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        requireActivity().finish()
    }
    
    private fun showAboutDialog() {
        val aboutText = """
            HabitMate v2.0.0
            
            Your comprehensive wellness companion app!
            
            Features:
            • Daily habit tracking with progress visualization
            • Mood journaling with trend analysis
            • Smart hydration reminders
            • Beautiful charts and insights
            • Home screen widget
            • Data export capabilities
            
            Built with ❤️ using Kotlin and Material Design 3
            
            © 2024 HabitMate. All rights reserved.
        """.trimIndent()
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("About HabitMate")
            .setMessage(aboutText)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun logout() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                dataManager.logout()
                HydrationReminderWorker.cancelReminder(requireContext())
                MoodReminderWorker.cancelReminder(requireContext())
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}