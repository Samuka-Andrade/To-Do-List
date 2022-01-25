package com.samuelandrade.todolist.Activity


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.samuelandrade.todolist.Adapter
import com.samuelandrade.todolist.Database.DbTarefas
import com.samuelandrade.todolist.Data.Task
import com.samuelandrade.todolist.Database.TaskDataSource
import com.samuelandrade.todolist.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val adapter by lazy{Adapter(applicationContext)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if(!TaskDataSource.getList().isEmpty()){
                TaskDataSource.getList().clear()
            }

       // Toast.makeText(applicationContext,TaskDataSource.getList().size.toString(),Toast.LENGTH_LONG).show()


        floatingActionButton.setOnClickListener{
            var i = Intent(applicationContext, TarefaActivity::class.java)
            startActivity(i)
            finish()
        }

        var recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        var manager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = manager

        var l : List<Task>

        var dbTarefas = DbTarefas(applicationContext)
       var cursor = dbTarefas.readableDatabase.rawQuery("SELECT * FROM tabelaTarefas",null)
        while(cursor.moveToNext()){
            var title = cursor.getString(cursor.getColumnIndex("title"))
            var hour = cursor.getString(cursor.getColumnIndex("hour"))
            var data = cursor.getString(cursor.getColumnIndex("data"))
            var msg = cursor.getString(cursor.getColumnIndex("msg"))
            var image = cursor.getString(cursor.getColumnIndex("image"))
            var id = cursor.getString(cursor.getColumnIndex("id"))
            var t = Task(title,hour,data,msg, (Uri.parse(image)) ,id)
            TaskDataSource.insertTask(t);
        }
        cursor?.close()
        adapter.submitList(TaskDataSource.getList())
        recyclerView.adapter = adapter
    }
}