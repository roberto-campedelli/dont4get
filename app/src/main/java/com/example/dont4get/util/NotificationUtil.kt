package com.example.dont4get.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.dont4get.CHANNEL_ID
import com.example.dont4get.MainActivity
import com.example.dont4get.R
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val NOTIFICATION_ID = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(message: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    //todo create a pendingIntent for the action we want to do -> play the message
    /*
    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        REQUEST_CODE,
        snoozeIntent,
        FLAGS)
     */

    var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(message)
        .setContentText(message)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line...")
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(NOTIFICATION_ID, builder.build())

}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}


class OneTimeScheduleWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {


        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Scheduled notification")
            .setContentText("Hello from one-time worker!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(), builder.build())
        }

        return Result.success()
    }

}

fun scheduleOneTimeNotification(initialDelay: Long, context: Context) {
    val work =
        OneTimeWorkRequestBuilder<OneTimeScheduleWorker>()
            .setInitialDelay(initialDelay, TimeUnit.SECONDS)
            .build()

    WorkManager.getInstance(context).enqueue(work)
}

fun schedulePeriodicNotifications(initialDelay: Long, context: Context) {

    val periodicWork =
        PeriodicWorkRequestBuilder<OneTimeScheduleWorker>(
            7, TimeUnit.DAYS
        )
            .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "periodic_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWork
        )
}

fun setWeeklyMemos(delays: List<Long>, context: Context) {

    for (delay in delays) {
        schedulePeriodicNotifications(delay, context)
    }

}