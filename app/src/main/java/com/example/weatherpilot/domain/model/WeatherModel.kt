package com.example.weatherpilot.domain.model

data class WeatherModel(
    val city: String,
    val code: String,
    val hoursWeather: List<HourWeatherModel>,
    val daysWeather: List<DayWeatherModel>?,
    val pressure: Int,
    val visibility: Int,
    val wind: Int,
    val humidity: Int,
    val clouds: Int,
    val description: String,
    val temp: Double,
    val icon: String?
)
