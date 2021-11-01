package com.example.dont4get

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun SaveMemo() {
    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var reminderType by remember { mutableStateOf("") }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                if (name.text.isNotEmpty()) {
                    Text(
                        text = name.text,
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = MaterialTheme.typography.h5
                    )
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        label = { Text(text = "Enter Memo Name") },
                        onValueChange = {
                            name = it
                        }
                    )
                    reminderType = MemoRemind()
                    when (reminderType) {
                        "Once" -> {
                            DatePicker()
                            TimePicker()
                        }
                        "Weekly" -> {
                            DayPicker()
                            TimePicker()
                        }
                        "Daily" -> {
                            TimePicker()
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
                        onClick = { openDialog.value = false }
                    ) {
                        Text("Delete")
                    }
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        onClick = { openDialog.value = false }
                    ) {
                        Text("Save")
                    }

                }
            }
        )
    }
}

@Composable
fun MemoRemind(): String {

    val radioOptions = listOf("Once", "Weekly", "Daily")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Row(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .height(60.dp)
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
fun ReminderSwitch() {
    var periodicReminder by remember { mutableStateOf(true) }
    Row() {
        Text(text = "Just once")
        Switch(
            checked = periodicReminder,
            onCheckedChange = { periodicReminder = it }
        )
        Text(text = "Periodic")
    }
}


@Composable
fun DatePicker() {

    val context = LocalContext.current

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    var date by remember { mutableStateOf("Date") }
    val dataPickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date = "$dayOfMonth/$month/$year"
        }, year, month, day
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Text(text = "Select Date: ${date}")
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = { dataPickerDialog.show() }
        ) {
            Text(text = "${date}", fontSize = 20.sp)
        }

    }
}


@Composable
fun TimePicker() {

    val context = LocalContext.current

    val hour: Int
    val min: Int

    val calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    min = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf("Time") }
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, min: Int ->
            time = "$hour:$min"
        }, hour, min, true
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Text(text = "Select Date: ${date}")
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            onClick = { timePickerDialog.show() }
        ) {
            Text(text = "${time}", fontSize = 20.sp)
        }
    }
}

@Composable
fun DayPicker() {

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

    Row() {
        IconToggleButton(checked = mon, onCheckedChange = { mon = it }) {
            val tint by animateColorAsState(if (mon) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[0],
                color = tint,
                fontSize = if (mon) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(checked = tue, onCheckedChange = { tue = it }) {
            val tint by animateColorAsState(if (tue) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[1],
                color = tint,
                fontSize = if (tue) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(checked = wed, onCheckedChange = { wed = it }) {
            val tint by animateColorAsState(if (wed) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[2],
                color = tint,
                fontSize = if (wed) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(checked = thu, onCheckedChange = { thu = it }) {
            val tint by animateColorAsState(if (thu) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[3],
                color = tint,
                fontSize = if (thu) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(checked = fri, onCheckedChange = { fri = it }) {
            val tint by animateColorAsState(if (fri) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[4],
                color = tint,
                fontSize = if (fri) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(checked = sat, onCheckedChange = { sat = it }) {
            val tint by animateColorAsState(if (sat) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[5],
                color = tint,
                fontSize = if (sat) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
        IconToggleButton(checked = sun, onCheckedChange = { sun = it }) {
            val tint by animateColorAsState(if (sun) checkedColor else uncheckedColor)
            Text(
                text = dayNameListEng[6],
                color = tint,
                fontSize = if (sun) checkedSize else uncheckedSize,
                fontWeight = FontWeight.Bold
            )

        }
    }
}
