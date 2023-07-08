package com.example.weatherpilot.domain.usecase.datastore

import com.example.weatherpilot.domain.model.WeatherModel
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class SaveStringToDataStoreUseCase @Inject constructor(private val repository : Repository) {

    suspend fun  execute(key : String , value : String) : Flow<Response<String>>
    {
        return  repository.saveStringToDataStore(key, value)
    }
}