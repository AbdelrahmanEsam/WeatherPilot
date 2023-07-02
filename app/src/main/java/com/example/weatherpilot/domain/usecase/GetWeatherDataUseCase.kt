package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(private val repository : Repository) {

    suspend fun execute(longitude : String , latitude : String,language : String) : Flow<Response<WeatherModel>>
    {
         return  repository.getWeatherResponse(longitude, latitude,language)
    }
}