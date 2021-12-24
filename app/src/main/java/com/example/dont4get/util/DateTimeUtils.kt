package com.example.dont4get.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.S)
fun getNotificationDelay(dateAndTime: String): Long {
    val targetDate = fromStringToDateTime(dateAndTime = dateAndTime)
    val duration = Duration.between(LocalDateTime.now(), targetDate)
    return duration.seconds
}

// from string 2011/12/03-10:15 to LocalDateTime '2011-12-03T10:15:30'
fun fromStringToDateTime(dateAndTime: String): LocalDateTime {
    // io receive the date in this format '2011/12/03-10:15'
    //i put the date in this format '2011-12-03T10:15:30'
    val newDateAndTime = dateAndTime.trim().plus(":00").replace("-", "T").replace("/", "-")
    return LocalDateTime.parse(newDateAndTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

// from string 2011/12/03-10:15 to LocalDateTime '2011-12-03T10:15:30'
fun fromStringToDate(date: String): LocalDate {
    // io receive the date in this format '2011/12/03-10:15'
    //i put the date in this format '2011-12-03T10:15:30'
    val newDate = date.trim().replace("/", "-")
    return LocalDate.parse(newDate, DateTimeFormatter.ISO_LOCAL_DATE)
}

fun isDateAndTimeValid(dateAndTime: String): Boolean {
    return fromStringToDateTime(dateAndTime = dateAndTime).isAfter(LocalDateTime.now())
}

// function to get the right delay from the list of choosen days and the time
fun getDelayFromDaysAndTime(choosenDays: List<Boolean>, time: String): List<Long> {

    val targetTime = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME)
    Log.i("targetTime: ", targetTime.toString())
    Log.i("choosenDays: ", choosenDays.toString())
    val now = LocalDateTime.now()
    val currentDay = now.dayOfWeek.value
    val targetDateTimes = mutableListOf<LocalDateTime>()
    val delays = mutableListOf<Long>()

    var dayIndex: Int

    for (day in choosenDays) {
        // if day is true means that the user tap that day and i have to schedule a notification
        // for that day and the time specified
        var targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime)
        Log.i("targetDateTime: ", targetDateTime.toString())
        Log.i("now: ", now.toString())

        if (day) {
            // i need to check if the day choosed is already passed in the week or not
            dayIndex = choosenDays.indexOf(day) + 1
            Log.i("targetDayIndex: ", dayIndex.toString())
            Log.i("currentDay: ", currentDay.toString())
            if (dayIndex > currentDay || (dayIndex == currentDay && targetDateTime.isAfter(now))) {
                targetDateTime.plusDays((dayIndex - currentDay).toLong())
            }
            // if the day is already passed i need to schedule a notification for the next week
            else if (dayIndex < currentDay || (dayIndex == currentDay && targetDateTime.isBefore(now))) {
                targetDateTime =
                    targetDateTime.plusWeeks(1L).minusDays((currentDay - dayIndex).toLong())
            }
            // if dayIndex == current day the targetDataTime date is already OK
            Log.i("targetDataTime: ", targetDateTime.toString())
            targetDateTimes.add(targetDateTime)
        }

    }

    for (targetDateTime in targetDateTimes) {
        delays.add(Duration.between(now, targetDateTime).seconds)
    }
    for (delay in delays)
        Log.i("delay", delay.toString())

    return delays

}

