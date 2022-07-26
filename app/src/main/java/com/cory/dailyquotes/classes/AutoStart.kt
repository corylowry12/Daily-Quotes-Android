package com.cory.dailyquotes.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AutoStart : BroadcastReceiver() {
    var alarm = AppWidgetAlarm()
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
           // alarm.setAlarm(context!!)
        }
    }
}