package com.proyecto_final.ftpappandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.ftpappandroid.databinding.ActivityEditorBinding
import kotlinx.coroutines.*
import java.io.*
import java.net.SocketException
import java.io.IOException

import java.io.FileWriter

import java.io.BufferedWriter

import java.io.File




class Editor : AppCompatActivity() {
    private lateinit var binding:ActivityEditorBinding
    private lateinit var conexion:Conexion_ftp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditorBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)


        conexion= intent.getSerializableExtra("CONECTADO") as Conexion_ftp

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
                linelist.forEach {
                    binding.etPrincipal.append(it)
                }
                bufer.close()
            }

            Toast.makeText(this,"Datos obtenidos correctamente", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }catch (e: IOException){
            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }

        binding.btnCargar.setOnClickListener {
            if(conexion==null){
                Toast.makeText(this,"NO HAY CONEXION CON EL DECODIFICADOR",Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        val archivo = File(conexion.directorioLocal)
                        try {
                            val writer = BufferedWriter(FileWriter(archivo, false)) // true for append
                            writer.write(binding.etPrincipal.text.toString())
                            writer.close()
                            conexion.subirFichero()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                }
                Toast.makeText(this,"Fichero subido correctamente al decodificador",Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDescargar.setOnClickListener {
            binding.etPrincipal.setText("")
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
                    linelist.forEach {
                        binding.etPrincipal.append(it)
                    }
                    bufer.close()
                }

                Toast.makeText(this,"Datos obtenidos correctamente", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
            }catch (e: IOException){
                Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        if(conexion!=null){
            CoroutineScope(Dispatchers.IO).launch{
                conexion.close()
            }
        }
    }

}


