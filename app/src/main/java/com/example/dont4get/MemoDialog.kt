package com.example.dont4get

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.dont4get.data.Memo
import com.example.dont4get.data.MemoViewModel
import com.example.dont4get.util.*
import java.util.*

//enum class reminderType { Once, Weekly}

fun validateName(name: TextFieldValue): Boolean {
    return name.text.isNotBlank()
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SaveMemoDialog(memo: Memo, memoViewModel: MemoViewModel) {

    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var reminderType by remember { mutableStateOf("") }
    var chosenDays by remember { mutableStateOf(fromStringToBooleanDayList(memo.days)) }
    var saveButtonEnabled by remember { mutableStateOf(false) }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        label = { Text(text = "Remind me to...") },
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        singleLine = true,
                        onValueChange = {
                            name = it
                            saveButtonEnabled = validateName(name = name)
                        }
                    )
                    reminderType = memoTypeSelector(memoType = memo.type)
                    memo.type = reminderType
                    when (reminderType) {
                        "Once" -> {
                            val date = datePicker("")
                            val time = if (date.isNotBlank()) {
                                timePickerWithValidation(date = date, prevTime = "")
                            } else timePicker("")
                            memo.date = "$date-$time"
                        }
                        "Weekly" -> {
                            chosenDays = dayPicker(chosenDays)
                            memo.days = fromBooleanDayListToString(chosenDays = chosenDays)
                            memo.date = timePicker("")
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        onClick = {
                            openDialog.value = false
                            memoViewModel.deleteMemo(memo)
                            Toast.makeText(context, "Memo Deleted", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Delete")
                    }
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        onClick = {
                            openDialog.value = false
                            memo.name = name.text
                            memoViewModel.addMemo(memo)
                            if (reminderType == "Once") {
                                setAlarm(
                                    context = context,
                                    millis = getNotificationDelayMillis(
                                        memo.date
                                    ),
                                    memo.name
                                )

                            } else if (reminderType == "Weekly") {
                                setWeeklyMemosAlarm(
                                    getDelayFromDaysAndTimeAlarm(
                                        choosenDays = chosenDays,
                                        memo.date,
                                    ), context, memoName = memo.name
                                )
                            }
                        },
                        enabled = saveButtonEnabled

                    ) {
                        Text("Save")
                    }

                }
            },
            properties = DialogProperties(false, false)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun updateMemoDialog(memo: Memo, memoViewModel: MemoViewModel): MemoDialogInfoStatus {
    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue(memo.name)) }
    val reminderType by remember { mutableStateOf(memo.type) }
    var chosenDays by remember { mutableStateOf(fromStringToBooleanDayList(memo.days)) }
    var saveButtonEnabled by remember { mutableStateOf(true) }

    var memoDialogInfoStatus by remember {
        mutableStateOf(
            MemoDialogInfoStatus.SHOW
        )
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                memoDialogInfoStatus = MemoDialogInfoStatus.HIDE
            },
            title = {
                Text(text = "")
            },
            text = {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    OutlinedTextField(
                        value = name,
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        singleLine = true,
                        onValueChange = {
                            name = it
                            saveButtonEnabled = validateName(name = name)
                        }
                    )
                    memo.type = reminderType
                    when (reminderType) {
                        "Once" -> {
                            // if i want a OnceMemo i need the exact date - year/month/day/hour/minute/seconds
                            val date = datePicker(memo.date.substring(0, memo.date.indexOf("-")))
                            val time = if (date.isNotBlank()) {
                                timePickerWithValidation(
                                    date = date,
                                    prevTime = memo.date.substring(memo.date.indexOf("-") + 1)
                                )
                            } else timePicker(memo.date.substring(memo.date.indexOf("-") + 1))
                            // in the case of OnceMemo the memo.date field contains the date and the time in the format year/month/day-hour:minute
                            memo.date = "$date-$time"
                        }
                        "Weekly" -> {
                            // if i want a WeeklyMemo i need the day of the week when i want to be
                            //notified and the time
                            chosenDays = dayPicker(chosenDays)
                            // in the case of WeeklyMemo the memo.date field contains only the time in the format hour:minute
                            memo.date = timePicker(memo.date)
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .padding(15.dp, 0.dp, 15.dp, 15.dp)
                            .weight(1f),
                        onClick = {
                            openDialog.value = false
                            memoDialogInfoStatus = MemoDialogInfoStatus.HIDE
                            cancelAlarm(context = context, memoName = memo.name)
                            //cancelNotification(context = context, memo.name)
                            memoViewModel.deleteMemo(memo)
                            Toast.makeText(context, "Memo Deleted", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Delete")
                    }
                    Button(
                        modifier = Modifier
                            .padding(15.dp, 0.dp, 15.dp, 15.dp)
                            .weight(1f),
                        onClick = {
                            openDialog.value = false
                            memoDialogInfoStatus = MemoDialogInfoStatus.HIDE
                            //if i change the name of the memo i need to delete the notification
                            //about the previous memo and after that i can update the name
                            if (memo.name != name.text)
                                cancelAlarm(context = context, memoName = memo.name)
                            memo.name = name.text
                            memo.days = fromBooleanDayListToString(chosenDays = chosenDays)
                            memoViewModel.updateMemo(memo)
                            if (reminderType == "Once") {
                                setAlarm(
                                    context = context,
                                    millis = getNotificationDelayMillis(
                                        memo.date
                                    ),
                                    memo.name
                                )

                            } else if (reminderType == "Weekly") {
                                setWeeklyMemosAlarm(
                                    getDelayFromDaysAndTimeAlarm(
                                        choosenDays = chosenDays,
                                        memo.date,
                                    ), context, memoName = memo.name
                                )
                            }
                        },
                        enabled = saveButtonEnabled

                    ) {
                        Text("Update")
                    }

                }
            },
        )
    }
    return memoDialogInfoStatus
}


