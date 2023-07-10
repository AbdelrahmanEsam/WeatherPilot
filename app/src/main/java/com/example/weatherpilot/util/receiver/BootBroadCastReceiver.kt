package com.example.weatherpilot.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.usecase.alerts.GetAllAlertsUseCase
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import com.example.weatherpilot.util.alarm.AlarmSchedulerInterface
import com.example.weatherpilot.util.coroutines.broadcastScope
import com.example.weatherpilot.util.usescases.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class BootBroadCastReceiver : BroadcastReceiver()  {


    @Inject
    lateinit var getAllAlertsUseCase: GetAllAlertsUseCase

    @Inject
    lateinit var alarmScheduler : AlarmSchedulerInterface

    @Inject
    @Dispatcher(Dispatchers.IO)lateinit var ioDispatcher: CoroutineDispatcher
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED)
        {
               getAndScheduleAllAlerts()
        }
    }

    private fun getAndScheduleAllAlerts()
    {
        broadcastScope(ioDispatcher) {
            getAllAlertsUseCase.execute().collectLatest { response ->
                    alarmScheduler.schedule(response)
            }
        }
    }
}