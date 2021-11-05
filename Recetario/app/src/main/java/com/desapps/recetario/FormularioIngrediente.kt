package com.desapps.recetario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.core.view.get
import java.io.Serializable

class FormularioIngrediente : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_ingrediente)


        val etNombre = findViewById<EditText>(R.id.etNombreIngrediente)
        val etCantidad = findViewById<EditText>(R.id.etCantidadIngrediente)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        fun obtenerMedidaSeleccionada(): String{
            try{
                var index = radioGroup.checkedRadioButtonId
                var radioBtn = findViewById<RadioButton>(index)
                var texto = radioBtn.text.toString()
                Toast.makeText(this, texto, Toast.LENGTH_SHORT).show()
                return texto
            }catch(e: Exception) {
                Toast.makeText(this, "Error " + e.message, Toast.LENGTH_LONG).show()
                return ""
            }
        }

        val btnAgregarIngrediente = findViewById<Button>(R.id.btnAgregarIngredienteFormulario)
        btnAgregarIngrediente.setOnClickListener{
            val nombreIngrediente = etNombre.text.toString()
            val cantidadIngrediente = etCantidad.text.toString()
            if(radioGroup.checkedRadioButtonId == -1 || nombreIngrediente.isEmpty() || cantidadIngrediente.isEmpty()){

                Toast.makeText(this, "Debes añadir un nombre, una cantidad y seleccionar una unidad de medida", Toast.LENGTH_LONG).show()
            }else{
                try{
                    //Retornar ingrediente al formulario de la receta
                    val intento = Intent()
                    intento.putExtra("nombreIngrediente", nombreIngrediente)
                    intento.putExtra("cantidadIngrediente", cantidadIngrediente)
                    intento.putExtra("medidaIngrediente", obtenerMedidaSeleccionada())
                    setResult(1, intento)
                    finish()

                }catch(e: Exception) {
                    Toast.makeText(this, "Error " + e.message, Toast.LENGTH_LONG).show()
                }
            }
        }


    }
}