package com.dam.acividades

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var boton: Button
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var navegador: Button
    private lateinit var telefono: Button
    private lateinit var llamda: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //bindeo
        boton = findViewById(R.id.botonactividad)
        navegador = findViewById(R.id.botonbrowser)
        telefono = findViewById(R.id.botonTelefono)
        llamda = findViewById(R.id.botonllamada)

        telefono.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = "tel:999555222".toUri()
            if (callIntent.resolveActivity(packageManager) != null) {
                startActivity(callIntent)
            } else {
                Log.e("MainActivity", "No hay aplicación instalada para manejar la acción.")
            }
        }
        boton.setOnClickListener {
            if (tenemosPermiso(Manifest.permission.CALL_PHONE)) {
                realizarLlamada()
            } else {
                solicitarPermiso(Manifest.permission.CALL_PHONE)
            }
        }


      







        navegador.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.xataka.com".toUri())
            if (browserIntent.resolveActivity(packageManager) != null) {
                startActivity(browserIntent)
            }
        }


        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val returnString = data.extras?.getString("stringdevuelta")
                    Log.d("intents", "Resultado devuelto: $returnString")
                }
            }

        }

        //oyente
        boton.setOnClickListener {
            //creamos un intent
            val intent = Intent(this, segundaActividad::class.java)

            intent.putExtra("envio","abuff")
            //lanzamos actividad

            startForResult.launch(intent)
        }


    }
    private fun tenemosPermiso(permiso: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED
    }
    /**
     * Indica el permiso que queremos solicitar y quién es el responsable de gestionar la solicitud
     * de permisos en tiempo de ejecución.
     *
     * @param permiso El permiso a solicitar (ej: Manifest.permission.CAMERA).
     */
    private fun solicitarPermiso(permiso: String) {
        //Aquí, this indica quien es el responsable de gestionar la solicitud de permisos y requestCode es un identifficador para nosotros
        ActivityCompat.requestPermissions(this, arrayOf(permiso), 1)
    }
    private fun realizarLlamada() {
        val numeroTelefono = "tel:123456789" // Número de teléfono a llamar
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = numeroTelefono.toUri()
        startActivity(intent)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                realizarLlamada()
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }







}
