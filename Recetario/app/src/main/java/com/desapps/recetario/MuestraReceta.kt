package com.desapps.recetario

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.io.Serializable
import java.sql.SQLException

class MuestraReceta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muestra_receta)

        var idReceta = -1
        val tvNombre = findViewById<TextView>(R.id.tvNombreReceta)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcionReceta)
        val lvIngredientes = findViewById<ListView>(R.id.lvIngredientesReceta)
        var cantidadPersonasActual = -1
        var cantidadPersonasNuevo = -1
        val etCantidadPersonas = findViewById<EditText>(R.id.etCantidadPersonas)
        val btnResta = findViewById<Button>(R.id.btnResta)
        val btnSuma = findViewById<Button>(R.id.btnSuma)
        val btnActualizarPersonas = findViewById<Button>(R.id.btnActualizarPersonas)
        val llaveRecetaConsulta = intent.extras?.getInt("LlaveSeleccionada")?:-1

        val admin = SQLite(this, "administracion", null, 1)
        val bd = admin.writableDatabase

        data class Ingrediente(var nombre:String, var cantidad:Double, var medida:String) {
            override fun toString(): String {
                return (nombre + " " + cantidad + " " + medida)
            }
        }
        var listaIngredientes = mutableListOf<Ingrediente>()
        var listaStringIngredientes = mutableListOf<String>()

        try{
            var fila = bd.rawQuery("SELECT idReceta, nombre, descripcion, personas FROM receta WHERE rowid ='${llaveRecetaConsulta}'", null)

            if(fila.moveToFirst()){
                idReceta = fila.getInt(0)
                tvNombre.setText(fila.getString(1))
                tvDescripcion.setText(fila.getString(2))
                cantidadPersonasActual = fila.getInt(3)
                cantidadPersonasNuevo = cantidadPersonasActual
                etCantidadPersonas.setText(cantidadPersonasActual.toString())
            }else{
                Toast.makeText(this, "No se encontró la receta", Toast.LENGTH_SHORT).show()
            }

            fila = bd.rawQuery("SELECT nombre, porcion, medida FROM ingrediente WHERE idReceta = '${idReceta}'", null)
            with(fila){
                while(moveToNext()){
                    var ingrediente = Ingrediente(fila.getString(0), fila.getDouble(1), fila.getString(2))
                    listaIngredientes.add(ingrediente)
                    listaStringIngredientes.add(ingrediente.toString())
                }
            }
            bd.close()

            val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaStringIngredientes)
            lvIngredientes.adapter = adaptador1

        }catch(e: Exception){
            Toast.makeText(this, e.message.toString() , Toast.LENGTH_LONG).show()
        }finally{
            bd.close()
        }

        btnSuma.setOnClickListener{
            cantidadPersonasNuevo++
            etCantidadPersonas.setText(cantidadPersonasNuevo.toString())
        }

        btnResta.setOnClickListener{
            if(cantidadPersonasNuevo>1){
                cantidadPersonasNuevo--
                etCantidadPersonas.setText(cantidadPersonasNuevo.toString())
            }
        }

        btnActualizarPersonas.setOnClickListener{
            var cantidadNueva:Double
            listaStringIngredientes.clear()

            listaIngredientes.forEach {
                cantidadNueva = (it.cantidad/cantidadPersonasActual)*cantidadPersonasNuevo

                it.cantidad = cantidadNueva
                listaStringIngredientes.add(it.toString())
            }
            cantidadPersonasActual = cantidadPersonasNuevo
            val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaStringIngredientes)
            lvIngredientes.adapter = adaptador1
        }

    }
}

