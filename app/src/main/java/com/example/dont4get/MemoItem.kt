package com.example.dont4get

import java.io.File

class MemoItem(val name: String, val fileName: File?, val date: String) {

    fun deleteFile(): Boolean {
        return fileName!!.delete()
    }


}