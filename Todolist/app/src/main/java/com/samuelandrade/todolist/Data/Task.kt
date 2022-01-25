package com.samuelandrade.todolist.Data

import android.net.Uri

data class Task(
    val title: String,
    val hour: String,
    val data: String,
    val msg: String?,
    val image: Uri?,
    val id : String
)
