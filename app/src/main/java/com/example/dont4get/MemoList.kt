package com.example.dont4get

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dont4get.data.Memo
import com.example.dont4get.data.MemoViewModel
import com.example.dont4get.util.fromStringToDateTime
import java.io.File
import java.io.FileInputStream

@ExperimentalMaterialApi
@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalAnimationApi
@Composable
fun MemoList(memos: List<Memo>, memoViewModel: MemoViewModel) {

    LazyColumn() {
        items(memos) { memo ->
            MemoCard(memo = memo, memoViewModel = memoViewModel)

        }
    }
}

enum class MemoDialogInfoStatus { HIDE, SHOW }

@ExperimentalMaterialApi
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MemoCard(memo: Memo, memoViewModel: MemoViewModel) {

    var memoDialogInfoStatus by remember {
        mutableStateOf(
            MemoDialogInfoStatus.HIDE
        )
    }

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
        onClick = { memoDialogInfoStatus = MemoDialogInfoStatus.SHOW },
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
                Text(text = memo.name, fontSize = 23.sp)
                if (memo.type == "Weekly") {
                    Text(text = memo.days)
                    Text(text = memo.date)
                } else {
                    val targetDateTime = fromStringToDateTime(memo.date)
                    val dayOfWeek = targetDateTime.dayOfWeek.toString()[0].plus(
                        targetDateTime.dayOfWeek.toString().substring(1, 3).lowercase()
                    )
                    val month = targetDateTime.month.toString()[0].plus(
                        targetDateTime.month.toString().substring(1, 3).lowercase()
                    )
                    Text(text = "$dayOfWeek ${targetDateTime.dayOfMonth} $month")
                    Text(text = "%02d:%02d".format(targetDateTime.hour, targetDateTime.minute))

                }
            }

            PlayPauseButton(player = player)
            // For now no delete button in the memo card
            //DeleteButton(memo = memo, memoViewModel = memoViewModel, context = context)
        }

        if (memoDialogInfoStatus == MemoDialogInfoStatus.SHOW) {
            memoDialogInfoStatus = updateMemoDialog(
                memo = memo,
                memoViewModel = memoViewModel
            )
        }
    }
}


// Code for the progress indicator of the player - still WIP
@Composable
fun PlayPauseButton(player: MediaPlayer) {

    var checked by rememberSaveable { mutableStateOf(true) }
    var progress by rememberSaveable {
        mutableStateOf(0F)
    }
    var position by rememberSaveable {
        mutableStateOf(0)
    }
    var playerState by rememberSaveable {
        mutableStateOf(false)
    }

    playerState = player.isPlaying
    position = player.currentPosition

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

}

//TODO - inserisci progress bar e tasti per andare avanti e indietro!

@Composable
fun MyIndicator(indicatorProgress: Float) {

    val progressAnimation by animateFloatAsState(
        targetValue = indicatorProgress,
        visibilityThreshold = 0.01f
    )
    LinearProgressIndicator(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(20.dp)), // Rounded edges
        progress = progressAnimation
    )
}
