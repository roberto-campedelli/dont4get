package com.example.dont4get.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_table")
data class Memo(

    @PrimaryKey
    @ColumnInfo(name = "fileName") val fileName: String,

    @ColumnInfo(name = "name") var name: String,

    // for the Once type = 2022/10/12 - 12:10:19
    // for the Weekly type = 12:10:19
    @ColumnInfo(name = "date") var date: String,

    // Once or Weekly
    @ColumnInfo(name = "type") var type: String,

    // Mon, Tue, Wed, Thu, Fri, Sat, Sun
    // for the Once type this field is empty
    @ColumnInfo(name = "days") var days: String,
)