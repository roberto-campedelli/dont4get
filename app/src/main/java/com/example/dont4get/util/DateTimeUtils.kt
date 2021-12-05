package com.example.dont4get.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.S)
fun getNotificationDelay(dateAndTime: String): Long {
    val targetDate = fromStringToDate(dateAndTime = dateAndTime)
    val duration = Duration.between(LocalDateTime.now(), targetDate)
    Log.i("Local date", LocalDateTime.now().toString())
    Log.i("Target", targetDate.toString())
    Log.i("ChronoUnit.MINUTES", duration.seconds.toString())
    return duration.seconds
}

fun fromStringToDate(dateAndTime: String): LocalDateTime {
    // io receive the date in this format '2011/12/03-10:15'
    //i put the date in this format '2011-12-03T10:15:30'
    val newDateAndTime = dateAndTime.trim().plus(":00").replace("-", "T").replace("/", "-")
    return LocalDateTime.parse(newDateAndTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

@RequiresApi(Build.VERSION_CODES.O)
fun checkIfDateIsValid(dateAndTime: String): Boolean {
    return fromStringToDate(dateAndTime = dateAndTime).isBefore(LocalDateTime.now())
}