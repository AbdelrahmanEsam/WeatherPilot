package com.example.weatherpilot.presentation.notification

import com.example.weatherpilot.domain.model.AlertItem

data class AlertsAndNotificationsState(val alertsAndNotificationsList : List<AlertItem> = emptyList())
