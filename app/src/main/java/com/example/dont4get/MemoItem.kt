package com.example.dont4get

import java.io.File

data class MemoItem(var name: String, val fileName: File?, var date: String) {

    fun deleteFile(): Boolean {
        return fileName!!.delete()
    }

}