# FitLog - Your Ultimate Modern Fitness Tracker

**FitLog** is a high-performance, privacy-focused, and **offline-first** Android application designed to accompany you on your fitness journey. Built with the latest technologies in the Android ecosystem, it offers a professional experience for tracking steps, weight evolution, gym sessions, and outdoor activities with absolute precision.

![Dashboard HomeScreen](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309420/HomeScreen_xvkgru.jpg)

## 🚀 What's New in v1.0.2 (The "Pro" Update)
- **Weight Tracking Pro**: A dedicated module to monitor your weight evolution with dynamic, auto-compressing charts and real-time **BMI (Body Mass Index)** calculation.
- **Smart Reports**: Generate and export your entire activity history into professional **PDF documents** directly to your device.
- **Precision Timers**: Redesigned chronometers for Workout and Gym modules that use `SystemClock` to ensure 100% accuracy, even when the app is in the background or the screen is locked.
- **Dynamic User Levels**: No more static profiles. Your level (Beginner, Intermediate, or Professional) is now recalculated automatically based on your daily goals.
- **Cloud Version Sync**: Integrated with **Firebase Realtime Database** to notify you instantly when a new version with improvements is available.

---

## ✨ Comprehensive Features

### 🛠️ Intelligent Onboarding & User Profile
Personalize your experience from the start. Set your age, gender, weight, and height. Choose your preferred measurement system (**Metric or Imperial**) and watch how the app adapts all values instantly.
- **Dynamic Leveling**: Based on your step and calorie goals, the app classifies your profile.
- **Unit Conversion**: Seamlessly switch between Kg/Cm and Lb/Ft-In with real-time mathematical precision.

| Onboarding Step 1 | Onboarding Step 2 | Onboarding Step 3 |
|:---:|:---:|:---:|
| ![Onboarding 1](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309402/dashboard1_dr6csw.jpg) | ![Onboarding 2](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309350/daschboard2_fraui1.jpg) | ![Onboarding 3](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309379/dashboard3_cdqmff.jpg) |

### 📊 Advanced Dashboard
Your health at a glance.
- **Native Step Counter**: Leverages hardware sensors for ultra-low battery consumption.
- **BMI Status**: Instant health classification with a visual color-coded scale (Underweight, Normal, Overweight, Obese).
- **Weekly Activity**: Quick visual summary of your consistency throughout the week.

### 🏃 Outdoor Activity Tracking
Professional-grade tracking for walking, running, or cycling.
- **Accurate Metrics**: Real-time distance, pace, and calorie calculation.
- **Persistent Notifications**: A dedicated "Workout Card" in your notification bar and **lock screen** that shows live progress and a running timer without opening the app.
- **Auto-Save**: Sessions are automatically indexed into your history upon completion.

![Workout](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309305/Workout_cs4tyw.jpg)

### 🏋️ Gym & Strength Training Log
A robust logger for your gym sessions.
- **Expandable Exercise Cards**: View your sets, reps, and weights in a clean, organized layout that supports long exercise names without breaking the UI.
- **Dynamic Weight Support**: Track your lifts in Kg or Lb according to your settings.

![Gym Work](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309437/GymWork_cifz9n.jpg)

### 📜 History & Professional Export
- **Smart Filters**: Analyze your progress by Day, Week, Month, or your entire history.
- **PDF Export**: Generate a clean, structured report of your activities to share with a coach or keep for your records.

![History](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309452/History_bz6uik.jpg)

### ⚙️ Personalization & UI
- **Premium Dark Mode**: A forced dark aesthetic for reduced eye strain and a modern, high-end look.
- **Glassmorphism Design**: Semi-transparent elements, vivid gradients, and smooth animations powered by Jetpack Compose.
- **Bilingual Support**: Fully localized in **English** and **Español**.

![Settings](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309474/settings_yuqxsi.jpg)
![Profile](https://res.cloudinary.com/ds4ko1uws/image/upload/v1771309481/WhatsApp_Image_2026-02-16_at_1.51.25_AM_1_ikhxi6.jpg)

---

## 🏗️ Architecture & Tech Stack

The app is built on a **Modular Clean Architecture** to ensure maintainability, testability, and performance:

- **Modular Design**: Separated into `:app`, `:core` (common logic), and `:feature` modules.
- **UI Layer**: 100% **Jetpack Compose** with Material 3 components.
- **State Management**: **MVVM/MVI** pattern using `StateFlow` and `collectAsStateWithLifecycle` for efficient UI updates.
- **Persistence**: 
    - **Room Database**: With robust **Schema Migrations (v5 to v6)** to ensure user data integrity.
    - **DataStore**: Reactive management of user preferences.
- **Dependency Injection**: **Dagger Hilt** for clean component lifecycle management.
- **Background Tasks**: 
    - **Foreground Services**: Optimized for step counting (API 34+ compliant).
    - **AlarmManager**: High-precision `setExactAndAllowWhileIdle` for motivational and reminder notifications.
- **External Integration**: **Firebase** Realtime Database for remote configuration and crash reporting.
- **Performance**: Custom **ProGuard/R8** rules for code shrinking and obfuscation without affecting app stability.

---

## 🔒 Privacy & Reliability
- **Offline First**: Your sensitive health data stays on your phone.
- **No Ads**: Zero distractions, zero tracking.
- **Battery Optimized**: Smart sensor handling to minimize energy consumption.

---
*Developed with ❤️ by Jorge Dev*
