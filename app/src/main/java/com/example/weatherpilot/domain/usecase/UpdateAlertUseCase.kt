package com.example.weatherpilot.domain.usecase

import com.example.weatherpilot.data.mappers.toSavedAlert
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import javax.inject.Inject

class UpdateAlertUseCase @Inject constructor(private val repository: Repository){

    suspend fun execute(alert: AlertItem)
    {
        repository.updateAlert(alert.toSavedAlert())
    }

}