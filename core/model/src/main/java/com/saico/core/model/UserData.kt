package com.saico.core.model

data class UserData(
    val darkThemeConfig: DarkThemeConfig,
    val languageConfig: LanguageConfig,
    val unitsConfig: UnitsConfig,
    val useDynamicColor: Boolean
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
