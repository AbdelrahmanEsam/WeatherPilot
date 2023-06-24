package com.example.weatherpilot.domain.usecase

import java.sql.Timestamp
import java.time.LocalDate

class GetCurrentTimeStampUseCase {

    suspend fun execute() : Long = System.currentTimeMillis() / 1000

}