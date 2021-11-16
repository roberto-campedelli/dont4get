package com.example.dont4get.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Memo::class], version = 2, exportSchema = false)
abstract class MemoDatabase : RoomDatabase() {

    abstract fun memoDao(): MemoDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var INSTANCE: MemoDatabase? = null

        fun getInstance(context: Context): MemoDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MemoDatabase::class.java,
                        "memo_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}
