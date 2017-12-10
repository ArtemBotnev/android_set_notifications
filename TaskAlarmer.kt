package ru.artembotnev.tasks

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import java.util.*

/**
 * Created by Artem Botnev on 17.11.2017.
 * Invoke instance of this class when you need to set time notification
 */

class TaskAlarmer(val context: Context) {
    private val ALERT_CONST = "ru.artembotnev.tasks.WAKE"
    private val NUMBER_INTENTS = "number_intents"
    init {
        AlarmAsyncTask().execute()
    }

    private fun makeAlerts(){
        //getting list of tasks through singleton (just list of tasks)
        var alertList = TaskLab.get(context).tasks

        if(alertList.isEmpty()) //there aren't tasks, exit
            return

        val realTime = Calendar.getInstance().timeInMillis //current time
        //select tasks those have active notification preference
        alertList = alertList.asSequence()
                .filter { it.isAlertActive }
                .filter { it.alertDate!!.time > realTime}
                .toList()

        if(alertList.isEmpty()) //there aren't such tasks, exit
            return

        var alertTime: Long //time when notification should come (milliseconds)
        var alertsCount = 0 // number of alerts from all tasks with alert which will be done
        //here
        for (task in alertList){
            alertsCount++
            alertTime = task.alertDate!!.time
            // intent for Alarm receiver
            val intent = AlarmReceiver.createIntent(context, task.title,
                    task.alertDate.toString(), ALERT_CONST, alertsCount)
            sendIntent(context, alertsCount, intent, alertTime)
        }
        
        PreferenceManager.getDefaultSharedPreferences(context) // save intents number
                .edit()
                .putInt(NUMBER_INTENTS, alertsCount)
                .apply()
    }

    //create pending intents
    private fun sendIntent(context: Context, number: Int, intent: Intent, dateMillis: Long){
        val send = PendingIntent.getBroadcast(context,
                number, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val version = Build.VERSION.SDK_INT //user's build version
        when{
            version < 19 -> alarmManager.set(AlarmManager.RTC_WAKEUP, dateMillis, send)
            version < 24 -> alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateMillis, send)
            else -> alarmManager
                    .setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateMillis, send)
        }
    }

    private fun clearReceiver(){
        val intentsNumber = PreferenceManager    //get number of pending intents
                .getDefaultSharedPreferences(context)
                .getInt(NUMBER_INTENTS, 0)
        val intent = AlarmReceiver.createIntent(context, ALERT_CONST)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var i = 0
        while (i < intentsNumber){
            i++
            val pendingIntent = PendingIntent.getBroadcast(context, i, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent) //remove from alarm manager
            pendingIntent.cancel() //remove pending intent
        }
    }

    private inner class AlarmAsyncTask: AsyncTask<Void?, Void?, Void?>(){
        override fun doInBackground(vararg args: Void?): Void? {
            clearReceiver() //clear old intents from receiver
            makeAlerts() //send new intents
            return null
        }
    }
}