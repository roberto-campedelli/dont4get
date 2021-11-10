package com.example.dont4get.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    @Insert
    fun insert(memo: Memo)

    @Delete
    fun delete(memo: Memo)

    @Query("SELECT * FROM memo_table")
    fun listAll(): Flow<Memo>


}