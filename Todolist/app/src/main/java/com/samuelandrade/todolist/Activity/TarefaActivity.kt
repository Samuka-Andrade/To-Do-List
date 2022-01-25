package com.samuelandrade.todolist.Activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.samuelandrade.todolist.Adapter
import com.samuelandrade.todolist.Database.DbTarefas
import com.samuelandrade.todolist.R
import com.samuelandrade.todolist.Data.Task
import com.samuelandrade.todolist.Database.TaskDataSource
import com.samuelandrade.todolist.databinding.ActivityTarefaBinding
import com.samuelandrade.todolist.format
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.sql.Time
import java.util.*

class TarefaActivity : AppCompatActivity() {
   lateinit var binding: ActivityTarefaBinding
   var titulo  = ""
   lateinit var data : String
   lateinit var hora : String
   var msg = ""
    var image : Uri? = Uri.EMPTY
   lateinit var register : ActivityResultLauncher<String>
   var isHourHas = false
   var isDateHas = false
   var idExtra = ""

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTarefaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        if (intent.hasExtra(Adapter.CHAVE_EXTRA_EDIT)){
            var t = TaskDataSource.getList()[intent.getIntExtra(Adapter.CHAVE_EXTRA_EDIT,0)]
            binding.titleTarefa.setText(t.title)
            titulo = t.title
            binding.horaTarefa.setText(t.hour)
            hora = t.hour
            binding.dataTarefa.setText(t.data)
            data = t.data
            idExtra = t.id
            if (!(t.msg?.isEmpty() == true)&&(t.msg!=null)){
                binding.msgTarefa.setText(t.msg)
                msg = t.msg!!
            }
            if (t.image!=null&&t.image!= Uri.EMPTY){
                image = t.image
                binding.textViewAddImage.setText(image.toString())
            }
        }
        var bar= toolbar
        toolbar.elevation = 10F
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener{
            finish()
        }
         register = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                image = it
                binding.textViewAddImage.setText(it.toString())
            })
        insertListeners()


    binding.buttonCancelar.setOnClickListener{  finish()

        startActivity(Intent(applicationContext,MainActivity::class.java))
    }

    }

    fun insertListeners() {
        binding.dataTarefa.setOnClickListener {
            var datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, datePicker.tag)
            datePicker.addOnPositiveButtonClickListener {
                var d = TimeZone.getDefault().getOffset(Date().time) * -1
                var time: Time = Time(it + d)
                data = time.format()
                binding.dataTarefa.setText(data)
                isDateHas = true
            }
        }
        binding.horaTarefa.setOnClickListener {
            var timePicker =
                MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
            timePicker.addOnPositiveButtonClickListener {
                val minute =
                    if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                hora = "$hour:$minute"
                binding.horaTarefa.setText(hora)
                isHourHas = true
                //  binding.horaTarefa.setText(timePicker.hour.toString()+":"+timePicker.minute)

            }
            timePicker.show(supportFragmentManager, timePicker.tag)
            }
        binding.textViewAddImage.setOnClickListener {



            register.launch("image/*")

        }


        binding.buttonCriar.setOnClickListener {
           if (!intent.hasExtra(Adapter.CHAVE_EXTRA_EDIT)){
             if (isDateHas&&isHourHas&&(!binding.titleTarefa.text.toString().isEmpty())){


                 if (image!=null&&image!= Uri.EMPTY){

                tratarImagem()}

                 var ts = Task(
                     title = binding.titleTarefa.text.toString(),
                     hour = hora,
                     data = data,
                     msg = binding.msgTarefa.text.toString(),
                     image = image ,
                     id = UUID.randomUUID().toString()
                 )

                     var dbTarefas = DbTarefas(applicationContext)
                     var cvl = ContentValues()
                     cvl.put("title", ts.title)
                     cvl.put("hour", ts.hour)
                     cvl.put("data", ts.data)
                     cvl.put("msg", ts.msg)
                     cvl.put("image", ts.image.toString())
                     cvl.put("id", ts.id)
                     dbTarefas.writableDatabase.insert(DbTarefas.NAME_TABELA, null, cvl)
                     // Toast.makeText(applicationContext,TaskDataSource.getList().size.toString(), Toast.LENGTH_LONG).show()
                     finish()
                     startActivity(Intent(applicationContext, MainActivity::class.java))
                 }else{
            Toast.makeText(applicationContext,"Título, data e hora são obrigatorios",Toast.LENGTH_LONG).show()
        }}else{
               if (image!=null&&image!= Uri.EMPTY){

                   tratarImagem()}
                var ts = Task(
                    title = binding.titleTarefa.text.toString(),
                    hour = hora,
                    data = data,
                    msg = binding.msgTarefa.text.toString(),
                    image = image ,
                    id = idExtra
                )
             //   TaskDataSource.getList()[intent.getIntExtra(Adapter.CHAVE_EXTRA_EDIT,0)] = ts

                var dbTarefas = DbTarefas(applicationContext)
                var cvl = ContentValues()
                cvl.put("title", ts.title)
                cvl.put("hour", ts.hour)
                cvl.put("data", ts.data)
                cvl.put("msg", ts.msg)
                cvl.put("image", ts.image.toString())
                cvl.put("id", ts.id)
                dbTarefas.writableDatabase.update(DbTarefas.NAME_TABELA,cvl,"id = '${ts.id}' ",null)

               finish()
                startActivity(Intent(applicationContext,MainActivity::class.java))



            }


    }
           }



    fun tratarImagem() { var file:File = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!,"Images")
        file.mkdir()
        var stream = contentResolver.openInputStream(image!!)
        var b = BitmapFactory.decodeStream(stream)
        var finalFile = File(file,UUID.randomUUID().toString()+"jpg")
        var fos = FileOutputStream(finalFile)
        b.compress(Bitmap.CompressFormat.JPEG,90,fos)
        image = Uri.fromFile(finalFile);

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(applicationContext,MainActivity::class.java))
    }

}