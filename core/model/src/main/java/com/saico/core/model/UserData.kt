package com.saico.core.model

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val languageConfig: LanguageConfig,
    val unitsConfig: UnitsConfig,
    val useDynamicColor: Boolean,
    val workoutReminderHour: Int = 18,
    val workoutReminderMinute: Int = 0
)

enum class DarkThemeConfig {
    FOLLOW_SYSTEM, LIGHT, DARK
}

enum class LanguageConfig {
    FOLLOW_SYSTEM, ENGLISH, SPANISH
}

enum class UnitsConfig {
    METRIC, IMPERIAL
}
enum class BmiStatus {
    LOW_WEIGHT, NORMAL, OVERWEIGHT, OBESE, UNKNOWN
}
