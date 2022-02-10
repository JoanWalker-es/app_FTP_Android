package com.proyecto_final.ftpappandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private var espera=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)



        binding.btnConectar.setOnClickListener {
            if(binding.tilIp.text.isNullOrBlank() || binding.tilPort.text.isNullOrBlank())
                Toast.makeText(this,"Introduce los datos de la conexión",Toast.LENGTH_SHORT).show()
            else if (binding.tilUser.text.isNullOrBlank() || binding.tilPass.text.isNullOrBlank()){
                Toast.makeText(this,"Introduce los datos de login",Toast.LENGTH_SHORT).show()
            }else{
                conexion=Conexion_ftp()
                conexion.server= binding.tilIp.text.toString()
                conexion.port=binding.tilPort.text.toString().toInt()
                conexion.user=binding.tilUser.text.toString()
                conexion.password=binding.tilPass.text.toString()


                try{
                    CoroutineScope(Dispatchers.IO).launch {
                        val conectado=conexion.conexionFtp()
                        binding.btnConectar.isClickable.not()
                        if(conectado){
                            runOnUiThread {
                                Toast.makeText(applicationContext,"Conectado correctamente al decodidicador",Toast.LENGTH_SHORT).show()
                            }
                            val intent = Intent(this@MainActivity, Editor::class.java)
                            intent.putExtra("CONECTADO", conexion)
                            startActivity(intent)
                            conexion.close()
                            finish()

                        }else{
                            runOnUiThread {
                                Toast.makeText(applicationContext,"No ha sido posible realizar la conexión",Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                }catch (e:SocketException){
                    Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }catch (e:IOException){
                    Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun btnDisable(btn:Button){
        btn.isClickable==false
        btn.setBackgroundColor(getColor(R.color.error))
        btn.setTextColor(getColor(R.color.error))
    }


}



