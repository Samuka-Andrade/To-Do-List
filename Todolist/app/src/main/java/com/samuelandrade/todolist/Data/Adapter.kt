package com.samuelandrade.todolist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.samuelandrade.todolist.Activity.TarefaActivity
import com.samuelandrade.todolist.Database.DbTarefas
import com.samuelandrade.todolist.Data.Task
import com.samuelandrade.todolist.Database.TaskDataSource
import java.lang.NullPointerException

class Adapter(var context: Context) : androidx.recyclerview.widget.ListAdapter<Task,Adapter.MyViewHolder>(DiffCalback()) {
    var c = context

    class MyViewHolder(itemView: View,context: Context) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.textViewTitle)
        var horaData = itemView.findViewById<TextView>(R.id.textViewInfo)
        var msg = itemView.findViewById<TextView>(R.id.textViewMsg)
        var imageView = itemView.findViewById<ImageView>(R.id.imageView)
        var c = context
        var menuButton = itemView.findViewById<AppCompatImageView>(R.id.button_menu)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemLista = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_recycler,parent,false)
        return MyViewHolder(itemLista,context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var model = getItem(position)
        holder.title.setText(model.title)
        holder.horaData.setText(model.data+ " " + model.hour)
        if ((!model.msg.equals(""))&&(model.msg!=null)){
            holder.msg.setText(model.msg)
            holder.msg.visibility = View.VISIBLE
        }
        if (model.image!=null&&model.image!= Uri.EMPTY){

            holder.imageView.visibility = View.VISIBLE
           holder.imageView.setImageURI(model.image)
        }

        holder.menuButton.setOnClickListener{

            var m = PopupMenu(holder.menuButton.context,holder.menuButton)
            m.inflate(R.menu.menu)
            m.show()
            m.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editar -> editar(task = model,position)
                    R.id.remover -> deletar(task = model,position)
                }
                return@setOnMenuItemClickListener true

            }


        }




    }

    fun editar(task: Task,position: Int){
    var intent = Intent(context,TarefaActivity::class.java)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(Companion.CHAVE_EXTRA_EDIT,position)
    c.startActivity(intent)
}
fun deletar(task: Task,position: Int){
    var dbTarefas = DbTarefas(context = context)
    dbTarefas.writableDatabase
        .delete(DbTarefas.NAME_TABELA,"id = '${task.id}' ",null)
    var cursor = dbTarefas.readableDatabase.rawQuery("SELECT * FROM tabelaTarefas",null)
    TaskDataSource.getList().clear()
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
        notifyItemChanged(position)
         notifyDataSetChanged()
}

    companion object {
        const val CHAVE_EXTRA_EDIT = "extra_edit"
    }
}

class DiffCalback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id }
