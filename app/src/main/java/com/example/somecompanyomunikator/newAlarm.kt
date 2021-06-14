package com.example.somecompanyomunikator

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class newAlarm {

        @RequiresApi(Build.VERSION_CODES.O)
        fun date(): Calendar {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val second = c.get(Calendar.SECOND)
            val date = Calendar.Builder().setDate(year, month, dayOfMonth).setTimeOfDay(hour, minute, second).build()
            return date
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun setAlarm(alarmManager: AlarmManager, alarmIntent: PendingIntent){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date().timeInMillis + (1000 * 60 * 30), alarmIntent)
        }

        fun stopAlarm(alarmManager: AlarmManager, alarmIntent: PendingIntent){
            alarmManager.cancel(alarmIntent)
        }

}
