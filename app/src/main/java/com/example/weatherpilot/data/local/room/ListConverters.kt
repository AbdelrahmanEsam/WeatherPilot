package com.example.weatherpilot.data.local.room

import androidx.room.TypeConverter
import com.example.weatherpilot.data.dto.Alert
import com.example.weatherpilot.data.dto.Current
import com.example.weatherpilot.data.dto.Daily
import com.example.weatherpilot.data.dto.Hourly
import com.example.weatherpilot.data.dto.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



object Converters {
    @TypeConverter
    fun dailyFromJson(jsonString: String): List<Daily> {
        val listType = object : TypeToken<List<Daily>?>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    @TypeConverter
    fun dailyToJson(objects: List<Daily>): String {
        return Gson().toJson(objects)
    }



    @TypeConverter
    fun hourlyFromJson(jsonString: String): List<Hourly> {
        val listType = object : TypeToken<List<Hourly>?>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    @TypeConverter
    fun hourlyToJson(objects: List<Hourly>): String {
        return Gson().toJson(objects)
    }

    @TypeConverter
    fun alertsFromJson(jsonString: String): List<Alert> {
        val listType = object : TypeToken<List<Alert>?>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    @TypeConverter
    fun alertsToJson(alerts: List<Alert>?): String {
      alerts?.let {
          return Gson().toJson(alerts)
      } ?: kotlin.run {
          return Gson().toJson(emptyList<Alert>())
      }

    }


    @TypeConverter
    fun weatherFromJson(jsonString: String): List<Weather> {
        val listType = object : TypeToken<List<Weather>?>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    @TypeConverter
    fun weatherToJson(objects: List<Weather>): String {
        return Gson().toJson(objects)
    }


    @TypeConverter
    fun currentFromJson(jsonString: String): Current {
        return Gson().fromJson(jsonString,Current::class.java)
    }

    @TypeConverter
    fun currentToJson(current: Current): String {
        return Gson().toJson(current)
    }



}