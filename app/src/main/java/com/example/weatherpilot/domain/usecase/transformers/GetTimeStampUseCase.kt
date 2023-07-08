package com.example.weatherpilot.domain.usecase.transformers

import java.util.Calendar

class GetTimeStampUseCase {

    fun execute(dateAndTime: String?): Long? {

        val calendar = Calendar.getInstance()
        return try {

            if (dateAndTime.isNullOrBlank()) return -1


            val splittedDate = dateAndTime.split(" ")
            calendar.set(
                splittedDate[0].toInt(),
                splittedDate[1].toInt(),
                splittedDate[2].toInt(),
                splittedDate[3].toInt(),
                splittedDate[4].toInt(),
                0
            )
            return calendar.timeInMillis
        } catch (e: Exception) {
            null
        }
    }
}