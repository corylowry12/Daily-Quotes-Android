package com.cory.dailyquotes

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.RemoteViews
import android.widget.Toast
import java.util.*
import kotlin.random.Random


/**
 * Implementation of App Widget functionality.
 */
class InspirationalWidget1 : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val thisWidget = ComponentName(
            context,
            InspirationalWidget1::class.java
        )

        for (widgetId in appWidgetManager.getAppWidgetIds(thisWidget)) {

            //Get the remote views
            val remoteViews = RemoteViews(
                context.packageName,
                R.layout.inspirational_widget1
            )
            val count = QuotesDBHelper(context, null).getAllRow()

            if (count!!.count > 1) {
                val randomNumber = (1..count.count).random()

                val cursor: Cursor = QuotesDBHelper(context, null).getRow(randomNumber.toString())
                cursor.moveToFirst()

                val cursorPeople = PeopleDBHelper(context, null).getRow(cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_PERSON_ID)))
                cursorPeople.moveToFirst()
                val people = cursorPeople.getString(cursorPeople.getColumnIndex(PeopleDBHelper.COLUMN_NAME))

                val randomQuote =
                    cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_QUOTE)) + "\n- $people"
                // Set the text with the current time.
                remoteViews.setTextViewText(
                    R.id.appwidget_text,
                    randomQuote.toString()
                )
            }
            else if (count.count == 1) {
                val cursor: Cursor = QuotesDBHelper(context, null).getRow("1")
                cursor.moveToFirst()

                val cursorPeople = PeopleDBHelper(context, null).getRow(cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_PERSON_ID)))
                cursorPeople.moveToFirst()
                val people = cursorPeople.getString(cursorPeople.getColumnIndex(PeopleDBHelper.COLUMN_NAME))

                val randomQuote =
                    cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_QUOTE)) + "\n- $people"
                // Set the text with the current time.
                remoteViews.setTextViewText(
                    R.id.appwidget_text,
                    randomQuote.toString()
                )
            }
            else {
                remoteViews.setTextViewText(
                    R.id.appwidget_text,
                    "There are no quotes stored"
                )
            }
            remoteViews.setOnClickPendingIntent(R.id.backgroundImage, getPendingSelfIntent(context))
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    override fun onEnabled(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AppWidgetAlarm::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 24*60*60*1000, pi)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.inspirational_widget1)
        val count = QuotesDBHelper(context, null).getAllRow()

        if (count!!.count > 1) {
            val randomNumber = (1..count.count).random()
            val cursor: Cursor = QuotesDBHelper(context, null).getRow(randomNumber.toString())
            cursor.moveToFirst()
            val cursorPeople = PeopleDBHelper(context, null).getRow(cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_PERSON_ID)))
            cursorPeople.moveToFirst()
            val people = cursorPeople.getString(cursorPeople.getColumnIndex(PeopleDBHelper.COLUMN_NAME))

            val randomQuote =
                cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_QUOTE)) + "\n- $people"
            // Set the text with the current time.
            views.setTextViewText(
                R.id.appwidget_text,
                randomQuote.toString()
            )
        }
        else if (count.count == 1) {
            val cursor: Cursor = QuotesDBHelper(context, null).getRow("1")
            cursor.moveToFirst()
            val cursorPeople = PeopleDBHelper(context, null).getRow(cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_PERSON_ID)))
            cursorPeople.moveToFirst()
            val people = cursorPeople.getString(cursorPeople.getColumnIndex(PeopleDBHelper.COLUMN_NAME))

            val randomQuote =
                cursor.getString(cursor.getColumnIndex(QuotesDBHelper.COLUMN_QUOTE)) + "\n- $people"
            // Set the text with the current time.
            views.setTextViewText(
                R.id.appwidget_text,
                randomQuote.toString()
            )
        }
        else {
            views.setTextViewText(
                R.id.appwidget_text,
                "There are no quotes stored"
            )
        }
        views.setOnClickPendingIntent(R.id.backgroundImage, getPendingSelfIntent(context))
    }

    override fun onDisabled(context: Context) { // Enter relevant functionality for when the last widget is disabled

        val intent = Intent(context, AppWidgetAlarm::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
        super.onDisabled(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        //Toast.makeText(context, "called", Toast.LENGTH_SHORT).show()
    }
}

fun getPendingSelfIntent(context: Context) : PendingIntent {

    /*val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java
    ).putExtra("widget", true).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_IMMUTABLE)*/
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("widget", "Widget")
    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    val pendingIntent : PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getActivity(context, 0, intent, 0)
    }
    return pendingIntent
}