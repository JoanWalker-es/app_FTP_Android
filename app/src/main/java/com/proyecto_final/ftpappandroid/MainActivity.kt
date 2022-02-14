package com.proyecto_final.ftpappandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.ftpappandroid.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.IOException
import java.net.SocketException

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var conexion:Conexion_ftp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnConectar.setOnClickListener {
            if(binding.tilIp.text.isNullOrBlank() || binding.tilPort.text.isNullOrBlank())
                Toast.makeText(this,this.getString(R.string.input_data),Toast.LENGTH_SHORT).show()
            else if (binding.tilUser.text.isNullOrBlank() || binding.tilPass.text.isNullOrBlank()){
                Toast.makeText(this,this.getString(R.string.input_login),Toast.LENGTH_SHORT).show()
            }else{
                conexion=Conexion_ftp()
                conexion.server= binding.tilIp.text.toString()
                conexion.port=binding.tilPort.text.toString().toInt()
                conexion.user=binding.tilUser.text.toString()
                conexion.password=binding.tilPass.text.toString()

                Toast.makeText(this,this.getString(R.string.connecting),Toast.LENGTH_SHORT).show()
                btnDisable(binding.btnConectar)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        conexion.conexionFtp()
                    }catch (e:SocketException){
                        Log.d("ERROR CONEXION",e.toString())
                    }catch (e:IOException){
                        Log.d("ERROR CONEXION",e.toString())
                    }finally {
                        if (conexion.isConectado) {
                            runOnUiThread {
                                cargaNuevaActivity()
                            }
                        } else {
                            runOnUiThread {
                                muestraError(getString(R.string.error))
                                btnEnable(binding.btnConectar)
                            }
                        }
                    }
                }
            }

        }

    }

    private fun btnDisable(btn:Button){
        btn.isEnabled = false
        btn.isClickable = false
        btn.setText(getString(R.string.conectando))
        btn.setTextColor(getColor(R.color.error))
    }

    private fun btnEnable(btn:Button){
        btn.isEnabled = true
        btn.isClickable = true
        btn.setText(getString(R.string.conectar))
        btn.setTextColor(getColor(R.color.color_primary))
    }

    private fun muestraError(error:String){
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
    }

    private fun cargaNuevaActivity(){
        Toast.makeText(this,this.getString(R.string.connection_success),Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Editor::class.java)
        intent.putExtra("CONECTADO", conexion)
        startActivity(intent)
        finish()
    }




}



