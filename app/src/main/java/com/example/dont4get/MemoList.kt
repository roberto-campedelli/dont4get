package com.example.dont4get

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.dont4get.data.MemoViewModel

@Composable
fun MemoList(memos: List<Memo>, memoViewModel: MemoViewModel) {


    LazyColumn() {
        items(memos) { memo ->
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

                    PlayPauseButton(memo = memo)

                    DeleteButton(memo = memo, memoViewModel = memoViewModel)

                }
            }

        }
    }
}




