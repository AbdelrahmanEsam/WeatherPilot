package com.example.weatherpilot.presentation.notification

import com.example.weatherpilot.domain.model.AlertItem

interface NotificationIntent {

    object GetAllAlertsNotifications : NotificationIntent

    data class DeleteAlert(val item: AlertItem) : NotificationIntent
}