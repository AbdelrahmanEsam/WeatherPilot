package com.example.weatherpilot.presentation.settings

data class SettingsState(val location : String = "GPS"
                         , val language : String = "english"
, val wind : String = "Meter / Sec"
, val temperature : String = "Celsius"
, val notification : String= "enabled"

)
