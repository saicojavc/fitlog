# FitLog - Simple Fitness Tracker for Beginners

**FitLog** is a lightweight, **offline-first** Android fitness tracking app built with modern Kotlin and Jetpack Compose. Designed specifically for people just starting their fitness journey (gym workouts, walking, and running), it focuses on simplicity, motivation, and privacy — no ads, no accounts, no internet required.

Track your daily steps, log outdoor activities with GPS, record gym sessions manually, monitor progress, and stay motivated with gentle daily notifications.

**Current date reference**: Built and analyzed as of January 2026.

## Features

### Onboarding & User Profile
Personalized setup with age, weight, height, gender, and daily goals (steps & calories).  
Uses Mifflin-St Jeor formula for accurate BMR-based calorie estimation.



### Dashboard (Home Screen)
Real-time overview of:
- Daily steps (using native sensor)
- Calories burned
- Approximate distance
- Progress rings & simple weekly charts



### Outdoor Activity Tracking
Real-time GPS tracking for walking, running, or cycling:
- Start / Pause / Stop controls
- Live timer, distance, pace, calories
- Automatic save to history


### Gym / Strength Training Log
Manual entry of workouts:
- Add multiple exercises per session
- Track sets, reps, weight
- Grouped by muscle or custom name

### History & Progress
Filter sessions by:
- Day / Week / Month / All time
- Detailed expandable cards
- **Export current filtered view as PDF** (via FAB)


### Settings & Personalization
- Theme: Light / Dark / System
- Language: English / Español
- Units: Metric (km/kg/cm) ↔ Imperial (mi/lb/ft-in) with live conversion
- Motivational notifications toggle + daily reminder time

### Motivational Notifications (Local)
- Daily morning encouragement
- 50% goal progress nudge
- Goal achieved celebration
- Soft end-of-day summary

All scheduled via AlarmManager — no background services needed for basic use.

## Tech Stack & Architecture

- **Language**: Kotlin 100%
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Clean MVVM + Repositories
- **State Management**: StateFlow + collectAsStateWithLifecycle
- **Navigation**: Jetpack Navigation Compose
- **Persistence**: Room Database (UserProfile, ActivitySession, GymExercise)
- **Preferences**: DataStore (theme, units, notification settings)
- **Sensors**: Sensor.TYPE_STEP_COUNTER (steps) + Fused Location Provider (GPS)
- **Notifications**: NotificationCompat + AlarmManager + BroadcastReceiver
- **Export**: Native PdfDocument (no external libs)
- **Other**: Coroutines & Flow, Gson (for TypeConverters)

**Key strengths**:
- Fully offline-first
- Battery-friendly sensor handling
- Clean separation of concerns
- Easy to extend (Health Connect, Firebase, Wear OS ready for v2)