@Composable
fun memoTypeSelector(memoType: String): String {

    val radioOptions = listOf("Once", "Weekly")
    var initialValue = radioOptions[0]
    if (memoType in radioOptions) {
        initialValue = memoType
    }
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(initialValue) }

    Row(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .height(50.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .weight(0.33f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) },
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                )
            }
        }
    }
    return selectedOption
}

@Composable
fun datePicker(prevDate: String): String {

    val context = LocalContext.current
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    var date by remember { mutableStateOf(prevDate) }

    val dataPickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date = "%4d/%02d/%02d".format(year, month + 1, dayOfMonth)
        }, year, month, day
    )

    dataPickerDialog.datePicker.minDate = calendar.time.time

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { dataPickerDialog.show() },
        ) {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }

        Text(
            text = date,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 3.dp)
                .clickable { dataPickerDialog.show() })
    }
    return date
}


@Composable
fun timePicker(prevTime: String): String {

    val context = LocalContext.current

    val hour: Int
    val min: Int

    val calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    min = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf(prevTime) }
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, min: Int ->
            time = "%02d:%02d".format(hour, min)
        }, hour, min, true
    )


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { timePickerDialog.show() },
        ) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }
        Text(
            text = time,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 3.dp)
                .clickable { timePickerDialog.show() })
    }
    return time
}

@Composable
fun timePickerWithValidation(date: String, prevTime: String): String {

    val context = LocalContext.current

    val timeNotValidColor = Color(0xFFF44336)
    val timeValidColor = Color(0xFF4f4f4f)
    var isTimeValid by remember { mutableStateOf(true) }

    val hour: Int
    val min: Int

    val calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    min = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf(prevTime) }
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, min: Int ->
            time = "%02d:%02d".format(hour, min)
        }, hour, min, true
    )

    if (time.isNotBlank()) {
        if (!isDateAndTimeValid(dateAndTime = "$date-$time")) {
            isTimeValid = false
            Toast.makeText(
                context,
                "Unfortunately you can't go back in time, pick a valid date!",
                Toast.LENGTH_SHORT
            ).show()
        } else
            isTimeValid = true
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { timePickerDialog.show() },
        ) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }
        Text(
            text = time,
            fontSize = 20.sp,
            color = if (isTimeValid) timeValidColor else timeNotValidColor,
            modifier = Modifier
                .padding(top = 3.dp)
                .clickable { timePickerDialog.show() })
    }

    return time
}

@Composable
fun dayPicker(daysList: List<Boolean>): List<Boolean> {

    var mon by remember { mutableStateOf(daysList[0]) }
    var tue by remember { mutableStateOf(daysList[1]) }
    var wed by remember { mutableStateOf(daysList[2]) }
    var thu by remember { mutableStateOf(daysList[3]) }
    var fri by remember { mutableStateOf(daysList[4]) }
    var sat by remember { mutableStateOf(daysList[5]) }
    var sun by remember { mutableStateOf(daysList[6]) }

    val checkedColor = Color(0xFF03DAC5)
    val uncheckedColor = Color(0xFFB0BEC5)

    val checkedSize = 25.sp
    val uncheckedSize = 20.sp

    val dayNameListEng = listOf("M", "T", "W", "T", "F", "S", "S")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        IconToggleButton(
            checked = mon, onCheckedChange = { mon = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (mon) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[0],
                color = tint,
                fontSize = if (mon) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(
            checked = tue, onCheckedChange = { tue = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (tue) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[1],
                color = tint,
                fontSize = if (tue) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(
            checked = wed, onCheckedChange = { wed = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (wed) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[2],
                color = tint,
                fontSize = if (wed) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(
            checked = thu, onCheckedChange = { thu = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (thu) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[3],
                color = tint,
                fontSize = if (thu) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(
            checked = fri, onCheckedChange = { fri = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (fri) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[4],
                color = tint,
                fontSize = if (fri) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(
            checked = sat, onCheckedChange = { sat = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (sat) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[5],
                color = tint,
                fontSize = if (sat) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(
            checked = sun, onCheckedChange = { sun = it }, modifier = Modifier.weight(
                0.143F
            )
        ) {
            val tint by animateColorAsState(if (sun) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[6],
                color = tint,
                fontSize = if (sun) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )
        }
    }

    return listOf(mon, tue, wed, thu, fri, sat, sun)

}
