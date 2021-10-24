package com.example.dont4get

import android.Manifest.permission.*
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dont4get.ui.theme.Dont4getTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val permissions = arrayListOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
private var recorder: MediaRecorder? = null

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent() {
            Dont4getTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    RecButton()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPermissionsApi
@Preview
@Composable
fun RecButtonPreview() {
    Dont4getTheme {
        RecButton()
    }
}

enum class ButtonState { Pressed, Released, Initial }

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPermissionsApi
@Composable
fun RecButton() {

    Scaffold(
        topBar = { myTopAppBar() },
        floatingActionButton = { FAB() },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.End,
        //bottomBar = { BottomBar() },
        //drawerContent = { Text(text = "Drawer Menu 1") },
        content = { innerPadding ->
            LazyColumn(contentPadding = innerPadding) {
                items(count = 100) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun myTopAppBar() {
    TopAppBar(
        title = { Text("RECUP") },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPermissionsApi
@Composable
fun FAB() {
    var buttonState by remember { mutableStateOf(ButtonState.Initial) }

    FloatingActionButton(
        onClick = {},
        backgroundColor = Color.Red,
        modifier = Modifier
            .padding(20.dp)
            .size(85.dp),

        ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        buttonState = ButtonState.Pressed

                        // Waits for the tap to release
                        // before returning the call
                        this.tryAwaitRelease()

                        // Set the currentState to Release
                        // to trigger Release animation
                        buttonState = ButtonState.Released
                    }
                )
            })
        if (buttonState == ButtonState.Pressed) {
            Text(text = "$buttonState")
            StartRec()
            CircularRecProgress()
            LinearRecProgress()
        } else if (buttonState == ButtonState.Released) {
            StopRec()
            SaveMemo()
        }
    }
}

@Composable
fun CircularRecProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(300.dp),
        color = Color.Yellow,
        strokeWidth = 3.5.dp,

        )
}

@Composable
fun LinearRecProgress() {
    LinearProgressIndicator(
        modifier = Modifier
            .size(200.dp),
        color = Color.Yellow,
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPermissionsApi
@Composable
fun StartRec() {

    val context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(permissions)
    when {
        // If the camera permission is granted, then show screen with the feature enabled
        permissionsState.allPermissionsGranted -> {
            //start record

            val mPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val currentDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val mFileName = File(mPath, currentDate.format(formatter) + ".3gp")
            recorder = MediaRecorder()

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(mFileName)
            }
            try {
                recorder!!.prepare()
            } catch (e: IOException) {
                Log.e("Audio Record", "recorder failed to prepare")
            }
            Toast.makeText(context, "sto registrando", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, mFileName.toString(), Toast.LENGTH_SHORT).show()

            recorder!!.start()
            Toast.makeText(context, "Camera permission Granted", Toast.LENGTH_SHORT).show()
        }
        // If the user denied the permission but a rationale should be shown, or the user sees
        // the permission for the first time, explain why the feature is needed by the app and allow
        // the user to be presented with the permission again or to not see the rationale any more.
        permissionsState.shouldShowRationale ||
                !permissionsState.permissionRequested -> {
            LaunchedEffect(permissionsState) {
                permissionsState.launchMultiplePermissionRequest()
            }
            Toast.makeText(context, "GIVE ME THE POWER", Toast.LENGTH_SHORT).show()
        }

    }
}

@Composable
fun StopRec() {

    val context = LocalContext.current

    recorder?.stop()
    recorder?.release()
    recorder = null

    Toast.makeText(context, "ho finito di registrare", Toast.LENGTH_SHORT).show()
}


@Composable
fun SaveMemo() {
    val openDialog = remember { mutableStateOf(true) }
    var name by remember { mutableStateOf(TextFieldValue("")) }

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
                    MemoRemind()
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
fun MemoRemind() {

    val radioOptions = listOf("Every Day", "Once per Week", "Just Once")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { }
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
    }
}
