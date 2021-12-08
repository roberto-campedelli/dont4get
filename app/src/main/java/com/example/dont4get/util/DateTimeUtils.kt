package com.example.dont4get.util

import android.os.Build
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

/*// from string 2011/12/03-10:15 to LocalDateTime '2011-12-03T10:15:30'
fun fromStringToTime(date: String): LocalTime {
    // io receive the date in this format '2011/12/03-10:15'
    //i put the date in this format '2011-12-03T10:15:30'
    val newDate = date.trim().replace("/", "-")
    return LocalDate.parse(newDate, DateTimeFormatter.ISO_LOCAL_DATE)
}*/

fun isDateValid(date: String): Boolean {
    return fromStringToDate(date = date).isAfter(LocalDate.now()) or fromStringToDate(date = date).isEqual(
        LocalDate.now()
    )
}

fun isDateAndTimeValid(dateAndTime: String): Boolean {
    return fromStringToDateTime(dateAndTime = dateAndTime).isAfter(LocalDateTime.now())
}

// function to get the right delay from the list of choosen days and the time
fun getDelayFromDaysAndTime(choosenDays: List<Boolean>, time: String): List<Long> {

    val targetTime = LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME)
    val now = LocalDateTime.now()
    val currentDay = now.dayOfWeek.value
    val targetDataTimes = mutableListOf<LocalDateTime>()
    val delays = mutableListOf<Long>()

    var dayIndex: Int

    for (day in choosenDays) {
        // if day is true means that the user tap that day and i have to schedule a notification
        // for that day and the time specified
        val targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime)
        if (day) {
            // i need to check if the day choosed is already passed in the week or not
            dayIndex = choosenDays.indexOf(day)
            if ((dayIndex > currentDay) || (dayIndex == currentDay && targetDateTime.isBefore(now))) {
                targetDateTime.plusDays((dayIndex - currentDay).toLong())
            }
            // if the day is already passed i need to schedule a notification for the next week
            else if (dayIndex < currentDay) {
                targetDateTime.plusWeeks(1L).minusDays((currentDay - dayIndex).toLong())
            }
            // if dayIndex == current day the targetDataTime date is already OK
            targetDataTimes.add(targetDateTime)
        }

    }

    for (targetDataTime in targetDataTimes) {
        delays.add(Duration.between(now, targetDataTime).seconds)
    }

    return delays

}

