package com.example.dont4get

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun MemoList(allMemos: MutableList<MemoItem>) {

    val context = LocalContext.current

    val files: Array<String> = context.fileList()

    LazyColumn() {
        items(files) { memo ->
            Card(elevation = 10.dp, modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                Text(text = memo, modifier = Modifier.padding(10.dp))
            }
        }
    }
/*
    LazyColumn() {
        items(files) { memo ->
            MemoCard(memoItem = memo)
        }
    }

 */
}