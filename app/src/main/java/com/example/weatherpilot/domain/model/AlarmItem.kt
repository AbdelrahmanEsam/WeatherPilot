package com.example.weatherpilot.domain.model

import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val message: String
)