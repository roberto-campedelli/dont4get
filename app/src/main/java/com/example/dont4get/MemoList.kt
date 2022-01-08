package com.example.dont4get

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
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
    var position by remember {
        mutableStateOf(0L)
    }

    var progress by remember { mutableStateOf(0.1f) }

    val player = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setDataSource(FileInputStream(File(memo.fileName)).fd)
        prepare()

    }

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

            }

            PlayPauseButton(player = player)

            DeleteButton(memo = memo, memoViewModel = memoViewModel, context = context)

            //PlayerProgressBar(player = player)

        }
    }

}

@Composable
fun PlayPauseButton(player: MediaPlayer) {

    var checked by remember { mutableStateOf(true) }

    var progress by remember {
        mutableStateOf(0F)
    }
    val duration = player.duration.toFloat()
    progress = (player.currentPosition.toFloat() - 0) / (duration)
    Log.i("audioProgress", progress.toString())


    player.setOnCompletionListener {
        checked = !checked // finish current activity
    }

    IconToggleButton(checked = checked, onCheckedChange = {
        checked = it
        if (player.isPlaying) {
            player.pause()
        } else {
            player.start()
            Log.i("audioProgress1", player.currentPosition.toString())
            /// todo: call a suspended function to update the current position
            //GlobalScope.launch { getCurrentPosition(player = player) }
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

    PlayerProgressBar(progress = progress)
}

//TODO - inserisci progress bar e tasti per andare avanti e indietro!

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PlayerProgressBar(progress: Float) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    LinearProgressIndicator(progress = animatedProgress)
    Spacer(Modifier.requiredHeight(30.dp))
}