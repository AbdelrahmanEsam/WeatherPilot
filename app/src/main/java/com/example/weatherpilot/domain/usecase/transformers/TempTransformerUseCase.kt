package com.example.weatherpilot.domain.usecase.transformers

import kotlin.math.roundToInt

class TempTransformerUseCase {

    fun execute(type: Temperature): String {

        return when (type) {
            is Temperature.Celsius -> "${type.temp} C"
            is Temperature.Fahrenheit -> (type.temp * 1.8 + 32).roundToInt().toString() + " F"
            is Temperature.Kelvin -> (type.temp + 273.15).roundToInt().toString() + " K"
        }

    }
}