package com.example.weatherpilot.data.mappers

import com.example.weatherpilot.data.dto.SavedAlert
import com.example.weatherpilot.domain.model.AlertItem


fun AlertItem.toSavedAlert(): SavedAlert {
    return SavedAlert(
        id = alarmId,
        arabicName = arabicName,
        englishName = englishName,
        longitude = longitude,
        latitude = latitude,
        message = message,
        time = time,
        kind = kind,
        scheduled = scheduled


    )
}


fun SavedAlert.toAlertItem(): AlertItem {
    return AlertItem(
        alarmId = id,
        arabicName = arabicName,
        englishName = englishName,
        longitude = longitude,
        latitude = latitude,
        message = message,
        time = time,
        kind = kind,
        scheduled = scheduled


    )
}