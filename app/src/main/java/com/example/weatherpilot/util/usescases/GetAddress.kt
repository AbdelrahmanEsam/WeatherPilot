package com.example.weatherpilot.util.usescases

import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

 fun Geocoder.getAddress(
    latitude: Double,
    longitude: Double,
    address: (Flow<Address?>) -> Unit
) {

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) {
                address(flowOf(it[0]))
            }
        } else {
            address(flowOf(getFromLocation(latitude, longitude, 1)?.get(0)))
        }
    } catch (e: Exception) {
        address(emptyFlow())
    }
}



fun Geocoder.getAddress(
    locationName : String,
    address: (Flow<List<Address?>>) -> Unit
) {

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocationName(locationName, 20) { addresses ->
                address(flowOf(addresses))
            }
        } else {
            address(flowOf(getFromLocationName(locationName,20)) as Flow<List<Address?>>)
        }
    } catch (e: Exception) {
        address(emptyFlow())
    }
}