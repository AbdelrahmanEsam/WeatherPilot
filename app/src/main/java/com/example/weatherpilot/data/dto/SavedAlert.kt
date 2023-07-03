package com.example.weatherpilot.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alerts")
data class SavedAlert(@PrimaryKey(autoGenerate = true)
                                val id : Int = 0,
                      val arabicName : String,
                      val englishName : String, val longitude : String,
                      val latitude : String,
                      val time : Long,
                      val message: String
                                )