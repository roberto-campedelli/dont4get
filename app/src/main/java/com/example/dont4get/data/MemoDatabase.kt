package com.example.dont4get.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Memo::class], version = 1, exportSchema = false)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MemoDatabase? = null

        fun getInstance(context: Context): MemoDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, MemoDatabase::class.java, "memo_database")
                    .build()
            }
        }
    }
}