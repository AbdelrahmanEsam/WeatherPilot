package com.example.weatherpilot.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WeatherResponse")
data class WeatherResponse(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0 ,
    val current: Current,
    val daily: List<Daily> = emptyList(),
    val hourly: List<Hourly> = emptyList(),
    val alerts : List<Alert> = emptyList(),
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val timezone: String = "",
    val timezone_offset: Int = 0,
)