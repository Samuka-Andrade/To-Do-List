package com.samuelandrade.todolist.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbTarefas(context: Context) : SQLiteOpenHelper(context,"Databse",null,1) {
    override fun onCreate(p0: SQLiteDatabase?) {
       p0?.execSQL("CREATE TABLE IF NOT EXISTS " + NAME_TABELA + "(title TEXT,hour TEXT, data TEXT, msg TEXT, image VARCHAR, id TEXT )")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        const val  NAME_TABELA = "tabelaTarefas"
    }


}