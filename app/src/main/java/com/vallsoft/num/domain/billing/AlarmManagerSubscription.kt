package com.vallsoft.num.domain.billing

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock


class AlarmManagerSubscription private constructor(context: Context) {
    private val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val intent: Intent
    private val alarmIntent: PendingIntent
    private var isAlarming = false

    companion object {

        @Volatile
        private var INSTANCE: AlarmManagerSubscription? = null

        fun getInstance(context: Context): AlarmManagerSubscription =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: AlarmManagerSubscription(context).also { INSTANCE = it }
                }
    }

    init {
        intent = Intent(context, BillingListener::class.java)
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent)
    }

    fun startAlarm() {
        if (!isAlarming) {
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 3000,
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent)
//            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3000,
//                    3000, alarmIntent)
            isAlarming = true
        }
    }

    fun stopAlarm() {
        if (isAlarming) {
            alarmMgr.cancel(alarmIntent)
            isAlarming = false
        }
    }

    class BootReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
                //TODO loading with system
                //  AlarmManagerSubscription(context!!).startAlarm()
            }
        }

    }
}