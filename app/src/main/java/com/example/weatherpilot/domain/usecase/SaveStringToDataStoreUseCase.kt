package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject



class SaveStringToDataStoreUseCase @Inject constructor(private val repository : Repository) {

    suspend fun execute(longitude : String , latitude : String) : WeatherModel
    {
        return  repository.getWeatherResponse(longitude, latitude)
    }
}