package com.example.dont4get

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dont4get.data.Memo
import java.io.File
import java.io.FileInputStream


@Composable
fun PlayPauseButton(memo: Memo) {

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

    PlayButton(memo = memo, player)
    PauseButton(player)
}

@Composable
fun PlayButton(memo: Memo, player: MediaPlayer) {

    IconButton(
        onClick = {
            player!!.start()
        },
        modifier = Modifier.padding(5.dp)

    ) {
        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = null,
        )

    }
}

@Composable
fun PauseButton(player: MediaPlayer) {
    IconButton(
        onClick = {
            player.pause()
        },
        modifier = Modifier.padding(5.dp)

    ) {
        Icon(
            Icons.Filled.Pause,
            contentDescription = null,
        )

    }
}

