package com.example.dont4get.util

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dont4get.CHANNEL_ID
import com.example.dont4get.R
import com.example.dont4get.data.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random


class RetrieveAlarmBootReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            val memoRepository =
                MemoRepository(MemoDatabase.getInstance(context = context).memoDao())

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Don't forget to ")
                .setContentText(intent.dataString)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            with(NotificationManagerCompat.from(context)) {
                notify(Random.nextInt(), builder.build())
            }

            val memoList = MemoDatabase.getInstance(context = context).memoDao().listAll()
            Toast.makeText(context, "BOOT COMPLETED!", Toast.LENGTH_LONG).show()
            for (memo in memoList.value!!) {
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

        }
    }
}
