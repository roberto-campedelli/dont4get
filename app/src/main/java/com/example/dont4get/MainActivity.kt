package com.example.dont4get

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dont4get.ui.theme.Dont4getTheme

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
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

@Preview
@Composable
fun RecButtonPreview() {
    Dont4getTheme {
        RecButton()
    }
}

enum class ButtonState { Pressed, Released }

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
            CircularRecProgress()
            LinearRecProgress()
        }
        Text(text = "$buttonState")

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

