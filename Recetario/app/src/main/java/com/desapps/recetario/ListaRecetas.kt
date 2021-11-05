package com.desapps.recetario

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.SQLException
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

var indiceSeleccionado = -1

class ListaRecetas : AppCompatActivity(), AdapterView.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_recetas)

        val lvRecetas = findViewById<ListView>(R.id.lvRecetas)
        val listaRecetas = mutableListOf<String>()
        val listaIndicesRecetas = mutableListOf<kotlin.Int>()

        /*
        fun RegistroDePrueba() {
            val nombreReceta = "Paleta de chocolate"
            val descripcionReceta = "Descripcioncita"
            val personasReceta = 5
            val categoriaReceta = "Postres"

            val admin = SQLite(this, "administracion", null, 1)
            val bd = admin.writableDatabase

                val registro = ContentValues()
                registro.put("nombre", nombreReceta)
                registro.put("descripcion", descripcionReceta)
                registro.put("personas", personasReceta)
                registro.put("categoria", categoriaReceta)
                var idInserado = bd.insert("receta", null, registro)
            bd.close()
        }

        RegistroDePrueba()
         */

        //Recibe y filtra por categoría
        var categoriaElegida = intent.extras?.getString("CategoriaElegida").orEmpty()

        fun RecargarRecetas(){
            try{
            val RecetaSQLite = SQLite(this, "administracion", null, 1)
            val bd = RecetaSQLite.writableDatabase

            listaIndicesRecetas.clear()
            listaRecetas.clear()
            lvRecetas.setAdapter(null)
                var fila:Cursor
                if(categoriaElegida.isEmpty()){
                    fila = bd.rawQuery("SELECT rowid, nombre FROM receta", null)
                }else{
                    fila = bd.rawQuery("SELECT rowid, nombre FROM receta WHERE categoria = '${categoriaElegida}'", null)
                }

                with(fila){
                    while(moveToNext()){
                        listaIndicesRecetas.add(fila.getInt(0))
                        listaRecetas.add(fila.getString(1))
                    }
                }
                bd.close()

                val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaRecetas)
                lvRecetas.adapter = adaptador1

            }catch(e: Exception){
                Toast.makeText(this, "Error consulta: "+e.message, Toast.LENGTH_LONG).show()
            }
        }

        RecargarRecetas();

        lvRecetas.setOnItemClickListener(this)

        fun EliminarReceta(indice:Int){
            try{
                var idReceta = -1
                val admin = SQLite(this, "administracion", null, 1)
                val bd = admin.writableDatabase
                val fila = bd.rawQuery("SELECT idReceta FROM receta WHERE rowid = '${indice}'", null)
                if(fila.moveToFirst()){
                    idReceta = fila.getInt(0)
                }else{
                    Toast.makeText(this, "No existe el articulo", Toast.LENGTH_SHORT).show()
                }
                bd.delete("receta", "rowid = '${indice}'", null)
                bd.delete("ingrediente", "idReceta = '${idReceta}'", null)
                bd.close()

                    Toast.makeText(this, "Eliminacion exitosa", Toast.LENGTH_SHORT).show()
            }catch(e:Exception){
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }

        val btnConsultar = findViewById<Button>(R.id.btnConsultarReceta)
        btnConsultar.setOnClickListener{
            if(indiceSeleccionado != -1){
                val llave = listaIndicesRecetas[indiceSeleccionado]
                indiceSeleccionado=-1
                val intento1 = Intent(this, MuestraReceta::class.java)
                intento1.putExtra("LlaveSeleccionada", llave)
                startActivity(intento1)
            }else{
                Toast.makeText(this, "No has seleccionado un elemento de la lista", Toast.LENGTH_SHORT).show()
            }
        }

        val btnActualizar = findViewById<Button>(R.id.btnActualizarReceta)
        btnActualizar.setOnClickListener{
            if(indiceSeleccionado != -1){
                val llave = listaIndicesRecetas[indiceSeleccionado]
                indiceSeleccionado=-1
                val intento1 = Intent(this, FormularioReceta::class.java)
                intento1.putExtra("LlaveSeleccionada2", llave)
                intento1.putExtra("EsNuevo",  false)
                startActivity(intento1)
            }else{
                Toast.makeText(this, "No has seleccionado un elemento de la lista", Toast.LENGTH_SHORT).show()
            }
        }

        val mensaje = AlertDialog.Builder(this)
        mensaje.setTitle("Eliminar receta")
        mensaje.setMessage("¿Desea eliminar esta receta del menú?")

        val btnEliminar = findViewById<Button>(R.id.btnEliminarReceta)
        btnEliminar.setOnClickListener{
            if(indiceSeleccionado != -1){

                mensaje.setPositiveButton(android.R.string.yes){ dialog, which ->
                    Toast.makeText(applicationContext, android.R.string.yes, Toast.LENGTH_SHORT).show()

                    val llave = listaIndicesRecetas[indiceSeleccionado]
                    indiceSeleccionado=-1
                    EliminarReceta(llave)
                    RecargarRecetas()
                }

                mensaje.setNegativeButton(android.R.string.no){ dialog, which ->
                    Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
                }
                mensaje.show()

            }else{
                Toast.makeText(this, "No has seleccionado un elemento de la lista", Toast.LENGTH_SHORT).show()
            }
        }

        val btnRegresar = findViewById<ImageButton>(R.id.btnRegresarRecetas)
        btnRegresar.setOnClickListener {
            val intento1 = Intent(this, MainActivity::class.java)
            startActivity(intento1)
            finish()
        }

        val btnVerCategorias = findViewById<Button>(R.id.btnVerCategorias)
        btnVerCategorias.setOnClickListener {
            val intento1 = Intent(this, Categorias::class.java)
            startActivity(intento1)
            finish()
        }

        val btnAñadir = findViewById<Button>(R.id.btnAñadirReceta)
        btnAñadir.setOnClickListener {
            val intento1 = Intent(this, FormularioReceta::class.java)
            startActivity(intento1)
            finish()
        }



    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var index = parent?.getPositionForView(view).toString().toInt()
        indiceSeleccionado = index
    }
}

