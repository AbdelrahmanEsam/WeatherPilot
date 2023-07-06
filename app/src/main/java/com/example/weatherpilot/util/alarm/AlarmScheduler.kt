package com.example.weatherpilot.util.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.util.receiver.AlarmReceiver
import com.example.weatherpilot.util.receiver.NotificationReceiver
import javax.inject.Inject

class AlarmScheduler(
    private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmSchedulerInterface {





    @SuppressLint("MissingPermission")
    override fun schedule(items: List<AlertItem>) {
        Log.d("alarm scheduler","alarm scheduler")
        items.forEach { item ->
            val intent = Intent(
                context,
                if (item.kind == context.getString(R.string.alarm))
                    AlarmReceiver::class.java else AlarmReceiver::class.java
            ).apply {
                putExtra(context.getString(R.string.broadcast_item), item)
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.time,
                PendingIntent.getBroadcast(
                    context,
                    item.alarmId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(item: AlertItem) {

        val intent = Intent(
            context,
            if (item.kind == context.getString(R.string.alarm))
                AlarmReceiver::class.java else NotificationReceiver::class.java
        )

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.alarmId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}