package com.samuelandrade.todolist

import java.text.SimpleDateFormat
import java.util.*

private val l : Locale = Locale("pt","BR")

fun Date.format() : String{
    return SimpleDateFormat("dd/MM/yyyy",l).format(this)
}

