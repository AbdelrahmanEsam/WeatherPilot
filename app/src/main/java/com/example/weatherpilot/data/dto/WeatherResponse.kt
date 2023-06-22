package com.example.weatherpilot.data.dto

data class WeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<TimeStampWeather>,
    val message: Int
)