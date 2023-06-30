package com.example.weatherpilot.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadCastReceiver : BroadcastReceiver()  {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED)
        {
                 //todo
        }
    }
}