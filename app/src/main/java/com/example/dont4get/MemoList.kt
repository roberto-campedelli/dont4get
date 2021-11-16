package com.example.dont4get

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dont4get.data.MemoViewModel
import com.example.dont4get.data.MemoViewModelFactory

@Composable
fun MemoList() {

    val context = LocalContext.current
    val memoViewModel: MemoViewModel =
        viewModel(factory = MemoViewModelFactory(context.applicationContext as Application))

    val memos = memoViewModel.allMemo.observeAsState(listOf()).value

    LazyColumn() {
        items(memos) { memo ->
            Card(
                elevation = 10.dp, modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(text = memo.toString(), modifier = Modifier.padding(10.dp))
            }
        }
    }

}
