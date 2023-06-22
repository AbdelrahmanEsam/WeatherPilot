package com.example.weatherpilot.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.timestampToDate(): String {
  return  LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault()).toString()
}