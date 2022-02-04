package com.example.dont4get.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_table")
data class Memo(

    @PrimaryKey
    @ColumnInfo(name = "fileName") val fileName: String,

    @ColumnInfo(name = "name") var name: String,

    @ColumnInfo(name = "date") var date: String,

    @ColumnInfo(name = "type") var type: String,

    @ColumnInfo(name = "days") var days: String,
)