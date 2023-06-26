package com.example.weatherpilot.presentation.settings

sealed interface SettingsIntent
{
    data class  LocationChange(val  location : String) : SettingsIntent
    data class  LanguageChange(val  language : String) : SettingsIntent
    data class  WindChange(val  wind : String) : SettingsIntent
    data class  TemperatureChange(val  temperature : String) : SettingsIntent
    data class  NotificationChange(val  notification : String) : SettingsIntent
}
