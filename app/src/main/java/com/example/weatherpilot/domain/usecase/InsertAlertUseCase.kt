package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.mappers.toSavedAlert
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InsertAlertUseCase @Inject constructor(private val repository: Repository) {

    suspend fun execute(alert : AlertItem) : Flow<Response<String>>
    {
     return  repository.insertAlertToDatabase(alert = alert.toSavedAlert())
    }
}