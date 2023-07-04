package com.example.weatherpilot.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.weatherpilot.R
import com.example.weatherpilot.domain.model.AlertItem

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val alertItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                context.getString(R.string.broadcast_item),
                AlertItem::class.java
            )
        } else {
            intent.getParcelableExtra(context.getString(R.string.broadcast_item))
        }

        val mediaPlayer = MediaPlayer.create(context, RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_NOTIFICATION))
        mediaPlayer.start()

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog.setTitle("Alarm")
        alertDialog.setMessage(alertItem?.message)
        alertDialog.setPositiveButton("OK") { _, _ ->
            mediaPlayer.stop()
        }
        val dialog: AlertDialog = alertDialog.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialog.show()

    }
}