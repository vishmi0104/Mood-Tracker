package com.example.habitmate.data

data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val category: HabitCategory = HabitCategory.GENERAL,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompletions: Int = 0
)

enum class HabitFrequency {
    DAILY, WEEKLY, MONTHLY
}

enum class HabitCategory(val displayName: String, val icon: String) {
    FOCUS("Focus", "🎯"),
    FITNESS("Fitness", "💪"),
    REST("Rest", "😴"),
    LEARNING("Learning", "📚"),
    HEALTH("Health", "🏥"),
    GENERAL("General", "⭐")
}

data class HabitEntry(
    val id: String = "",
    val habitId: String = "",
    val date: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val note: String = ""
)

data class MoodEntry(
    val id: String = "",
    val mood: MoodType = MoodType.NEUTRAL,
    val note: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val intensity: Int = 5 // 1-10 scale
)

enum class MoodType(val emoji: String, val colorRes: String, val description: String) {
    HAPPY("😊", "mood_happy", "Happy"),
    NEUTRAL("😐", "mood_neutral", "Neutral"),
    SAD("😢", "mood_sad", "Sad"),
    ANGRY("😠", "mood_angry", "Angry"),
    EXCITED("🤩", "mood_excited", "Excited"),
    CALM("😌", "mood_calm", "Calm"),
    STRESSED("😰", "mood_stressed", "Stressed"),
    GRATEFUL("🙏", "mood_grateful", "Grateful"),
    LONELY("😔", "mood_lonely", "Lonely"),
    CONFIDENT("😎", "mood_confident", "Confident")
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val avatar: String = "",
    val preferences: UserPreferences = UserPreferences()
)

data class UserPreferences(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val hydrationGoal: Int = 8,
    val reminderInterval: Int = 60,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val weeklyReportEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false // New flag to track onboarding completion
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class HydrationData(
    val glassesToday: Int = 0,
    val goal: Int = 8,
    val reminderInterval: Int = 60, // minutes
    val lastReminderTime: Long = 0,
    val lastGlassTime: Long = 0,
    val totalGlassesThisWeek: Int = 0,
    val averageGlassesPerDay: Float = 0f,
    val customIntakeMl: Int = 250 // Custom intake amount in ml
)

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val type: AchievementType = AchievementType.STREAK,
    val requirement: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val category: AchievementCategory = AchievementCategory.HABITS
)

enum class AchievementType {
    STREAK, COMPLETION, CONSISTENCY, MILESTONE, SPECIAL
}

enum class AchievementCategory(val displayName: String) {
    HABITS("Habits"),
    MOOD("Mood"),
    HYDRATION("Hydration"),
    GENERAL("General")
}

data class DailyQuote(
    val id: String = "",
    val text: String = "",
    val author: String = "",
    val category: QuoteCategory = QuoteCategory.MOTIVATION,
    val date: String = ""
)

enum class QuoteCategory {
    MOTIVATION, MINDFULNESS, SUCCESS, HEALTH, HAPPINESS
}

data class AnalyticsData(
    val weeklyHabitCompletion: Float = 0f,
    val weeklyMoodAverage: Float = 0f,
    val weeklyHydrationAverage: Float = 0f,
    val totalHabitsCompleted: Int = 0,
    val totalDaysActive: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val mostFrequentMood: MoodType = MoodType.NEUTRAL,
    val habitsByCategory: Map<HabitCategory, Int> = emptyMap(),
    val moodDistribution: Map<MoodType, Int> = emptyMap()
)

data class WidgetData(
    val habitCompletionPercentage: Float = 0f,
    val hydrationPercentage: Float = 0f,
    val todayMood: MoodType? = null,
    val nextHabit: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

data class NotificationSettings(
    val habitReminders: Boolean = true,
    val hydrationReminders: Boolean = true,
    val moodCheckIns: Boolean = true,
    val achievementNotifications: Boolean = true,
    val weeklyReports: Boolean = true,
    val quietHoursStart: Int = 22, // 10 PM
    val quietHoursEnd: Int = 8, // 8 AM
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

