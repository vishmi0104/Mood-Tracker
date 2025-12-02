# HabitMate - Wellness Companion App

## Overview
HabitMate is a comprehensive Android wellness app designed to help users manage their daily health routines, track habits, log moods, and maintain proper hydration. Built with modern Android development practices using Kotlin, Material Design 3, and responsive layouts.

## Features

### 🎯 Core Features
- **Daily Habit Tracker**: Add, edit, delete, and track daily wellness habits
- **Mood Journal**: Log mood entries with emoji selector and optional notes
- **Hydration Reminder**: Smart notifications to remind users to drink water
- **Progress Visualization**: Beautiful charts showing mood trends and habit completion

### 🚀 Advanced Features
- **Home Screen Widget**: Shows today's habit completion percentage
- **Mood Trend Chart**: MPAndroidChart integration for 7-day mood visualization
- **Responsive Design**: Optimized for both phones and tablets
- **Data Persistence**: SharedPreferences-based storage without external databases

### 🎨 User Experience
- **Onboarding Flow**: 3-screen introduction to app features
- **Authentication**: Login and registration with validation
- **Modern UI**: Ocean blue and pink color scheme with gradient backgrounds
- **Material Design 3**: Following latest design guidelines

## Technical Architecture

### 📱 Activities & Fragments
- `SplashActivity`: App launch screen with branding
- `OnboardingActivity`: 3-screen onboarding flow
- `AuthActivity`: Login and registration with validation
- `MainActivity`: Main container with bottom navigation
- `HabitsFragment`: Habit management and tracking
- `MoodFragment`: Mood logging and trend visualization
- `SettingsFragment`: Hydration tracking and app settings

### 🗄️ Data Management
- `DataManager`: Centralized data persistence using SharedPreferences
- `Models`: Data classes for Habit, MoodEntry, User, and HydrationData
- No external databases - all data stored locally

### 🔔 Background Services
- `HydrationReminderWorker`: WorkManager-based water reminder notifications
- Periodic work requests for customizable reminder intervals

### 📊 Widgets
- `HabitProgressWidget`: Home screen widget showing daily progress
- Real-time updates with click-to-open functionality

## Dependencies

```kotlin
// Core Android
implementation 'androidx.core:core-ktx:1.17.0'
implementation 'androidx.appcompat:appcompat:1.7.1'
implementation 'com.google.android.material:material:1.13.0'
implementation 'androidx.constraintlayout:constraintlayout:2.2.1'

// Navigation
implementation 'androidx.navigation:navigation-fragment-ktx:2.8.4'
implementation 'androidx.navigation:navigation-ui-ktx:2.8.4'

// Background Work
implementation 'androidx.work:work-runtime-ktx:2.10.0'

// Charts
implementation 'com.github.PhilJay:MPAndroidChart:3.1.0'

// Animations
implementation 'com.airbnb.android:lottie:6.4.0'
```

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/habitmate/
│   │   ├── data/
│   │   │   ├── Models.kt
│   │   │   └── DataManager.kt
│   │   ├── ui/
│   │   │   ├── splash/
│   │   │   ├── onboarding/
│   │   │   ├── auth/
│   │   │   ├── main/
│   │   │   ├── habits/
│   │   │   ├── mood/
│   │   │   ├── settings/
│   │   │   └── hydration/
│   │   └── widget/
│   └── res/
│       ├── layout/
│       ├── layout-sw600dp/ (Tablet layouts)
│       ├── drawable/
│       ├── values/
│       └── xml/
```

## Key Features Implementation

### Habit Tracking
- Add habits with name, description, and frequency (Daily/Weekly/Monthly)
- Check off habits for the current day
- Visual progress tracking with completion percentage
- Edit and delete functionality with confirmation dialogs

### Mood Journal
- 5 mood types: Happy, Excited, Neutral, Sad, Angry
- Emoji-based selection interface
- Optional note-taking for each mood entry
- 7-day mood trend chart using MPAndroidChart
- Calendar-style mood history view

### Hydration Reminder
- Customizable daily water intake goals
- Manual glass counting with +1 button
- Configurable reminder intervals (30min to 4 hours)
- WorkManager-based background notifications
- Achievement notifications when goals are reached

### Widget Integration
- Home screen widget showing daily habit completion
- Real-time progress updates
- Click-to-open app functionality
- Gradient background matching app theme

## Color Scheme
- **Primary**: Ocean Blue (#0066CC)
- **Secondary**: Pink (#FF69B4)
- **Gradients**: Ocean Blue to Pink transitions
- **Status Colors**: Success (Green), Warning (Yellow), Error (Red)

## Responsive Design
- Phone layouts optimized for portrait orientation
- Tablet layouts with side navigation for landscape
- Adaptive UI elements that scale appropriately
- Material Design 3 components throughout

## Data Persistence
All user data is stored locally using SharedPreferences:
- User authentication information
- Habit definitions and completion status
- Mood entries with timestamps
- Hydration tracking data
- App settings and preferences

## Permissions Required
- `POST_NOTIFICATIONS`: For hydration reminder notifications
- `WAKE_LOCK`: For background work execution
- `RECEIVE_BOOT_COMPLETED`: For restarting reminders after device reboot

## Installation & Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator (API 24+)

## Future Enhancements
- Cloud sync capabilities
- Social features and sharing
- Advanced analytics and insights
- Custom habit categories
- Integration with fitness trackers
- Dark theme support

## License
This project is created for educational purposes as part of an Android development assignment.

---

**HabitMate** - Your Wellness Companion 🌟

