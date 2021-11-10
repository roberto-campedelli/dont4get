package com.example.dont4get.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemoRepository

    init {
        val memoDao = MemoDatabase.getInstance(application).memoDao()
        repository = MemoRepository(memoDao = memoDao)
    }

    fun addUser(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMemo(memo)
        }
    }
}