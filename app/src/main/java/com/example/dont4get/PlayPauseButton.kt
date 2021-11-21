package com.example.dont4get

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.dont4get.data.Memo
import java.io.File
import java.io.FileInputStream

@Composable
fun PlayButton(memo: Memo) {
    var context = LocalContext.current

    IconButton(
        onClick = { Play(memo.fileName, context = context) }
    ) {
        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = null,
        )

    }
}

fun Play(uri: String, context: Context) {

    Log.i("uri", uri.toString())

    val player = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setDataSource(FileInputStream(File(uri)).fd)
        prepare()
        start()
    }
}