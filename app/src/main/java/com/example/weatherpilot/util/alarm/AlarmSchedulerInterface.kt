package com.example.weatherpilot.util.alarm

import com.example.weatherpilot.domain.model.AlarmItem

interface AlarmSchedulerInterface {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}