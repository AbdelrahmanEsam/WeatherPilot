package com.example.weatherpilot.data.mappers

import com.example.weatherpilot.data.dto.TimeStampWeather
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.domain.model.HourWeatherModel
import com.example.weatherpilot.domain.model.WeatherModel


fun WeatherResponse.toWeatherModel() : WeatherModel
{
    return WeatherModel(city = city.name , code = cod
        , hoursWeather =  list.map(TimeStampWeather::toHourWeatherModel)
        , message = message.toString()
        , pressure = list.sumOf { it.main.pressure } / 24
    , visibility = list.sumOf { it.visibility } / 24
    , wind = (list.sumOf { it.wind.speed } / 24).toInt()
    , humidity = list.sumOf { it.main.humidity } / 24
 , clouds = list.sumOf { it.clouds.all } / 24
    , description = list.flatMap { it.weather }.groupBy { it.description }.maxByOrNull { it.value.count() }?.key.toString()
    , temp = (list.sumOf { it.main.temp } / 24)
    ,icon = list.flatMap { it.weather }.groupBy { it.icon }.maxByOrNull { it.value.count() }?.key
    )
}


fun TimeStampWeather.toHourWeatherModel() : HourWeatherModel = kotlin.run {
    HourWeatherModel(date = dt_txt
        , weatherCode = weather[0].icon,temp = main.temp.toString()
        , timestamp = dt, pressure = main.pressure
    , main.humidity
    , visibility
    , clouds
    , wind
    , weather[0].description
    )
}
