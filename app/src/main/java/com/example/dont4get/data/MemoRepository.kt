package com.example.dont4get.data

import androidx.lifecycle.LiveData

class MemoRepository(private val memoDao: MemoDao) {

    val readAllMemo: LiveData<List<Memo>> = memoDao.listAll()

    val readAllMemoReboot: List<Memo> = memoDao.listAllReboot()


    suspend fun addMemo(memo: Memo) {
        memoDao.insert(memo)
    }

    suspend fun deleteMemo(memo: Memo) {
        memoDao.delete(memo)
    }

    suspend fun updateMemo(memo: Memo) {
        memoDao.update(memo = memo)
    }

}