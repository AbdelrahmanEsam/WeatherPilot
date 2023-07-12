package com.example.weatherpilot.domain.usecase.cached

import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCachedResponseUseCase  @Inject constructor(private val repository: Repository) {

    suspend fun execute() : Flow<List<WeatherModel>>
    {
        return  repository.getCachedWeatherFromDatabase()
    }
}