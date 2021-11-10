package com.example.dont4get

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemoCard(memoItem: MemoItem) {
    Card(elevation = 10.dp, modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        Text(text = memoItem.name, modifier = Modifier.padding(10.dp))
    }
}