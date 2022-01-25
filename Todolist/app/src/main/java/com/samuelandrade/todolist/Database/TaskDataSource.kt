package com.samuelandrade.todolist.Database

import com.samuelandrade.todolist.Data.Task

object TaskDataSource{
    private val list = arrayListOf<Task>()

    fun getList () = list
    fun insertTask(task: Task){
        list.add(task)

    }
}