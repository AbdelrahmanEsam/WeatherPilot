package com.example.weatherpilot.domain.model

data class HourWeatherModel(
    val weatherCode: String,
    val temp: String,
    val timestamp: String,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val clouds: Int,
    val wind: Int,
    val description: String,
    val icon: String
)
