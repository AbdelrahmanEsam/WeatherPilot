package com.example.weatherpilot.domain.usecase.favourites

import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherFromRemoteResponseUseCase @Inject constructor(private val repository : Repository)  {

    suspend fun execute(longitude: String, latitude: String, language: String) : Flow<Response<WeatherModel>>
    {
        return repository.getWeatherFromRemoteResponse(longitude, latitude, language)
    }
}