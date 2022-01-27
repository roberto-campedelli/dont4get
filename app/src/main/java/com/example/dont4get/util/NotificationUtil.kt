package com.example.dont4get.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.*
import com.example.dont4get.CHANNEL_ID
import com.example.dont4get.MainActivity
import com.example.dont4get.R
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.S)
fun setAlarm(context: Context, millis: Long, memoName: String) {
    val alarmManager = context.getSystemService(ComponentActivity.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val data = memoName.toUri()
    intent.data = data
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
}

@RequiresApi(Build.VERSION_CODES.S)
fun setPeriodicAlarm(context: Context, millis: Long, memoName: String) {
    val alarmManager = context.getSystemService(ComponentActivity.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val data = memoName.toUri()
    intent.data = data
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    val oneWeekInMillis = TimeUnit.DAYS.toMillis(7)
    Log.i("one week in millis ", oneWeekInMillis.toString())
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        millis,
        AlarmManager.INTERVAL_DAY * 7,
        pendingIntent
    )
}

@RequiresApi(Build.VERSION_CODES.S)
fun setWeeklyMemosAlarm(delays: List<Long>, context: Context, memoName: String) {

    for (delay in delays) {
        setPeriodicAlarm(context, delay, memoName = memoName)
    }

}

fun cancelAlarm(context: Context, memoName: String) {
    val alarmManager = context.getSystemService(ComponentActivity.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val data = memoName.toUri()
    intent.data = data
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
    alarmManager.cancel(pendingIntent)
}

class OneTimeScheduleWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Don't forget to ")
            .setContentText(inputData.getString("MESSAGE"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(), builder.build())
        }

        return Result.success()
    }

}

fun scheduleOneTimeNotification(initialDelay: Long, context: Context, memoName: String) {

    val data = workDataOf("MESSAGE" to memoName)

    val work =
        OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
            .setInitialDelay(initialDelay, TimeUnit.SECONDS)
            .setInputData(data)
            .build()

    WorkManager.getInstance(context).enqueueUniqueWork(memoName, ExistingWorkPolicy.REPLACE, work)
}

fun schedulePeriodicNotifications(initialDelay: Long, context: Context, memoName: String) {

    val data = workDataOf("MESSAGE" to memoName)

    val periodicWork =
        PeriodicWorkRequestBuilder<OneTimeScheduleWorker>(
            7, TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.SECONDS)
            .setInputData(data)

            .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            memoName,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWork
        )
}

fun setWeeklyMemos(delays: List<Long>, context: Context, memoName: String) {

    for (delay in delays) {
        schedulePeriodicNotifications(delay, context, memoName = memoName)
    }

}

fun cancelNotification(context: Context, memoName: String) {
    WorkManager.getInstance(context).cancelUniqueWork(memoName)
}