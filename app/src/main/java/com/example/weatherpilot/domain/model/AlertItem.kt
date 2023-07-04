package com.example.weatherpilot.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime



@Parcelize
data class AlertItem(
    val alarmId : Int,
    val arabicName: String,
    val englishName: String,
    val longitude: String,
    val latitude: String,
    val time: Long = -1,
    val message: String = "",
    val kind : String,
    val  scheduled : Boolean = false
) : Parcelable