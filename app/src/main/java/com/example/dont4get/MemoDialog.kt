package com.example.dont4get

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import java.io.File
import java.util.*

@Composable
fun SaveMemo(memo: Memo, file: File, memoViewModel: MemoViewModel) {
    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var reminderType by remember { mutableStateOf("") }

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
                        onValueChange = {
                            name = it
                        }
                    )
                    reminderType = MemoRemind()
                    when (reminderType) {
                        "Once" -> {
                            DatePicker()
                            memo.date = TimePicker()
                        }
                        "Weekly" -> {
                            DayPicker()
                            memo.date = TimePicker()
                        }
                        "Daily" -> {
                            memo.date = TimePicker()
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
                            file.delete()
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
                        }
                    ) {
                        Text("Save")
                    }

                }
            },
            properties = DialogProperties(false, false)
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
fun DatePicker(): String {

    val context = LocalContext.current

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)

    var date by remember { mutableStateOf("") }
    val dataPickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date = "$dayOfMonth/$month/$year"
        }, year, month, day
    )

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

        Text(text = date, fontSize = 20.sp, modifier = Modifier.padding(3.dp))

    }
    return date
}


@Composable
fun TimePicker(): String {

    val context = LocalContext.current

    val hour: Int
    val min: Int

    val calendar = Calendar.getInstance()
    hour = calendar.get(Calendar.HOUR_OF_DAY)
    min = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf("") }
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
        Text(text = time, fontSize = 20.sp, modifier = Modifier.padding(3.dp))
    }
    return time
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
