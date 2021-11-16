package com.example.dont4get.data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoViewModel(application: Application) : AndroidViewModel(application) {

    val allMemo: LiveData<List<Memo>>
    private val repository: MemoRepository

    init {
        val memoDao = MemoDatabase.getInstance(application).memoDao()
        repository = MemoRepository(memoDao = memoDao)
        allMemo = repository.readAllMemo
    }

    fun addMemo(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMemo(memo)
        }
    }

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMemo(memo)
        }
    }

    fun updateMemo(memo: Memo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMemo(memo = memo)
        }
    }

}

class MemoViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(MemoViewModel::class.java)) {
            return MemoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}