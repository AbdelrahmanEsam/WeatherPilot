package com.example.weatherpilot.util.alarm

import android.annotation.SuppressLint
import com.example.weatherpilot.domain.model.AlertItem

interface AlarmSchedulerInterface {

    fun cancel(item: AlertItem)
    @SuppressLint("MissingPermission")
    fun schedule(items: List<AlertItem>)
}