package com.example.weatherpilot.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Location(val id : Int = 0 ,val arabicName : String , val englishName : String,val longitude : String , val latitude : String) :
    Parcelable