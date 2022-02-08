package com.example.dont4get.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemoDao {

    @Insert
    suspend fun insert(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Query("SELECT * FROM memo_table")
    fun listAll(): LiveData<List<Memo>>

    @Query("SELECT * FROM memo_table")
    fun listAllReboot(): List<Memo>

}