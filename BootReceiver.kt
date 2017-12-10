package ru.artembotnev.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Artem Botnev on 22.11.2017.
 */

class BootReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "android.intent.action.BOOT_COMPLETED")
            TaskAlarmer(context)
    }
}
