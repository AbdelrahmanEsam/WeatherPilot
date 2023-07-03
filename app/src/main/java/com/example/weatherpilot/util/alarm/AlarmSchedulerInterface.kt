package com.example.weatherpilot.util.alarm

import com.example.weatherpilot.domain.model.AlertItem

interface AlarmSchedulerInterface {
    fun schedule(item: AlertItem)
    fun cancel(item: AlertItem)
}