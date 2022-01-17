package com.example.dont4get

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    player.setOnCompletionListener {
        checked = !checked
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

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    Column() {
        LinearProgressIndicator(progress = animatedProgress)

        Spacer(Modifier.requiredHeight(30.dp))

        MyIndicator(indicatorProgress = progress, duration = duration.toInt())
    }


}

//TODO - inserisci progress bar e tasti per andare avanti e indietro!

@Composable
fun PlayerProgressBar(progress: Float) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    LinearProgressIndicator(progress = animatedProgress)
    Spacer(Modifier.requiredHeight(30.dp))
}

@Composable
fun MyIndicator(indicatorProgress: Float, duration: Int) {
    var progress by remember { mutableStateOf(0f) }
    val progressAnimDuration = duration
    val progressAnimation by animateFloatAsState(
        targetValue = indicatorProgress
    )
    LinearProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)), // Rounded edges
        progress = progressAnimation
    )

}