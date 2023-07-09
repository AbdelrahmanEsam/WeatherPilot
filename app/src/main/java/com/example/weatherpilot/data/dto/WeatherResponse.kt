package com.example.weatherpilot.data.dto

data class WeatherResponse(
    val current: Current,
    val daily: List<Daily>? = null,
    val hourly: List<Hourly>? = null,
    val alerts : List<Alert>?= null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val timezone: String = "",
    val timezone_offset: Int = 0
)