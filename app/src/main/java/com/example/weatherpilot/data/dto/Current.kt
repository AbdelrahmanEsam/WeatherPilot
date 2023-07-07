package com.example.weatherpilot.data.dto

data class Current(
    val clouds: Int = 0,
    val dew_point: Double = 0.0,
    val dt: Int= 0,
    val feels_like: Double= 0.0,
    val humidity: Int= 0,
    val pressure: Int= 0,
    val sunrise: Int= 0,
    val sunset: Int= 0,
    val temp: Double= 0.0,
    val uvi: Double= 0.0,
    val visibility: Int= 0,
    val weather: List<Weather> = emptyList(),
    val wind_deg: Int= 0,
    val wind_speed: Double= 0.0
)