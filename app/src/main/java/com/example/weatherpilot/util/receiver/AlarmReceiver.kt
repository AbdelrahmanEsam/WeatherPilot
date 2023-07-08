package com.example.weatherpilot.util.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem
import com.example.weatherpilot.domain.usecase.alerts.DeleteAlertUseCase
import com.example.weatherpilot.domain.usecase.network.GetWeatherDataUseCase
import com.example.weatherpilot.domain.usecase.datastore.ReadStringFromDataStoreUseCase
import com.example.weatherpilot.util.coroutines.broadcastScope
import com.example.weatherpilot.util.hiltanotations.Dispatcher
import com.example.weatherpilot.util.hiltanotations.Dispatchers
import com.example.weatherpilot.util.usescases.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {


    @Inject
    lateinit var notificationManager: NotificationManager


    @Inject
    lateinit var deleteAlertUseCase: DeleteAlertUseCase


    @Inject
    lateinit var getWeatherDataUseCase: GetWeatherDataUseCase

    @Inject
    lateinit var readStringFromDataStoreUseCase: ReadStringFromDataStoreUseCase


    @Inject
    @Dispatcher(Dispatchers.IO)
    lateinit var ioDispatcher: CoroutineDispatcher


    @Inject
    @Dispatcher(Dispatchers.Main)
    lateinit var mainDispatcher: CoroutineDispatcher


    override fun onReceive(context: Context, intent: Intent) = broadcastScope(mainDispatcher) {

        val alertItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                context.getString(R.string.broadcast_item),
                AlertItem::class.java
            )
        } else {
            intent.getParcelableExtra(context.getString(R.string.broadcast_item))
        }
        alertItem?.let {

        deleteAlert(alertItem)
        }
        withContext(ioDispatcher) {
            val notificationEnabledResponse =
                readStringFromDataStoreUseCase.execute<String?>(context.getString(R.string.notificationtype))
                    .first()

            if (notificationEnabledResponse is Response.Success) {
                if (notificationEnabledResponse.data == context.getString(R.string.enabled_type) || notificationEnabledResponse.data.isNullOrEmpty()) {
                    withContext(mainDispatcher) {


                        if (!Settings.canDrawOverlays(context)) {
                            val settingsIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            settingsIntent.apply {

                                data = Uri.parse("package:" + context.packageName)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(settingsIntent)
                        } else {
                            alertItem?.let {

                                getWeatherAlert(context, alertItem) {
                                    dialogBuilder(context, it)
                                }
                            }
                        }

                   }
                }
            }
        }
    }


    private suspend fun getDataStorePrefLanguage(language: suspend (String?) -> Unit) {
        language(readStringFromDataStoreUseCase.execute<String?>("languageType").first().data)
    }

    private fun getWeatherAlert(
        context: Context,
        alertItem: AlertItem,
        alertMessageCallback: suspend (AlertItem) -> Unit
    ) {
        broadcastScope(ioDispatcher) {
            getDataStorePrefLanguage {

                val response = getWeatherDataUseCase.execute(
                    alertItem.longitude,
                    alertItem.latitude,
                    it ?: context.getString(R.string.en)
                )

                val updatedAlert = response.first().data?.description?.let { desc ->
                    alertItem.copy(message = desc)
                }
                alertMessageCallback(updatedAlert ?: alertItem)
            }

        }
    }


    private suspend fun dialogBuilder(context: Context, alertItem: AlertItem) {

        val mediaPlayer = MediaPlayer.create(
            context, RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )
        )
        mediaPlayer.start()


        withContext(mainDispatcher) {
            val alertDialog: AlertDialog.Builder =
                AlertDialog.Builder(ContextThemeWrapper(context, R.style.myDialog))
            alertDialog.apply {

                setTitle(context.getString(R.string.weather_alert))
                setMessage(alertItem.message)


                setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                    mediaPlayer.stop()

                }
            }
            val dialog: AlertDialog = alertDialog.create()
            dialog.window?.apply {
                setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                setGravity(Gravity.TOP)
            }
            dialog.show()
        }


    }


    private fun deleteAlert(alertItem: AlertItem) {
        broadcastScope(ioDispatcher) {
            deleteAlertUseCase.execute(alertItem)
        }
    }
}