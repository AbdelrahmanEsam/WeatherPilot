package com.example.weatherpilot.util.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.usecase.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.util.coroutines.Dispatcher
import com.example.weatherpilot.util.coroutines.Dispatchers
import com.example.weatherpilot.util.coroutines.broadcastScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import javax.inject.Inject


@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {


    @Inject
    lateinit var notificationManager: NotificationManager


    @Inject
    lateinit var deleteAlertUseCase: DeleteAlertUseCase


    @Inject
    lateinit var getWeatherDataUseCase: GetWeatherDataUseCase

    @Inject
    lateinit var readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase


    @Inject
    @Dispatcher(Dispatchers.IO)lateinit var ioDispatcher: CoroutineDispatcher

    override fun onReceive(context: Context, intent: Intent) {



        val alertItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                context.getString(R.string.broadcast_item),
                AlertItem::class.java
            )
        } else {
            intent.getParcelableExtra(context.getString(R.string.broadcast_item))
        }
        Log.d("scheduler", "out")
        alertItem?.let {

            getWeatherAlert(context,alertItem){
                notificationBuilder(context, it)
            }


        }

    }


    private suspend fun getDataStorePrefLanguage( language :  suspend (String?) ->Unit )
    {
            language(readStringFromDataStoreUseCase.execute("languageType").first())
    }

    private fun getWeatherAlert(context: Context,alertItem: AlertItem,alertMessageCallback: (AlertItem) -> Unit )
    {
        broadcastScope(ioDispatcher) {
            getDataStorePrefLanguage {

           val response =  getWeatherDataUseCase.execute(alertItem.longitude,alertItem.latitude,it ?: context.getString(R.string.en))

                val updatedAlert = response.first().data?.description?.let {desc ->
                    alertItem.copy(message = desc) }
                alertMessageCallback(updatedAlert ?: alertItem)
            }

        }
    }





    private fun notificationBuilder(context: Context, alertItem: AlertItem) {
        val notification = NotificationCompat
            .Builder(context, context.getString(R.string.weather_alert))
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(context.getString(R.string.weather_state))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentText(with(alertItem) {
                message.ifEmpty {
                    context.getString(
                        R.string.weather_is_fine_no_alerts_for_the_scheduled_period
                    )
                }
            }).build()

        notificationManager.notify(1000, notification)
        deleteAlert(alertItem)

    }


    private fun deleteAlert(alertItem: AlertItem)
    {
        broadcastScope(ioDispatcher) {
            deleteAlertUseCase.execute(alertItem)
        }
    }
}