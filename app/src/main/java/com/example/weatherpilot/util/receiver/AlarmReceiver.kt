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
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {


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


    @Inject
    @Dispatcher(Dispatchers.Main)lateinit var mainDispatcher: CoroutineDispatcher
    override fun onReceive(context: Context, intent: Intent) {

        val alertItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                context.getString(R.string.broadcast_item),
                AlertItem::class.java
            )
        } else {
            intent.getParcelableExtra(context.getString(R.string.broadcast_item))
        }




        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
           }else {




            alertItem?.let {
                getWeatherAlert(context,alertItem){
                    dialogBuilder(context, it)
                }
            }


        }
    }



    private suspend fun getDataStorePrefLanguage( language :  suspend (String?) ->Unit )
    {
        language(readStringFromDataStoreUseCase.execute("languageType").first())
    }

    private fun getWeatherAlert(context: Context,alertItem: AlertItem,alertMessageCallback:  suspend  (AlertItem) -> Unit )
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





    private  suspend  fun dialogBuilder(context: Context, alertItem: AlertItem) {

        val mediaPlayer = MediaPlayer.create(
            context, RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )
        )
        mediaPlayer.start()


        withContext(mainDispatcher){
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(ContextThemeWrapper(context,R.style.myDialog))
        alertDialog.setTitle("Alarm")
        alertDialog.setMessage(alertItem?.message)

        alertDialog.setPositiveButton("OK") { _, _ ->
            mediaPlayer.stop()
            deleteAlert(alertItem)
        }
        val dialog: AlertDialog = alertDialog.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialog.show()
        }



    }


    private fun deleteAlert(alertItem: AlertItem)
    {
        broadcastScope(ioDispatcher) {
            deleteAlertUseCase.execute(alertItem)
        }
    }
}