package com.example.weatherpilot.domain.usecase.transformers

sealed interface Temperature
{
    data class Celsius(val temp : Int) : Temperature
    data class Fahrenheit (val temp : Int) : Temperature
    data class Kelvin(val temp : Int) : Temperature
}