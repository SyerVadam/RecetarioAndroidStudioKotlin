package com.desapps.recetario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRecetas = findViewById<Button>(R.id.btnRecetas)
        btnRecetas.setOnClickListener(){
            val intento1 = Intent(this, ListaRecetas::class.java)
            startActivity(intento1)
        }

        val btnCategorias = findViewById<Button>(R.id.btnCategorias)
        btnCategorias.setOnClickListener(){
            val intento1 = Intent(this, Categorias::class.java)
            startActivity(intento1)
        }

        val btnInfo = findViewById<Button>(R.id.btnInfo)
        btnInfo.setOnClickListener{
            Toast.makeText(this, "Aplicación creada por José Israel Zavaleta Rivera en la experiencia educativa de Desarrollo de Aplicaciones, concluida exitosamente el 05/11/2021", Toast.LENGTH_LONG).show()
        }
    }
}