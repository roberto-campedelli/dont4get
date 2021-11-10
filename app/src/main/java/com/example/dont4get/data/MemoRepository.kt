package com.example.dont4get.data

import kotlinx.coroutines.flow.Flow

class MemoRepository(private val memoDao: MemoDao) {

    val listAllMemo: Flow<Memo> = memoDao.listAll()

    fun addMemo(memo: Memo) {
        memoDao.insert(memo)
    }

    fun deleteMemo(memo: Memo) {
        memoDao.delete(memo)
    }
}