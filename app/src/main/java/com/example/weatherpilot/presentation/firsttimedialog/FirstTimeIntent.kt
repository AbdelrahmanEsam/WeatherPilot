package com.example.weatherpilot.presentation.firsttimedialog

sealed interface FirstTimeIntent
{

    data class NotificationStateChanged(val newNotificationState : String) : FirstTimeIntent
    data class PlaceStateChanged(val gps : String) : FirstTimeIntent
    object Go : FirstTimeIntent


}