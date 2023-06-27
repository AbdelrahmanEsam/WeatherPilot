package com.example.weatherpilot.domain.usecase

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetCurrentDateUseCase {

     fun execute() : String
    {
        val date = Date(System.currentTimeMillis())
        val formatter = SimpleDateFormat("EEE, MMM dd yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }

}