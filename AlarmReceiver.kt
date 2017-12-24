package ru.artembotnev.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

/**
 * Created by Artem Botnev on 20.11.2017.
 */

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private val NOTIFICATION_ID = 200 // unique number of notification
        private val TITLE = "name"
        private val TIME = "time"
        private val NUMBER = "number"
        private val CHANNEL_ID = "ru.artembotnev.tasks.notification_channel"

        fun createIntent(context: Context, title: String, time: String, strConst: String,
                         amount: Int): Intent {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.action = strConst
            intent.putExtra(TITLE, title)
                    .putExtra(TIME, time)
                    .putExtra(NUMBER, amount)

            return intent
        }

        fun createIntent(context: Context, strConst: String): Intent {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.action = strConst

            return intent
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        fun unpack(const: String) = intent.getStringExtra(const)
        val context = context.applicationContext
        val name = unpack(TITLE) //event's name
        val time = unpack(TIME) //event's time
        val amount = intent.getIntExtra(NUMBER, 0) // number of notifications

        startMessage(name, time, amount, context)
    }

    private fun startMessage(nameTask: String, timeTask: String,
                             amountTask: Int, context: Context) {

        val amountTask = amountTask % 5 // no more than 5 messages on the screen

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setTicker(nameTask)
                .setSmallIcon(R.mipmap.ic_icon) //icon
                .setContentTitle(timeTask) //show event time (needs formatting)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND) // default notification ringtone
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID * amountTask, notification)
    }
}