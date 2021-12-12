package com.example.dont4get

import android.content.Context
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
import com.example.dont4get.util.cancelNotification

@Composable
fun DeleteButton(memo: Memo, memoViewModel: MemoViewModel, context: Context) {

    IconButton(
        onClick = {
            memoViewModel.deleteMemo(memo)
            cancelNotification(context = context, memo.name)
        },
        modifier = Modifier.padding(5.dp)
    ) {
        Icon(
            Icons.Filled.Delete,
            contentDescription = null,
        )

    }

}
