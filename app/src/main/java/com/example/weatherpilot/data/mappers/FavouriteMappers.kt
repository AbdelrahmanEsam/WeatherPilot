package com.example.weatherpilot.data.mappers

import com.example.weatherpilot.data.dto.FavouriteLocation
import com.example.weatherpilot.domain.model.Location


fun FavouriteLocation.toLocation() : Location
{
    return Location(arabicName,englishName, longitude, latitude)
}

fun  Location.toFavouriteLocation() : FavouriteLocation
{
    return FavouriteLocation(arabicName =  arabicName, englishName = englishName, longitude =  longitude,latitude =  latitude)
}