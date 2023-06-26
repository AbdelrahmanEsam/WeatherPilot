package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject



class SaveStringToDataStoreUseCase @Inject constructor(private val repository : Repository) {

    suspend fun execute(key : String , value : String)
    {
          repository.saveStringToDataStore(key, value)
    }
}