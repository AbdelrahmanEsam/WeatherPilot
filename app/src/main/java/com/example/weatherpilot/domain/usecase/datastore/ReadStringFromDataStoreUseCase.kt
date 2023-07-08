package com.example.weatherpilot.domain.usecase.datastore

import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadStringFromDataStoreUseCase @Inject constructor(private val repository : Repository) {

    suspend fun  <T> execute(key : String) : Flow<Response<T>>
    {
            return repository.getStringFromDataStore(key)
    }

}