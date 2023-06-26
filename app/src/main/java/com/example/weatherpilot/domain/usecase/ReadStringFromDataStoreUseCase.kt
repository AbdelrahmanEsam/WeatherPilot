package com.example.weatherpilot.domain.usecase

import android.util.Log
import com.example.weatherpilot.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReadStringFromDataStoreUseCase @Inject constructor(private val repository : Repository) {

    suspend fun execute(key : String) : Flow<String?>
    {
            return repository.getStringFromDataStore(key)
    }

}