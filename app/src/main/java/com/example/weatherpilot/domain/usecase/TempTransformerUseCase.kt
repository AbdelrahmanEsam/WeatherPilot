package com.example.weatherpilot.domain.usecase

import kotlin.math.roundToInt

class TempTransformerUseCase {

     fun execute(temp : Int,type : String) : String
    {

        return when(type)
        {
            "F" ->  (temp * 1.8 + 32).roundToInt().toString() + " F"

            "K" -> (temp + 273.15).roundToInt().toString() + " K"

             else -> "$temp C"
        }

    }
}