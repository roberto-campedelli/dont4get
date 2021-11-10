package com.example.dont4get.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_table")
data class Memo(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "fileName") val fileName: String?,
    @ColumnInfo(name = "date") val date: String
)