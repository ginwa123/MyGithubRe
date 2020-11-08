package com.m.ginwa.dicoding.mygithubre.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.m.ginwa.dicoding.mygithubre.R
import com.m.ginwa.dicoding.mygithubre.ui.MainActivity
import java.util.*


const val TIME_FORMAT = "HH:mm"
const val CALENDAR_MINUTE = "calendarHour"
const val CALENDAR_HOUR_OF_DAY = "calendarMinute"
const val CHANNEL_ID = "Channel_1"
const val CHANNEL_NAME = "AlarmManager channel"
const val REPEATING_CODE: Int = 213
const val TYPE = "repeat alarm type"

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt(CALENDAR_HOUR_OF_DAY, 0))
        calendar.set(Calendar.MINUTE, sharedPreferences.getInt(CALENDAR_MINUTE, 0))
        val type = intent.getStringExtra(TYPE)
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // handle alarm on boot
            setRepeatingAlarm(context, calendar)
        } else if (type == "alarm repeat") {
            showAlarmNotification(context)
            setRepeatingAlarm(context, calendar)
        }
    }

    fun setRepeatingAlarm(context: Context, calendar: Calendar) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(TYPE, "alarm repeat")
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REPEATING_CODE,
            intent,
            0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (calendar.before(Calendar.getInstance())) calendar.timeInMillis += AlarmManager.INTERVAL_DAY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REPEATING_CODE, intent, 0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }

    private fun showAlarmNotification(
        context: Context
    ) {
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.reminder))
            .setContentText(context.getString(R.string.lets_find_user_again))
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(312, notification)

    }
}
