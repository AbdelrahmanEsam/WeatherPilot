package com.example.weatherpilot.domain.model

import com.example.weatherpilot.data.dto.Clouds
import com.example.weatherpilot.data.dto.Wind

data class HourWeatherModel(
    val date: String,
    val weatherCode: String,
    val temp: String,
    val timestamp: Long,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val clouds: Clouds,
    val wind: Wind,
    val description: String
)
