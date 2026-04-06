<div align="center">

<br/>

```
  ███████╗██╗████████╗██╗      ██████╗  ██████╗
  ██╔════╝██║╚══██╔══╝██║     ██╔═══██╗██╔════╝
  █████╗  ██║   ██║   ██║     ██║   ██║██║  ███╗
  ██╔══╝  ██║   ██║   ██║     ██║   ██║██║   ██║
  ██║     ██║   ██║   ███████╗╚██████╔╝╚██████╔╝
  ╚═╝     ╚═╝   ╚═╝   ╚══════╝ ╚═════╝  ╚═════╝
```

### *Performance Telemetry for the Human Core*

**Track. Analyze. Level Up.**

<br/>

![Version](https://img.shields.io/badge/Version-1.0.9_build_13-00E5FF?style=flat-square)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Modular_Clean-00C853?style=flat-square)
![Offline](https://img.shields.io/badge/Offline-First-FF6D00?style=flat-square)
![No Ads](https://img.shields.io/badge/Ads-Zero-E53935?style=flat-square)

<br/>

| ![HomeScreen](https://res.cloudinary.com/ds4ko1uws/image/upload/v1772429110/HomeScreen_xvkgru.jpg) | ![Workout](https://res.cloudinary.com/ds4ko1uws/image/upload/v1772429214/Workout_cs4tyw.jpg) | ![GymWork](https://res.cloudinary.com/ds4ko1uws/image/upload/v1772429200/GymWork_cifz9n.jpg) | ![History](https://res.cloudinary.com/ds4ko1uws/image/upload/v1772429181/History_bz6uik.jpg) |
|:---:|:---:|:---:|:---:|
| Dashboard | Outdoor Run | Gym Work | History |

<br/>

</div>

---

## ✦ Concept

> *FitLog treats your body like a system with telemetry.*

Most fitness apps show you numbers. FitLog gives you **states** — your step count is a *Core Energy level*, your streak is a *signal*, your week is a *performance scan*. The entire interface is built around a cyberpunk/technical aesthetic: particle backgrounds, streak orbs, and data-driven animations that respond to your actual behavior.

It's offline-first, zero-ads, and 100% private. Your health data stays on your device.

---

## ✦ What's New — v1.0.2 *"The Pro Update"*

| Feature | Description |
|---|---|
| **Weight Tracking Pro** | Full evolution module with auto-compressing charts + real-time BMI calculation |
| **Smart PDF Reports** | Export your entire activity history as a professional document, on-device |
| **Precision Timers** | `SystemClock`-based chronometers — accurate even when screen is locked |
| **Dynamic Leveling** | Profile level (Beginner → Intermediate → Pro) recalculates automatically from your goals |
| **Cloud Version Sync** | Firebase Realtime Database notifies you instantly when a new version drops |

---

## ✦ Features

### 🧠 Intelligent Streak System
FitLog's streak engine goes beyond simple day counting.

- **Consecutive day tracking** based on daily step goal completion
- **Grace Period**: Miss one day → streak *freezes*. Miss two → streak resets. No brutal punishments for human life
- **Level Up Animation**: Full-screen `SyncLevelUpScreen` with a technical orb that celebrates every streak increment

---

### 📊 Dashboard — Core Telemetry
Your health state at a glance, rendered as live data:

- **Native hardware step counter** — ultra-low battery via sensor fusion
- **Weekly activity grid** — visual consistency scan across 7 days
- **Real-time metrics** — steps, calories, and distance in a single unified interface
- **Glassmorphism UI** — semi-transparent panels, vivid gradients, smooth Compose animations

---

### 🏃 Outdoor Activity Tracking
Professional-grade session engine for walking, running, and cycling:

- **Real-time GPS tracking** with distance, pace, and calorie output
- **MET-based calorie algorithm** — factors in your weight, age, and live GPS speed for genuine accuracy (not estimations)
- **Persistent Workout Card** — a live notification on the lock screen and status bar showing timer + metrics without opening the app
- **Auto-save** — sessions are indexed into history on completion

---

### 🏋️ Gym & Strength Training
A robust dual-mode logger:

**Solo Mode** — manual logging of exercises, sets, reps, and weight (Kg or Lb)

**Protocol Mode (Guided)** — a structured 5-day split:

| Day | Focus | Supports |
|---|---|---|
| Day 1 | Push | Gym + Home |
| Day 2 | Pull | Gym + Home |
| Day 3 | Legs | Gym + Home |
| Day 4 | Upper | Gym + Home |
| Day 5 | Full Body | Gym + Home |

- **Rest Timer** with dynamic `+15s` extension — tap to breathe
- **Expandable Exercise Cards** — clean layout that handles long exercise names without UI breakage

---

### ⚖️ Weight Tracking & Health Insights

- **Evolution chart** — auto-compresses as data accumulates, always readable
- **BMI analysis** with visual scale: Underweight / Normal / Overweight / Obese
- **Timestamped history** — every entry logged for long-term trend analysis

---

### 🔔 Notification Telemetry
Smart alerts that respond to your actual progress, not just the clock:

| Trigger | Alert Type |
|---|---|
| Goal at 50% | Progress nudge |
| Goal at 90% | Final push notification |
| 9:00 PM, goal unmet | Critical level alert |
| Training reminder | Persistent alarm-style notification |

All notifications are `setExactAndAllowWhileIdle` — they fire even in Doze mode.

---

### 📜 History & Export

- **Smart filters**: Day / Week / Month / All Time
- **PDF export**: Clean structured report for coaches, logs, or personal records
- **Bilingual**: Fully localized in **English** and **Español**

---

### 🎮 Gamification *(in development)*

- Unlockable achievements based on streak milestones
- Badges for total volume lifted across all gym sessions
- Weekly performance dashboard with telemetry-style data graphs

---

## ✦ Architecture

FitLog is built on **Modular Clean Architecture** following Google's official recommendations, with full separation between layers and features:

```
fitlog/
├── app/                        → MainActivity, NavHost, Hilt setup, permission orchestration
│                                 Launches StepCounterService (Foreground, API 34+ compliant)
│
├── core/
│   ├── database/               → Room DB — profiles, gym sessions, runs, goals
│   │                             Schema migrations v5 → v6 with full data integrity
│   ├── datastore/              → User preferences (theme, units, notification flags)
│   ├── domain/                 → Pure business logic — UseCases, repository interfaces
│   ├── model/                  → Shared data models across all modules
│   ├── network/                → Firebase Realtime Database sync layer
│   ├── ui/                     → Design system: theme, typography, custom icons, strings
│   └── notification/           → Alert engine, AlarmManager workers
│
└── feature/
    ├── dashboard/              → Core telemetry view, step tracking, streak orb
    ├── gymwork/                → Solo + Protocol modes, rest timer
    ├── outdoorrun/             → GPS tracking, MET calorie engine
    ├── onboarding/             → Profile setup, unit system, dynamic leveling
    ├── weight/                 → BMI module, evolution chart
    ├── history/                → Filters, PDF export
    └── settings/               → Theme, language, notifications, profile
```

**Data flow:**
```
Compose UI ──► ViewModel (StateFlow) ──► UseCase ──► Repository
                    ▲                                     │
                    └──────────── Flow ◄──────────────────┘
                                              │
                                    Room ─────┴───── Firebase
```

**State management pattern:** MVI/MVVM hybrid — each feature exposes a sealed `UiState` consumed via `collectAsStateWithLifecycle`.

---

## ✦ Tech Stack

```
Language            Kotlin 2.1.0
UI                  Jetpack Compose + Material 3
State               StateFlow + MVI UiState pattern
DI                  Dagger Hilt + KSP
Local DB            Room (migrations v5→v6)
Preferences         DataStore
Background          Foreground Service (step counter, API 34+)
Alarms              AlarmManager — setExactAndAllowWhileIdle
Cloud               Firebase Realtime Database
Build               ProGuard/R8 with custom rules
Localization        English + Español
```

---

## ✦ Privacy & Performance

- **Offline-first** — all health data lives on your device
- **Zero ads** — no trackers, no banners, no distractions
- **Battery optimized** — hardware sensor fusion for step counting, smart service lifecycle
- **ProGuard/R8** — code shrinking and obfuscation without compromising stability

---

## ✦ Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+ (target: 34)

### Clone & run
```bash
git clone https://github.com/saicojavc/fitlog.git
cd fitlog
```

Open in Android Studio, sync Gradle, and run on a device or emulator (API 26+).

> **Note:** GPS tracking and step counting require a physical device for full functionality.

---

## ✦ Roadmap

- [x] Weight tracking + BMI module
- [x] PDF export engine
- [x] Protocol gym mode (5-day split)
- [x] Grace-period streak system
- [x] MET-based calorie calculation
- [ ] Weekly telemetry graphs (performance dashboard)
- [ ] Achievement system (streaks + volume milestones)
- [ ] WearOS sync
- [ ] Gemini API — AI-powered weekly performance summary

---

<div align="center">

<br/>

*"Your body generates data every second.*
*FitLog just makes it readable."*

<br/>

Built with ⚡ by **Jorge Adrián Valdés Camacho**
[Portfolio](https://jorge-android-dev.web.app) · [GitHub](https://github.com/saicojavc) · [LinkedIn](https://www.linkedin.com/in/jorge-adri%C3%A1n-vald%C3%A9s-camacho-21b371221/)

<br/>

</div>
