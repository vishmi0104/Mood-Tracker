package com.example.habitmate.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DataManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("habitmate_prefs", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // User Management
    fun saveUser(user: User) {
        val userJson = JSONObject().apply {
            put("id", user.id)
            put("name", user.name)
            put("email", user.email)
            put("createdAt", user.createdAt)
        }
        prefs.edit().putString("user", userJson.toString()).apply()
    }
    
    fun getUser(): User? {
        val userJson = prefs.getString("user", null) ?: return null
        return try {
            val json = JSONObject(userJson)
            User(
                id = json.getString("id"),
                name = json.getString("name"),
                email = json.getString("email"),
                createdAt = json.getLong("createdAt")
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun isLoggedIn(): Boolean = getUser() != null
    
    fun logout() {
        prefs.edit().remove("user").apply()
    }
    
    // Habit Management
    fun saveHabits(habits: List<Habit>) {
        val habitsArray = JSONArray()
        habits.forEach { habit ->
            val habitJson = JSONObject().apply {
                put("id", habit.id)
                put("name", habit.name)
                put("description", habit.description)
                put("frequency", habit.frequency.name)
                put("createdAt", habit.createdAt)
                put("isActive", habit.isActive)
            }
            habitsArray.put(habitJson)
        }
        prefs.edit().putString("habits", habitsArray.toString()).apply()
    }
    
    fun getHabits(): List<Habit> {
        val habitsJson = prefs.getString("habits", null) ?: return emptyList()
        return try {
            val habitsArray = JSONArray(habitsJson)
            val habits = mutableListOf<Habit>()
            for (i in 0 until habitsArray.length()) {
                val habitJson = habitsArray.getJSONObject(i)
                habits.add(
                    Habit(
                        id = habitJson.getString("id"),
                        name = habitJson.getString("name"),
                        description = habitJson.getString("description"),
                        frequency = HabitFrequency.valueOf(habitJson.getString("frequency")),
                        createdAt = habitJson.getLong("createdAt"),
                        isActive = habitJson.getBoolean("isActive")
                    )
                )
            }
            habits
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Habit Entries Management
    fun saveHabitEntries(entries: List<HabitEntry>) {
        val entriesArray = JSONArray()
        entries.forEach { entry ->
            val entryJson = JSONObject().apply {
                put("id", entry.id)
                put("habitId", entry.habitId)
                put("date", entry.date)
                put("isCompleted", entry.isCompleted)
                put("completedAt", entry.completedAt ?: 0)
            }
            entriesArray.put(entryJson)
        }
        prefs.edit().putString("habit_entries", entriesArray.toString()).apply()
    }
    
    fun getHabitEntries(): List<HabitEntry> {
        val entriesJson = prefs.getString("habit_entries", null) ?: return emptyList()
        return try {
            val entriesArray = JSONArray(entriesJson)
            val entries = mutableListOf<HabitEntry>()
            for (i in 0 until entriesArray.length()) {
                val entryJson = entriesArray.getJSONObject(i)
                entries.add(
                    HabitEntry(
                        id = entryJson.getString("id"),
                        habitId = entryJson.getString("habitId"),
                        date = entryJson.getString("date"),
                        isCompleted = entryJson.getBoolean("isCompleted"),
                        completedAt = if (entryJson.getLong("completedAt") > 0) entryJson.getLong("completedAt") else null
                    )
                )
            }
            entries
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getHabitEntriesForDate(date: String): List<HabitEntry> {
        return getHabitEntries().filter { it.date == date }
    }
    
    fun getHabitEntriesForHabit(habitId: String): List<HabitEntry> {
        return getHabitEntries().filter { it.habitId == habitId }
    }
    
    // Mood Entries Management
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val entriesArray = JSONArray()
        entries.forEach { entry ->
            val entryJson = JSONObject().apply {
                put("id", entry.id)
                put("mood", entry.mood.name)
                put("note", entry.note)
                put("date", entry.date)
                put("timestamp", entry.timestamp)
            }
            entriesArray.put(entryJson)
        }
        prefs.edit().putString("mood_entries", entriesArray.toString()).apply()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        val entriesJson = prefs.getString("mood_entries", null) ?: return emptyList()
        return try {
            val entriesArray = JSONArray(entriesJson)
            val entries = mutableListOf<MoodEntry>()
            for (i in 0 until entriesArray.length()) {
                val entryJson = entriesArray.getJSONObject(i)
                entries.add(
                    MoodEntry(
                        id = entryJson.getString("id"),
                        mood = MoodType.valueOf(entryJson.getString("mood")),
                        note = entryJson.getString("note"),
                        date = entryJson.getString("date"),
                        timestamp = entryJson.getLong("timestamp")
                    )
                )
            }
            entries
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getMoodEntriesForDate(date: String): List<MoodEntry> {
        return getMoodEntries().filter { it.date == date }
    }
    
    // Hydration Data Management
    fun saveHydrationData(data: HydrationData) {
        val dataJson = JSONObject().apply {
            put("glassesToday", data.glassesToday)
            put("goal", data.goal)
            put("reminderInterval", data.reminderInterval)
            put("lastReminderTime", data.lastReminderTime)
            put("customIntakeMl", data.customIntakeMl)
        }
        prefs.edit().putString("hydration_data", dataJson.toString()).apply()
    }
    
    fun getHydrationData(): HydrationData {
        val dataJson = prefs.getString("hydration_data", null) ?: return HydrationData()
        return try {
            val json = JSONObject(dataJson)
            HydrationData(
                glassesToday = json.getInt("glassesToday"),
                goal = json.getInt("goal"),
                reminderInterval = json.getInt("reminderInterval"),
                lastReminderTime = json.getLong("lastReminderTime"),
                customIntakeMl = json.optInt("customIntakeMl", 250)
            )
        } catch (e: Exception) {
            HydrationData()
        }
    }
    
    // Utility Methods
    fun getCurrentDate(): String = dateFormat.format(Date())
    
    fun generateId(): String = UUID.randomUUID().toString()
    
    fun getTodayProgress(): Float {
        val today = getCurrentDate()
        val habits = getHabits().filter { it.isActive }
        val entries = getHabitEntriesForDate(today)
        
        if (habits.isEmpty()) return 0f
        
        val completedCount = entries.count { it.isCompleted }
        return completedCount.toFloat() / habits.size
    }
    
    fun resetDailyData() {
        val today = getCurrentDate()
        val hydrationData = getHydrationData()
        saveHydrationData(hydrationData.copy(glassesToday = 0))
    }
    
    // Enhanced Habit Management
    fun updateHabitStreak(habitId: String, isCompleted: Boolean) {
        val habits = getHabits().toMutableList()
        val habitIndex = habits.indexOfFirst { it.id == habitId }
        if (habitIndex != -1) {
            val habit = habits[habitIndex]
            val updatedHabit = if (isCompleted) {
                habit.copy(
                    streak = habit.streak + 1,
                    bestStreak = maxOf(habit.bestStreak, habit.streak + 1),
                    totalCompletions = habit.totalCompletions + 1
                )
            } else {
                habit.copy(streak = 0)
            }
            habits[habitIndex] = updatedHabit
            saveHabits(habits)
        }
    }
    
    // Achievement Management
    fun saveAchievements(achievements: List<Achievement>) {
        val achievementsArray = JSONArray()
        achievements.forEach { achievement ->
            val achievementJson = JSONObject().apply {
                put("id", achievement.id)
                put("title", achievement.title)
                put("description", achievement.description)
                put("icon", achievement.icon)
                put("type", achievement.type.name)
                put("requirement", achievement.requirement)
                put("isUnlocked", achievement.isUnlocked)
                put("unlockedAt", achievement.unlockedAt ?: 0)
                put("category", achievement.category.name)
            }
            achievementsArray.put(achievementJson)
        }
        prefs.edit().putString("achievements", achievementsArray.toString()).apply()
    }
    
    fun getAchievements(): List<Achievement> {
        val achievementsJson = prefs.getString("achievements", null) ?: return getDefaultAchievements()
        return try {
            val achievementsArray = JSONArray(achievementsJson)
            val achievements = mutableListOf<Achievement>()
            for (i in 0 until achievementsArray.length()) {
                val achievementJson = achievementsArray.getJSONObject(i)
                achievements.add(
                    Achievement(
                        id = achievementJson.getString("id"),
                        title = achievementJson.getString("title"),
                        description = achievementJson.getString("description"),
                        icon = achievementJson.getString("icon"),
                        type = AchievementType.valueOf(achievementJson.getString("type")),
                        requirement = achievementJson.getInt("requirement"),
                        isUnlocked = achievementJson.getBoolean("isUnlocked"),
                        unlockedAt = if (achievementJson.getLong("unlockedAt") > 0) achievementJson.getLong("unlockedAt") else null,
                        category = AchievementCategory.valueOf(achievementJson.getString("category"))
                    )
                )
            }
            achievements
        } catch (e: Exception) {
            getDefaultAchievements()
        }
    }
    
    private fun getDefaultAchievements(): List<Achievement> {
        return listOf(
            Achievement("first_habit", "First Steps", "Complete your first habit", "🎯", AchievementType.COMPLETION, 1, false, null, AchievementCategory.HABITS),
            Achievement("week_streak", "Week Warrior", "Complete habits for 7 days straight", "🔥", AchievementType.STREAK, 7, false, null, AchievementCategory.HABITS),
            Achievement("month_streak", "Monthly Master", "Complete habits for 30 days straight", "👑", AchievementType.STREAK, 30, false, null, AchievementCategory.HABITS),
            Achievement("mood_tracker", "Mood Master", "Log your mood for 7 days", "😊", AchievementType.CONSISTENCY, 7, false, null, AchievementCategory.MOOD),
            Achievement("hydration_hero", "Hydration Hero", "Meet your daily hydration goal for 7 days", "💧", AchievementType.CONSISTENCY, 7, false, null, AchievementCategory.HYDRATION),
            Achievement("habit_100", "Century Club", "Complete 100 habits total", "💯", AchievementType.MILESTONE, 100, false, null, AchievementCategory.HABITS)
        )
    }
    
    fun unlockAchievement(achievementId: String) {
        val achievements = getAchievements().toMutableList()
        val achievementIndex = achievements.indexOfFirst { it.id == achievementId }
        if (achievementIndex != -1 && !achievements[achievementIndex].isUnlocked) {
            achievements[achievementIndex] = achievements[achievementIndex].copy(
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
            saveAchievements(achievements)
        }
    }
    
    // Daily Quotes Management
    fun saveDailyQuote(quote: DailyQuote) {
        val quoteJson = JSONObject().apply {
            put("id", quote.id)
            put("text", quote.text)
            put("author", quote.author)
            put("category", quote.category.name)
            put("date", quote.date)
        }
        prefs.edit().putString("daily_quote", quoteJson.toString()).apply()
    }
    
    fun getDailyQuote(): DailyQuote? {
        val quoteJson = prefs.getString("daily_quote", null) ?: return null
        return try {
            val json = JSONObject(quoteJson)
            DailyQuote(
                id = json.getString("id"),
                text = json.getString("text"),
                author = json.getString("author"),
                category = QuoteCategory.valueOf(json.getString("category")),
                date = json.getString("date")
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun getRandomQuote(): DailyQuote {
        val quotes = listOf(
            DailyQuote("1", "The secret of getting ahead is getting started.", "Mark Twain", QuoteCategory.MOTIVATION, getCurrentDate()),
            DailyQuote("2", "Success is the sum of small efforts repeated day in and day out.", "Robert Collier", QuoteCategory.SUCCESS, getCurrentDate()),
            DailyQuote("3", "The present moment is the only time over which we have dominion.", "Thich Nhat Hanh", QuoteCategory.MINDFULNESS, getCurrentDate()),
            DailyQuote("4", "Take care of your body. It's the only place you have to live.", "Jim Rohn", QuoteCategory.HEALTH, getCurrentDate()),
            DailyQuote("5", "Happiness is not something ready made. It comes from your own actions.", "Dalai Lama", QuoteCategory.HAPPINESS, getCurrentDate())
        )
        return quotes.random()
    }
    
    // Analytics Management
    fun getAnalyticsData(): AnalyticsData {
        val habits = getHabits()
        val habitEntries = getHabitEntries()
        val moodEntries = getMoodEntries()
        val hydrationData = getHydrationData()
        
        val today = getCurrentDate()
        val weekAgo = getDateDaysAgo(7)
        
        // Calculate weekly habit completion
        val weeklyHabitEntries = habitEntries.filter { it.date >= weekAgo }
        val weeklyHabits = habits.filter { it.isActive }
        val weeklyCompletion = if (weeklyHabits.isNotEmpty()) {
            weeklyHabitEntries.count { it.isCompleted }.toFloat() / (weeklyHabits.size * 7)
        } else 0f
        
        // Calculate weekly mood average
        val weeklyMoodEntries = moodEntries.filter { it.date >= weekAgo }
        val weeklyMoodAverage = if (weeklyMoodEntries.isNotEmpty()) {
            weeklyMoodEntries.map { it.intensity }.average().toFloat()
        } else 0f
        
        // Calculate weekly hydration average
        val weeklyHydrationAverage = hydrationData.averageGlassesPerDay
        
        // Calculate streaks
        val currentStreak = calculateCurrentStreak()
        val longestStreak = habits.maxOfOrNull { it.bestStreak } ?: 0
        
        // Most frequent mood
        val mostFrequentMood = moodEntries.groupBy { it.mood }
            .maxByOrNull { it.value.size }?.key ?: MoodType.NEUTRAL
        
        // Habits by category
        val habitsByCategory = habits.groupBy { it.category }
            .mapValues { it.value.size }
        
        // Mood distribution
        val moodDistribution = moodEntries.groupBy { it.mood }
            .mapValues { it.value.size }
        
        return AnalyticsData(
            weeklyHabitCompletion = weeklyCompletion,
            weeklyMoodAverage = weeklyMoodAverage,
            weeklyHydrationAverage = weeklyHydrationAverage,
            totalHabitsCompleted = habitEntries.count { it.isCompleted },
            totalDaysActive = habitEntries.map { it.date }.distinct().size,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            mostFrequentMood = mostFrequentMood,
            habitsByCategory = habitsByCategory,
            moodDistribution = moodDistribution
        )
    }
    
    private fun calculateCurrentStreak(): Int {
        val habits = getHabits().filter { it.isActive }
        val habitEntries = getHabitEntries()
        val today = getCurrentDate()
        
        var streak = 0
        var currentDate = today
        
        while (true) {
            val dayEntries = habitEntries.filter { it.date == currentDate }
            val completedHabits = dayEntries.count { it.isCompleted }
            
            if (completedHabits >= habits.size) {
                streak++
                currentDate = getDateDaysAgo(1, currentDate)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun getDateDaysAgo(days: Int, fromDate: String = getCurrentDate()): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.time = dateFormat.parse(fromDate) ?: Date()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return dateFormat.format(calendar.time)
    }
    
    // Widget Data Management
    fun getWidgetData(): WidgetData {
        val today = getCurrentDate()
        val habits = getHabits().filter { it.isActive }
        val habitEntries = getHabitEntriesForDate(today)
        val hydrationData = getHydrationData()
        val moodEntries = getMoodEntriesForDate(today)
        
        val habitCompletionPercentage = if (habits.isNotEmpty()) {
            habitEntries.count { it.isCompleted }.toFloat() / habits.size
        } else 0f
        
        val hydrationPercentage = hydrationData.goal.toFloat().let { goal ->
            if (goal > 0) hydrationData.glassesToday.toFloat() / goal else 0f
        }
        
        val todayMood = moodEntries.maxByOrNull { it.timestamp }?.mood
        
        val nextHabit = habits.firstOrNull { habit ->
            !habitEntries.any { it.habitId == habit.id && it.isCompleted }
        }?.name ?: ""
        
        return WidgetData(
            habitCompletionPercentage = habitCompletionPercentage,
            hydrationPercentage = hydrationPercentage,
            todayMood = todayMood,
            nextHabit = nextHabit,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    // Notification Settings Management
    fun saveNotificationSettings(settings: NotificationSettings) {
        val settingsJson = JSONObject().apply {
            put("habitReminders", settings.habitReminders)
            put("hydrationReminders", settings.hydrationReminders)
            put("moodCheckIns", settings.moodCheckIns)
            put("achievementNotifications", settings.achievementNotifications)
            put("weeklyReports", settings.weeklyReports)
            put("quietHoursStart", settings.quietHoursStart)
            put("quietHoursEnd", settings.quietHoursEnd)
            put("soundEnabled", settings.soundEnabled)
            put("vibrationEnabled", settings.vibrationEnabled)
        }
        prefs.edit().putString("notification_settings", settingsJson.toString()).apply()
    }
    
    fun getNotificationSettings(): NotificationSettings {
        val settingsJson = prefs.getString("notification_settings", null) ?: return NotificationSettings()
        return try {
            val json = JSONObject(settingsJson)
            NotificationSettings(
                habitReminders = json.getBoolean("habitReminders"),
                hydrationReminders = json.getBoolean("hydrationReminders"),
                moodCheckIns = json.getBoolean("moodCheckIns"),
                achievementNotifications = json.getBoolean("achievementNotifications"),
                weeklyReports = json.getBoolean("weeklyReports"),
                quietHoursStart = json.getInt("quietHoursStart"),
                quietHoursEnd = json.getInt("quietHoursEnd"),
                soundEnabled = json.getBoolean("soundEnabled"),
                vibrationEnabled = json.getBoolean("vibrationEnabled")
            )
        } catch (e: Exception) {
            NotificationSettings()
        }
    }
    
    // User Preferences Management
    fun saveUserPreferences(preferences: UserPreferences) {
        val prefsJson = JSONObject().apply {
            put("theme", preferences.theme.name)
            put("hydrationGoal", preferences.hydrationGoal)
            put("reminderInterval", preferences.reminderInterval)
            put("soundEnabled", preferences.soundEnabled)
            put("vibrationEnabled", preferences.vibrationEnabled)
            put("notificationsEnabled", preferences.notificationsEnabled)
            put("biometricEnabled", preferences.biometricEnabled)
            put("weeklyReportEnabled", preferences.weeklyReportEnabled)
        }
        prefs.edit().putString("user_preferences", prefsJson.toString()).apply()
    }
    
    fun getUserPreferences(): UserPreferences {
        val prefsJson = prefs.getString("user_preferences", null) ?: return UserPreferences()
        return try {
            val json = JSONObject(prefsJson)
            UserPreferences(
                theme = ThemeMode.valueOf(json.getString("theme")),
                hydrationGoal = json.getInt("hydrationGoal"),
                reminderInterval = json.getInt("reminderInterval"),
                soundEnabled = json.getBoolean("soundEnabled"),
                vibrationEnabled = json.getBoolean("vibrationEnabled"),
                notificationsEnabled = json.getBoolean("notificationsEnabled"),
                biometricEnabled = json.getBoolean("biometricEnabled"),
                weeklyReportEnabled = json.getBoolean("weeklyReportEnabled")
            )
        } catch (e: Exception) {
            UserPreferences()
        }
    }
    
    // Check and unlock achievements
    fun checkAchievements() {
        val habits = getHabits()
        val habitEntries = getHabitEntries()
        val moodEntries = getMoodEntries()
        val hydrationData = getHydrationData()
        
        // Check first habit achievement
        if (habitEntries.any { it.isCompleted }) {
            unlockAchievement("first_habit")
        }
        
        // Check streak achievements
        val maxStreak = habits.maxOfOrNull { it.streak } ?: 0
        if (maxStreak >= 7) unlockAchievement("week_streak")
        if (maxStreak >= 30) unlockAchievement("month_streak")
        
        // Check mood tracking achievement
        val moodDays = moodEntries.map { it.date }.distinct().size
        if (moodDays >= 7) unlockAchievement("mood_tracker")
        
        // Check hydration achievement
        if (hydrationData.glassesToday >= hydrationData.goal) {
            // This would need to be tracked daily, simplified for now
            unlockAchievement("hydration_hero")
        }
        
        // Check total completions achievement
        val totalCompletions = habitEntries.count { it.isCompleted }
        if (totalCompletions >= 100) unlockAchievement("habit_100")
    }
}

