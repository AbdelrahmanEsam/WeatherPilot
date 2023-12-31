package com.example.weatherpilot.domain.usecase.alerts

import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.repository.Repository
import com.example.weatherpilot.util.usescases.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlertsUseCase  @Inject constructor(private val repository: Repository) {
     fun execute() : Flow<List<AlertItem>> {
        return repository.getAlerts()
    }
}