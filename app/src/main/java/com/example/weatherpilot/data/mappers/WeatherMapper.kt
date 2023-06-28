package com.example.weatherpilot.data.mappers

import com.example.weatherpilot.data.dto.Daily
import com.example.weatherpilot.data.dto.Hourly
import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.domain.model.DayWeatherModel
import com.example.weatherpilot.domain.model.HourWeatherModel
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.util.toDay
import com.example.weatherpilot.util.toHour


fun WeatherResponse.toWeatherModel(): WeatherModel {

    return WeatherModel(
        city = timezone.split("/")[1],
        code = current.weather[0].icon,
        hoursWeather = hourly.slice(0..23).map(Hourly::toHourWeatherModel),
        pressure = current.pressure,
        visibility = current.visibility,
        wind = current.wind_speed.toInt(),
        humidity = current.humidity,
        clouds = current.clouds,
        description = current.weather[0].description,
        temp = current.temp,
        icon = current.weather[0].icon,
        daysWeather = daily.map(Daily::toDayWeatherModel)
    )
}


fun Hourly.toHourWeatherModel(): HourWeatherModel = kotlin.run {
    HourWeatherModel(
        weatherCode = weather[0].icon,
        temp = temp.toString(),
        timestamp = dt.toHour(),
        pressure = pressure,
        humidity = humidity,
        visibility = visibility,
        clouds = clouds,
        wind = wind_speed.toInt(),
        description = weather[0].description,
        icon = weather[0].icon

    )
}


fun Daily.toDayWeatherModel(): DayWeatherModel = kotlin.run {

    DayWeatherModel(
        name = dt.toDay(), weather[0].description, icon = weather[0].icon,
        maxTemp = temp.max.toInt().toString(), minTemp = temp.min.toInt().toString(),
    )
}

