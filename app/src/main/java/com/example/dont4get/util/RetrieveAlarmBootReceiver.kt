package com.example.dont4get.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.dont4get.data.MemoDatabase

class RetrieveAlarmBootReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
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

