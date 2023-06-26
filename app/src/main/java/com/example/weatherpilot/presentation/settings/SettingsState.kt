package com.example.weatherpilot.presentation.settings

data class SettingsState(val locationType : String? = "GPS"
                         , val languageType : String? = "english"
, val windType : String? = "Meter / Sec"
, val temperatureType : String? = "Celsius"
, val notificationType : String?= "enabled"

)
