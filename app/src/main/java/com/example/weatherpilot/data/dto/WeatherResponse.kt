package com.example.weatherpilot.data.dto

data class WeatherResponse(
    val current: Current,
    val daily: List<Daily> = mutableListOf(),
    val hourly: List<Hourly> = mutableListOf(),
    val alerts : List<Alert>?= mutableListOf(),
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val timezone: String = "",
    val timezone_offset: Int = 0
)