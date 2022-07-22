package com.cory.dailyquotes

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.os.SystemClock
import android.widget.RemoteViews
import android.widget.Toast
import java.util.*


class AppWidgetAlarm : BroadcastReceiver() {

    @Override
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG")
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        val remoteViews = RemoteViews(context.getPackageName(),
            R.layout.inspirational_widget1);
        remoteViews.setTextViewText(R.id.appwidget_text, SystemClock.uptimeMillis().toString());
        val thiswidget = ComponentName(context, InspirationalWidget1::class.java)
        val manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(thiswidget, remoteViews);
        //Release the lock
        wl.release();
    }
}