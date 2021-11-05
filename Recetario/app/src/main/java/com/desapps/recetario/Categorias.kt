package com.desapps.recetario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class Categorias : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        fun AbrirListaRecetas(categoria:String){
            val intento1 = Intent(this, ListaRecetas::class.java)
            intento1.putExtra("CategoriaElegida", categoria)
            startActivity(intento1)
            finish()
        }

        val btnRecetasPostres = findViewById<Button>(R.id.btnPostres)
        btnRecetasPostres.setOnClickListener(){
            AbrirListaRecetas("Postres")
        }

        val btnRecetasSopas = findViewById<Button>(R.id.btnSopas)
        btnRecetasSopas.setOnClickListener(){
            AbrirListaRecetas("Sopas")
        }

        val btnRecetasCocteleria = findViewById<Button>(R.id.btnCocteleria)
        btnRecetasCocteleria.setOnClickListener(){
            AbrirListaRecetas("Coctelería")
        }

        val btnRecetasEnsaladas = findViewById<Button>(R.id.btnEnsaladas)
        btnRecetasEnsaladas.setOnClickListener(){
            AbrirListaRecetas("Ensaladas")
        }

        val btnRegresar = findViewById<ImageButton>(R.id.btnRegresarCategorias)
        btnRegresar.setOnClickListener(){
            finish()
        }

    }
}