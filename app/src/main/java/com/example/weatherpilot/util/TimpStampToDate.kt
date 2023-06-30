package com.example.weatherpilot.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


fun Int.toHour() : String
{
    val date = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(this.toLong()),
        ZoneId.systemDefault()
    )
    val formatter = DateTimeFormatter.ISO_LOCAL_TIME
    val formattedDate = date.format(formatter).split(":")[0]

    val result = StringBuilder()
    if (formattedDate.toInt() > 12)  result.append(formattedDate.toInt()-12).append(" PM")
    else if (formattedDate.toInt() == 12) result.append(formattedDate).append(" PM")
    else result.append(formattedDate).append(" AM")
  return    result.toString()
}





fun Long.toDay() : String
{
    val date = Date(this*1000)
    val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
    return formatter.format(date).toString()
}



