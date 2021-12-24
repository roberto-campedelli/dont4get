package com.example.dont4get

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dont4get.data.Memo
import com.example.dont4get.data.MemoViewModel
import java.io.File
import java.io.FileInputStream
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@ExperimentalAnimationApi
@Composable
fun MemoList(memos: List<Memo>, memoViewModel: MemoViewModel) {

    LazyColumn() {
        items(memos) { memo ->
            MemoCard(memo = memo, memoViewModel = memoViewModel)

        }
    }
}

@Composable
fun MemoCard(memo: Memo, memoViewModel: MemoViewModel) {

    val context = LocalContext.current


    var player = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setDataSource(FileInputStream(File(memo.fileName)).fd)
        prepare()

    }


    //TODO calculate duration e current position of the media player
    val duration = player.duration.toLong()

    val audioDuration = LocalTime.of(
        //TimeUnit.MILLISECONDS.toHours(duration).toInt(),
        TimeUnit.MILLISECONDS.toMinutes(duration).toInt(),
        TimeUnit.MILLISECONDS.toSeconds(duration).toInt()
    )
    var audioProgress = LocalTime.of(
        TimeUnit.MILLISECONDS.toMinutes(0).toInt(),
        TimeUnit.MILLISECONDS.toSeconds(0).toInt()
    )


    while (player.isPlaying && player.currentPosition < player.duration) {
        Log.i("audioProgress", player.currentPosition.toString())
    }
    val position = player.currentPosition.toLong()
    audioProgress = LocalTime.of(
        TimeUnit.MILLISECONDS.toMinutes(position).toInt(),
        TimeUnit.MILLISECONDS.toSeconds(position).toInt()
    )

    //todo inserisci progress bar e tasti per andare avanti e indietro!

    //Todo - fix this multithreading solution
    /*
    Thread {
        try {
            while (player.currentPosition < player.duration) {
                //seekbar.setProgress(mp.getCurrentPosition())
                val millis: Int = player.currentPosition
                Log.i("audioProgress", millis.toString())
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    println("interrupt exeption$e")
                }
            } // end while
        } catch (e: Exception) {
            e.printStackTrace()
            println("my Exception$e")
        }
    }.start()
*/

    Card(
        elevation = 10.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.dp)
            ) {
                Text(text = memo.name, modifier = Modifier)
                Text(text = memo.date, modifier = Modifier)

                Text(text = audioDuration.toString())
                Text(text = player.currentPosition.toString())

            }

            PlayPauseButton(player = player)

            DeleteButton(memo = memo, memoViewModel = memoViewModel, context = context)

        }
    }

}

@Composable
fun PlayPauseButton(player: MediaPlayer) {


    var checked by remember { mutableStateOf(true) }

    player.setOnCompletionListener {
        checked = !checked // finish current activity
    }


    IconToggleButton(checked = checked, onCheckedChange = {
        checked = it
        if (player.isPlaying) {
            player.pause()
        } else {
            player.start()
        }
    }, modifier = Modifier.padding(10.dp)) {
        val tint by animateColorAsState(if (checked) Color(0xFF46EC40) else Color(0xFFB0BEC5))
        if (checked) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = "Localized description",
                tint = tint,
                modifier = Modifier.size(35.dp)
            )
        } else {
            Icon(
                Icons.Filled.Pause, contentDescription = "Localized description", tint = tint,
                modifier = Modifier.size(35.dp)
            )
        }
    }

}

//TODO to decide: add or not the control to the speed of the audio
/*
@Composable
fun SpeedUpAudioButton(player: MediaPlayer) {

    var controlEnabled by remember {
        mutableStateOf(true)
    }
    while (player.isPlaying)
    controlEnabled = true

    TextButton(onClick = {
        val speed = player.playbackParams.speed
        player.playbackParams.speed = speed + 0.25F
    }, enabled = controlEnabled) {
        Text("faster")
    }
}

@Composable
fun SlowDownAudioButton(player: MediaPlayer) {

    var controlEnabled by remember {
        mutableStateOf(true)
    }
    while (player.isPlaying)
        controlEnabled = true

    TextButton(onClick = {
        val speed = player.playbackParams.speed
        player.playbackParams.speed = speed - 0.25F
    }, enabled = controlEnabled) {
        Text("slower")
    }

}
*/
