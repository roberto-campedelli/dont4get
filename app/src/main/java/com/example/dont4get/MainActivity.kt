package com.example.dont4get

import android.Manifest.permission.*
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dont4get.ui.theme.Dont4getTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

private val permissions = arrayListOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

class MainActivity : ComponentActivity() {
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

@ExperimentalPermissionsApi
@Preview
@Composable
fun RecButtonPreview() {
    Dont4getTheme {
        RecButton()
    }
}

enum class ButtonState { Pressed, Released }

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

@ExperimentalPermissionsApi
@Composable
fun FAB() {
    var buttonState by remember { mutableStateOf(ButtonState.Released) }

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
            StartRec()
            CircularRecProgress()
            LinearRecProgress()
            Text(text = "$buttonState")
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


@ExperimentalPermissionsApi
@Composable
fun StartRec() {

    var context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(permissions)
    when {
        // If the camera permission is granted, then show screen with the feature enabled
        permissionsState.allPermissionsGranted -> {
            //start record
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