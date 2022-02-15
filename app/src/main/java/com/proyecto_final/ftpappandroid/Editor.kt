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
                withContext(Dispatchers.IO){
                    conexion.conexionFtp()
                    conexion.crearCarpeta()
                    if(conexion.existeFichero()){
                        val bufer=conexion.leerArchivo(conexion.descargaArchivo())
                        runOnUiThread {
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
                            muestraMensaje(getString(R.string.data_success))
                        }

                    }else{
                        runOnUiThread {
                            muestraMensaje(getString(R.string.existe))
                        }
                    }
                }

            }

        }catch (e: Exception){
            muestraMensaje(e.message.toString())
            //Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }catch (e: IOException){
            muestraMensaje(e.message.toString())
            //Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
        }

        binding.btnCargar.setOnClickListener {
            if(conexion==null){
                Toast.makeText(this,this.getString(R.string.no_connection),Toast.LENGTH_SHORT).show()
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
                muestraMensaje(getString(R.string.file_upload))
                //Toast.makeText(this,this.getString(R.string.file_upload),Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDescargar.setOnClickListener {
            binding.etPrincipal.setText("")
            try{
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        conexion.conexionFtp()
                        conexion.crearCarpeta()
                        if(conexion.existeFichero()){
                            val bufer=conexion.leerArchivo(conexion.descargaArchivo())
                            runOnUiThread {
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
                                muestraMensaje(getString(R.string.data_success))
                            }

                        }else{
                            runOnUiThread {
                                muestraMensaje(getString(R.string.existe))
                            }
                        }
                    }

                }

            }catch (e: Exception){
                muestraMensaje(e.message.toString())
                //Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
            }catch (e: IOException){
                muestraMensaje(e.message.toString())
                //Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun muestraMensaje(error:String){
        Toast.makeText(this,error,Toast.LENGTH_LONG).show()
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


