package com.example.weatherpilot.util

import android.icu.util.Calendar
import android.util.Log
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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


fun Int.toDate() : LocalDate
{
    val instant = Instant.ofEpochMilli(this.toLong())
    return instant.atZone(ZoneId.systemDefault()).toLocalDate()
}


fun Int.toDay() : String
{
    val  calendar = Calendar.getInstance()

    calendar.timeInMillis = this.toLong()

    val  days = arrayOf( "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")

   return  days[calendar.get(Calendar.DAY_OF_WEEK)];
}



