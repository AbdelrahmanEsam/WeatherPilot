package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(private val repository : Repository) {

    suspend fun execute(longitude : String , latitude : String,language : String = "") : WeatherModel
    {
         return  repository.getWeatherResponse(longitude, latitude)
    }
}