package com.example.dont4get

import android.Manifest.permission.*
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.Snapshot.Companion.observe
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dont4get.data.*
import com.example.dont4get.ui.theme.Dont4getTheme
import com.example.dont4get.util.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val CHANNEL_ID = "channel"

@RequiresApi(Build.VERSION_CODES.S)
private val permissions = arrayListOf(RECORD_AUDIO, READ_EXTERNAL_STORAGE)
private var recorder: MediaRecorder? = null

class MainActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @RequiresApi(Build.VERSION_CODES.S)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager = notificationManager)
        setContent() {
            Dont4getTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Home()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalPermissionsApi
@Preview
@Composable
fun HomePreview() {
    Dont4getTheme {
        Home()
    }
}

enum class ButtonState { Pressed, Released, Initial }

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Home() {

    val context = LocalContext.current
    val memoViewModel: MemoViewModel =
        viewModel(factory = MemoViewModelFactory(context.applicationContext as Application))
    val memos = memoViewModel.allMemo.observeAsState(listOf()).value
    //val sortedMemos = memos.sortedBy { it.date }

    Scaffold(
        topBar = { MyTopAppBar() },
        floatingActionButton = { FAB(memoViewModel) },
        isFloatingActionButtonDocked = false,
        floatingActionButtonPosition = FabPosition.Center,
        content = {
            MemoList(memos, memoViewModel)
            Button(onClick = { TestFunction(context = context) }) {
                Text(text = "Test")
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.S)
fun TestFunction(context: Context) {

    retrieveMemos(context = context)
}


@Composable
fun MyTopAppBar() {
    TopAppBar(
        title = { Text("Dont4Get") },
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun FAB(memoViewModel: MemoViewModel) {
    var buttonState by remember { mutableStateOf(ButtonState.Initial) }
    var fileName: File? = null
    var memo: Memo

    FloatingActionButton(
        onClick = {},
        backgroundColor = Color.Red,
        modifier = Modifier
            .padding(20.dp)
            .size(90.dp),
    ) {
        Box(
            modifier = Modifier
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
            fileName = startRec()
            CircularRecProgress()
        } else if (buttonState == ButtonState.Released && fileName != null) {
            memo = stopRec(fileName!!)
            SaveMemoDialog(memo, memoViewModel)
        }
    }
}

@Preview
@Composable
fun CircularRecProgress() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(300.dp),
        color = Color(0xffffffff),
        strokeWidth = 4.dp,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun startRec(): File? {

    val context = LocalContext.current
    var fileName: File? = null
    val permissionsState = rememberMultiplePermissionsState(permissions)

    when {
        // if all the permissions are granted
        permissionsState.allPermissionsGranted -> {
            val mPath = context.filesDir
            //val mPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val currentDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            fileName = File(mPath, currentDate.format(formatter) + ".opus")
            recorder = MediaRecorder()

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.OGG)
                setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
                setOutputFile(fileName)
            }
            try {
                recorder!!.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Audio Record", "recorder failed to prepare")
            }
            Toast.makeText(context, "I'm recording", Toast.LENGTH_SHORT).show()
            //Toast.makeText(context, fileName.toString(), Toast.LENGTH_SHORT).show()

            recorder!!.start()
            //Toast.makeText(context, "Camera permission Granted", Toast.LENGTH_SHORT).show()
        }
        // if the permission are not granted i ask for them
        else -> {
            LaunchedEffect(permissionsState) {
                permissionsState.launchMultiplePermissionRequest()
            }
            //i the user deny the permissions, i inform him that i need the permission to work properly
            if (!permissionsState.allPermissionsGranted) {
                Toast.makeText(
                    context,
                    "I need the permissions to work.\nGo to the application setting! Thanks.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    return fileName
}

@Composable
fun stopRec(fileName: File): Memo {

    recorder?.stop()
    recorder?.release()
    recorder = null

    //Toast.makeText(context, "ho finito di registrare", Toast.LENGTH_SHORT).show()
    return Memo(
        fileName = fileName.toString(),
        name = "UndefinedName",
        date = "UndefinedDate",
        type = "UndefinedType",
        days = "UndefinedDays"
    )
}


private fun createNotificationChannel(notificationManager: NotificationManager) {
    // Create the NotificationChannel
    val name = "Reminder Notification"
    val descriptionText = "Reminder Notification"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
    mChannel.description = descriptionText
    mChannel.enableLights(true)
    mChannel.lightColor = android.graphics.Color.RED
    mChannel.enableVibration(true)
    mChannel.setShowBadge(true)
    mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    notificationManager.createNotificationChannel(mChannel)

}