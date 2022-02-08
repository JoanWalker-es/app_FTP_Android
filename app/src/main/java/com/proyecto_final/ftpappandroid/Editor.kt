package com.proyecto_final.ftpappandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.ftpappandroid.databinding.ActivityEditorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.SocketException

class Editor : AppCompatActivity() {
    private lateinit var binding:ActivityEditorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditorBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)


        val conexion:Conexion_ftp = getIntent().getSerializableExtra("CONECTADO") as Conexion_ftp

        try{
            lifecycleScope.launch {
                val bufer= withContext(Dispatchers.IO){
                    conexion.conexionFtp()
                    conexion.crearCarpeta()
                    conexion.leerArchivo(conexion.descargaArchivo())
                }

                val linelist= mutableListOf<String>()
                bufer.useLines {
                        lines-> lines.forEach {
                            linelist.add(it+"\n")
                        }
                }
                linelist.forEach { binding.etPrincipal.append(it) }

            }

            Toast.makeText(this,"Datos obtenidos correctamente", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }catch (e: IOException){
            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }

    }

}