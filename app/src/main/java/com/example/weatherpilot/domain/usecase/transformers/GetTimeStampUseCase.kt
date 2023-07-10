package com.example.weatherpilot.domain.usecase.transformers

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GetTimeStampUseCase {

    fun execute(dateAndTime: String?): Long? {
        return try {

            if (dateAndTime.isNullOrBlank()) return -1


            val splittedDate = dateAndTime.split(" ")
            val builder = StringBuilder()
            builder.apply {
                append(splittedDate[0]+"-")
                if (splittedDate[1].length <2) append("0")
                append(splittedDate[1]+"-")

                if (splittedDate[2].length <2) append("0")
                append(splittedDate[2]+" ")
                if (splittedDate[3].length <2) append("0")
                append(splittedDate[3]+":")
                if (splittedDate[4].length <2) append("0")
                append(splittedDate[4]+":")
                append("00")
            }
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale("ar", "EG"))


            val parsedDate =
                format.parse(builder.toString())

            parsedDate?.time
        } catch (e: Exception) {
            null
        }
    }
}