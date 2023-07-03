package com.example.weatherpilot.data.mappers

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.domain.model.AlertItem


fun AlertItem.toSavedAlert() : SavedAlert
{
 return  SavedAlert(arabicName = arabicName, englishName = englishName, longitude = longitude, latitude = latitude, message = message, time = time)
}


fun SavedAlert.toAlertItem() : AlertItem
{
 return AlertItem(arabicName = arabicName, englishName = englishName, longitude = longitude, latitude = latitude, message = message, time = time)
}