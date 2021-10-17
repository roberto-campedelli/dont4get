package com.example.dont4get

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
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
        topBar = { TopAppBar() },
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

/*
            BottomAppBar(
                // Defaults to null, that is, No cutout
                cutoutShape = MaterialTheme.shapes.small.copy(
                    androidx.compose.foundation.shape.CornerSize(percent = 50)
                )
            ) {

                /* Bottom app bar content */
            }
        }
    ) {
        // Screen content
    }*/

@Composable
fun TopAppBar() {
    TopAppBar(
        title = { Text("Recording App") },
    )
}

@Composable
fun FAB() {
    var buttonState by remember { mutableStateOf(ButtonState.Released) }


    FloatingActionButton(
        onClick = {},
        backgroundColor = Color.Red,
        modifier = Modifier
            .size(80.dp),
        elevation = FloatingActionButtonDefaults.elevation()

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
        Text(text = "$buttonState")

    }
}


/*onClick = {
           if (buttonState == ButtonState.Released) {
               buttonState = com.example.dont4get.ButtonState.Pressed
           } else
               buttonState = ButtonState.Released
       },*/


@Composable
fun BottomBar() {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(elevation = 10.dp) {

        BottomNavigationItem(
            icon = {
                Icon(imageVector = Icons.Default.Home, "")
            },
            label = { Text(text = "Home") },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
            })

        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Favorite, "")
        },
            label = { Text(text = "Favorite") },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
            })

    }
}

@Composable
fun StartRec() {
    Text(text = "start")

}

@Composable
fun StopRec() {
    Text(text = "stop")
}

/*
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {

        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "Alert Dialog") },
            text = { Text("Start Recording!") },
            confirmButton = {
                Button(modifier = Modifier.fillMaxWidth(),
                    onClick = { openDialog.value = false }
                ) {
                    Text("Dismiss")
                }
            })
    }
*/



