package com.example.dont4get

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

    Card(
        elevation = 5.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Row {
            Column() {
                Text(text = memo.name, modifier = Modifier.padding(10.dp))
                Text(text = memo.date, modifier = Modifier.padding(10.dp))
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
    }) {
        val tint by animateColorAsState(if (checked) Color(0xFF46EC40) else Color(0xFFB0BEC5))
        if (checked) Icon(
            Icons.Filled.PlayArrow,
            contentDescription = "Localized description",
            tint = tint
        )
        else Icon(Icons.Filled.Pause, contentDescription = "Localized description", tint = tint)
    }

}
