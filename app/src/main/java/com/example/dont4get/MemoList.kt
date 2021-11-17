package com.example.dont4get

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dont4get.data.Memo

@Composable
fun MemoList(memos: List<Memo>) {


    LazyColumn() {
        items(memos) { memo ->
            Card(
                elevation = 5.dp, modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Column() {
                    Text(text = memo.fileName, modifier = Modifier.padding(10.dp))
                    Text(text = memo.name, modifier = Modifier.padding(10.dp))
                    Text(text = memo.date, modifier = Modifier.padding(10.dp))
                }

            }
        }
    }
}

