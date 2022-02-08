package com.proyecto_final.ftpappandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.ftpappandroid.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketException

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

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
                var conexion=Conexion_ftp()
                conexion.server= binding.tilIp.text.toString()
                conexion.port=binding.tilPort.text.toString().toInt()
                conexion.user=binding.tilUser.text.toString()
                conexion.password=binding.tilPass.text.toString()


                try{
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            conexion.conexionFtp()
                        }
                    }
                    Toast.makeText(this,"Conectado correctamente al decodidicador",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Editor::class.java)
                    intent.putExtra("CONECTADO", conexion)
                    startActivity(intent)
                }catch (e:SocketException){
                    Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }catch (e:IOException){
                    Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}



