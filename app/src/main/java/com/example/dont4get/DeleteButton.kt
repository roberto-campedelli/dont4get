package com.example.dont4get

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dont4get.data.Memo
import com.example.dont4get.data.MemoViewModel
import java.io.File

@Composable
fun DeleteButton(memo: Memo, memoViewModel: MemoViewModel) {

    IconButton(
        onClick = { delete(memo, memoViewModel = memoViewModel) },
        modifier = Modifier.padding(5.dp)
    ) {
        Icon(
            Icons.Filled.Delete,
            contentDescription = null,
        )

    }

}

private fun delete(memo: Memo, memoViewModel: MemoViewModel) {

    val file = File(memo.fileName)
    file.delete()

    memoViewModel.deleteMemo(memo)

}