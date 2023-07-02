package com.example.weatherpilot.data.dto

data class WeatherResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val alerts : List<Alert>?,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)