package com.example.weatherpilot.domain.model

data class WeatherModel(
    val city: String = "",
    val code: String = "",
    val hoursWeather: List<HourWeatherModel> = emptyList(),
    val daysWeather: List<DayWeatherModel>? = emptyList(),
    val pressure: Int = 0,
    val visibility: Int= 0,
    val wind: Int= 0,
    val humidity: Int= 0,
    val clouds: Int= 0,
    val description: String = "",
    val temp: Double = 0.0,
    val icon: String? = "",
    val alertMessage : String?= ""
)
