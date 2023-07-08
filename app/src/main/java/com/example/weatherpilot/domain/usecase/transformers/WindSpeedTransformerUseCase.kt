package com.example.weatherpilot.domain.usecase.transformers

import kotlin.math.roundToInt

class WindSpeedTransformerUseCase {

   fun execute(speed : Int,type : String) : String
    {

       return when(type){


           "M/S" -> (speed.coerceAtLeast(0) * 0.44704)
               .toInt().toString() + " M/S"

           else -> (speed.coerceAtLeast(0) *  2.23694).roundToInt().toString() + " M/H"
       }

    }
}