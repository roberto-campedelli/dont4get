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
import java.io.File
import java.util.*

//enum class reminderState { Once, Weekly, Daily }

fun validateName(name: TextFieldValue): Boolean {
    return name.text.isNotBlank()
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SaveMemo(memo: Memo, file: File, memoViewModel: MemoViewModel) {
    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var reminderType by remember { mutableStateOf("") }
    lateinit var chosenDays: List<Boolean>
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
                    reminderType = MemoRemind(memoType = memo.type)
                    memo.type = reminderType
                    when (reminderType) {
                        "Once" -> {
                            val date = DatePicker("")
                            val time = if (date.isNotBlank()) {
                                TimePickerWithValidation(date = date, time = "")
                            } else TimePicker("")
                            memo.date = "$date-$time"
                        }
                        "Weekly" -> {
                            chosenDays = DayPicker()
                            memo.date = TimePicker("")
                        }
                        "Daily" -> {
                            memo.date = TimePicker("")
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
                            Toast.makeText(context, "memo eliminato", Toast.LENGTH_SHORT).show()
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
                                val delay = getNotificationDelay(memo.date)
                                scheduleOneTimeNotification(delay, context, memo.name)
                            } else if (reminderType == "Weekly") {
                                setWeeklyMemos(
                                    getDelayFromDaysAndTime(
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
fun ShowUpdateMemo(memo: Memo, file: File, memoViewModel: MemoViewModel): MemoDialogInfoStatus {
    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue(memo.name)) }
    var reminderType by remember { mutableStateOf(memo.type) }
    lateinit var chosenDays: List<Boolean>
    var saveButtonEnabled by remember { mutableStateOf(true) }

    var memoDialogInfoStatus by remember {
        mutableStateOf(
            MemoDialogInfoStatus.show
        )
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                memoDialogInfoStatus = MemoDialogInfoStatus.hide
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
                    //reminderType = MemoRemind(memoType = memo.type)
                    memo.type = reminderType
                    when (reminderType) {
                        "Once" -> {
                            // if i want a OnceMemo i need the exact date - year/month/day/hour/minute/seconds
                            val date = DatePicker(memo.date.substring(0, memo.date.indexOf("-")))
                            val time = if (date.isNotBlank()) {
                                TimePickerWithValidation(
                                    date = date,
                                    time = memo.date.substring(memo.date.indexOf("-") + 1)
                                )
                            } else TimePicker(memo.date.substring(memo.date.indexOf("-") + 1))
                            // in the case of OnceMemo the memo.date field contains the date and the time in the format year/month/day-hour:minute
                            memo.date = "$date-$time"
                        }
                        "Weekly" -> {
                            // if i want a WeeklyMemo i need the day of the week when i want to be
                            //notified and the time
                            chosenDays = DayPicker()
                            // in the case of WeeklyMemo the memo.date field contains only the time in the format hour:minute
                            memo.date = TimePicker(memo.date)
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
                            memoDialogInfoStatus = MemoDialogInfoStatus.hide
                            cancelNotification(context = context, memo.name)
                            memoViewModel.deleteMemo(memo)
                            Toast.makeText(context, "memo eliminato", Toast.LENGTH_SHORT).show()
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
                            memoDialogInfoStatus = MemoDialogInfoStatus.hide
                            //if i change the name of the memo i need to delete the notification
                            //about the previous memo and after that i can update the name
                            if (memo.name != name.text)
                                cancelNotification(context = context, memoName = memo.name)
                            memo.name = name.text
                            memoViewModel.updateMemo(memo)
                            if (reminderType == "Once") {
                                val delay = getNotificationDelay(memo.date)
                                scheduleOneTimeNotification(delay, context, memo.name)
                            } else if (reminderType == "Weekly") {
                                setWeeklyMemos(
                                    getDelayFromDaysAndTime(
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
            //properties = DialogProperties(false, false)
        )
    }
    return memoDialogInfoStatus
}


@Composable
fun MemoRemind(memoType: String): String {

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
fun DatePicker(date: String): String {

    val context = LocalContext.current

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    var date by remember { mutableStateOf(date) }

    var dataPickerDialog = DatePickerDialog(
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
fun TimePicker(time: String): String {

    val context = LocalContext.current

    val hour: Int
    val min: Int

    val calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    min = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf(time) }
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
fun TimePickerWithValidation(date: String, time: String): String {

    val context = LocalContext.current

    val timeNotValidColor = Color(0xFFF44336)
    val timeValidColor = Color(0xFF4f4f4f)
    var isTimeValid by remember { mutableStateOf(true) }

    val hour: Int
    val min: Int

    val calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    min = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf(time) }
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
                Toast.LENGTH_LONG
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
fun DayPicker(): List<Boolean> {

    var mon by remember { mutableStateOf(false) }
    var tue by remember { mutableStateOf(false) }
    var wed by remember { mutableStateOf(false) }
    var thu by remember { mutableStateOf(false) }
    var fri by remember { mutableStateOf(false) }
    var sat by remember { mutableStateOf(false) }
    var sun by remember { mutableStateOf(false) }

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
