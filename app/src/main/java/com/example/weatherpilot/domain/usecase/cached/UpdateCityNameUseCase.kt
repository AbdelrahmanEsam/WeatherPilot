package com.example.weatherpilot.domain.usecase.cached

import com.example.weatherpilot.data.dto.WeatherResponse
import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCityNameUseCase @Inject constructor(private val repository: Repository) {

    suspend fun <T>execute(city: String) : Flow<Response<T>>
    {
        return repository.updateResponseToDatabase(city)
    }
}