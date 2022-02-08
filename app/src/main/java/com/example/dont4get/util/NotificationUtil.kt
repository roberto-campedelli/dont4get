package com.example.dont4get.util

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.*
import com.example.dont4get.CHANNEL_ID
import com.example.dont4get.MainActivity
import com.example.dont4get.R
import com.example.dont4get.data.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

// Old notification management using WorkManager

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

class RetrieveMemosWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun doWork(): Result {

        val memoList = MemoDatabase.getInstance(context = context).memoDao().listAllReboot()
        for (memo in memoList) {
            Log.i("db result = ", memo.name + memo.date + memo.type)
            if (memo.type == "Once") {
                setAlarm(
                    context = context,
                    millis = System.currentTimeMillis() + getNotificationDelayMillis(
                        memo.date
                    ),
                    memo.name
                )
                Toast.makeText(context, "set alarm on ${memo.date}!", Toast.LENGTH_LONG).show()

            } else if (memo.type == "Weekly") {
                setWeeklyMemosAlarm(
                    getDelayFromDaysAndTimeAlarm(
                        choosenDays = fromStringToBooleanDayList(memo.days),
                        memo.date,
                    ), context, memoName = memo.name
                )
                Toast.makeText(context, "set alarm on ${memo.date}!", Toast.LENGTH_LONG).show()

            }
        }

        return Result.success()
    }
}

fun retrieveMemos(context: Context) {
    val work =
        OneTimeWorkRequestBuilder<RetrieveMemosWorker>()
            .build()

    WorkManager.getInstance(context).enqueue(work)
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