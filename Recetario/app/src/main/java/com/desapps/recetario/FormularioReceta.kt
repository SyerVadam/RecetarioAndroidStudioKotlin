package com.desapps.recetario

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import java.io.Serializable

var indiceSeleccionado2 = -1

class FormularioReceta : AppCompatActivity(), AdapterView.OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_receta)

        val etNombre = findViewById<EditText>(R.id.etNombreReceta)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcionReceta)
        val etPersonas = findViewById<EditText>(R.id.etPersonas)
        val spnrCategoria = findViewById<Spinner>(R.id.spnrCategoria)
        val btnAgregarIngrediente = findViewById<Button>(R.id.btnAgregarIngrediente)
        val btnEliminarIngrediente = findViewById<Button>(R.id.btnEliminarIngrediente)
        val lvIngredientes = findViewById<ListView>(R.id.lvIngredientesFormulario)
        val btnRegistrarReceta = findViewById<Button>(R.id.btnRegistrarReceta)

        //Carga en spinner las categorías
        val listaCategorias = arrayOf("Postres", "Sopas", "Coctelería", "Ensaladas")
        val adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCategorias)
        spnrCategoria.adapter = adaptador

        data class Ingrediente(var idIngrediente:Int, var nombre:String, var cantidad:Double, var medida:String) :Serializable {
            override fun toString(): String {
                return (nombre + " " + cantidad + " " + medida)
            }
        }
        //Declaración de listas mutables de ingredientes
        var listaIngredientes = mutableListOf<Ingrediente>()
        var listaStringIngredientes = mutableListOf<String>()

        // Recibe en caso de actualizar la receta
        var esNuevo = intent.extras?.getBoolean("EsNuevo")?:true
        val llaveRecetaActualizacion = intent.extras?.getInt("LlaveSeleccionada2")?:-1
        var idReceta = -1

        //Solo se invoca más adelante en caso de ser una actualización y no un registro
        fun CargaRecetaPorActualizar(){
            try{
                val admin = SQLite(this, "administracion", null, 1)
                val bd = admin.writableDatabase
                var fila = bd.rawQuery("SELECT idReceta, nombre, descripcion, personas FROM receta WHERE rowid ='${llaveRecetaActualizacion}'", null)
                if(fila.moveToFirst()){
                    idReceta = fila.getInt(0)
                    etNombre.setText(fila.getString(1))
                    etDescripcion.setText(fila.getString(2))
                    etPersonas.setText(fila.getInt(3).toString())
                }else{
                    Toast.makeText(this, "No se encontró la receta", Toast.LENGTH_SHORT).show()
                }
                fila = bd.rawQuery("SELECT idIngrediente, nombre, porcion, medida FROM ingrediente WHERE idReceta = '${idReceta}'", null)
                with(fila){
                    while(moveToNext()){
                        var ingrediente = Ingrediente(fila.getInt(0), fila.getString(1), fila.getDouble(2), fila.getString(3))
                        listaIngredientes.add(ingrediente)
                        listaStringIngredientes.add(ingrediente.toString())
                    }
                }
                val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaStringIngredientes)
                lvIngredientes.adapter = adaptador1
                bd.close()
            }catch(e: Exception){
                Toast.makeText(this, e.message.toString() , Toast.LENGTH_LONG).show()
            }
        }

        if(!esNuevo){
            btnRegistrarReceta.setText("ACTUALIZAR RECETA")
            CargaRecetaPorActualizar()
        }


        //Recibe ingrediente como resultado de la otra actividad: FormularioIngrediente
        var obtenerIngrediente = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
            var nombreIngrediente = activityResult.data?.getStringExtra("nombreIngrediente").orEmpty()
            var cantidadIngrediente = activityResult.data?.getStringExtra("cantidadIngrediente").orEmpty().toDouble()
            var medidaIngrediente = activityResult.data?.getStringExtra("medidaIngrediente").orEmpty()

            var ingrediente = Ingrediente(-1, nombreIngrediente, cantidadIngrediente, medidaIngrediente)

            listaIngredientes.add(ingrediente)
            listaStringIngredientes.add(ingrediente.toString())

            val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaStringIngredientes)
            lvIngredientes.adapter = adaptador1
        }

        btnAgregarIngrediente.setOnClickListener{
            //Nueva actividad, formulario para ingrediente
            val intento1 = Intent(this, FormularioIngrediente::class.java)
            obtenerIngrediente.launch(intento1)
        }

        btnEliminarIngrediente.setOnClickListener{
            if(indiceSeleccionado2 != -1){
                listaIngredientes.removeAt(indiceSeleccionado2)
                listaStringIngredientes.removeAt(indiceSeleccionado2)
                indiceSeleccionado2=-1
                val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaStringIngredientes)
                lvIngredientes.adapter = adaptador1
            }else{
                Toast.makeText(this, "No has seleccionado un elemento de la lista", Toast.LENGTH_SHORT).show()
            }
        }

        lvIngredientes.setOnItemClickListener(this)

        fun RegistrarReceta(){
            val nombreReceta = etNombre.text.toString()
            val descripcionReceta = etDescripcion.text.toString()
            val personasReceta = etPersonas.text.toString().toInt()
            val categoriaReceta = spnrCategoria.selectedItem.toString()

            val admin = SQLite(this, "administracion", null, 1)
            val bd = admin.writableDatabase

            try {
            val registro = ContentValues()
            registro.put("nombre", nombreReceta)
            registro.put("descripcion", descripcionReceta)
            registro.put("personas", personasReceta)
            registro.put("categoria", categoriaReceta)
            var idInserado = bd.insert("receta", null, registro)
                registro.clear()

                listaIngredientes.forEach {
                    var registro = ContentValues()
                    registro.put("nombre", it.nombre.toString())
                    registro.put("porcion", it.cantidad.toDouble())
                    registro.put("medida", it.medida.toString())
                    registro.put("idReceta", idInserado.toInt())
                    bd.insert("ingrediente", null, registro)
                    registro.clear()
                }

                Toast.makeText(this, "Registrado con éxito", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }finally {
                bd.close()
            }
        }

        fun ActualizarReceta(){
            val nombreReceta = etNombre.text.toString()
            val descripcionReceta = etDescripcion.text.toString()
            val personasReceta = etPersonas.text.toString().toInt()
            val categoriaReceta = spnrCategoria.selectedItem.toString()

            try {
                val admin = SQLite(this, "administracion", null, 1)
                val bd = admin.writableDatabase

                val registro = ContentValues()
                registro.put("nombre", nombreReceta)
                registro.put("descripcion", descripcionReceta)
                registro.put("personas", personasReceta)
                registro.put("categoria", categoriaReceta)
                var idActualizado = bd.update("receta",  registro, "rowid = '${llaveRecetaActualizacion}'", null)
                registro.clear()

                listaIngredientes.forEach {
                    var registro = ContentValues()
                    registro.put("nombre", it.nombre.toString())
                    registro.put("porcion", it.cantidad.toDouble())
                    registro.put("medida", it.medida.toString())
                    bd.update("ingrediente", registro, "idIngrediente = '${it.idIngrediente}'", null)
                    registro.clear()
                }
                bd.close()

                Toast.makeText(this, "Registrado con éxito", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }

        val mensaje = AlertDialog.Builder(this)
        mensaje.setTitle("Actualizar receta")
        mensaje.setMessage("¿Seguro que desea actualizar esta receta?")

        btnRegistrarReceta.setOnClickListener{
            if(etNombre.text.isEmpty() || etDescripcion.text.isEmpty() || etPersonas.text.isEmpty()){
                Toast.makeText(this, "Debe escribir un nombre y una descripción para la receta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(spnrCategoria.selectedItem == null){
                Toast.makeText(this, "Seleccione una categoría para tu receta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(listaStringIngredientes.isEmpty()){
                Toast.makeText(this, "Añada ingredientes a su receta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(esNuevo){
                RegistrarReceta()
                val intento1 = Intent(this, ListaRecetas::class.java)
                startActivity(intento1)
                finish()
            }else{

                mensaje.setPositiveButton(android.R.string.yes){ dialog, which ->
                    Toast.makeText(applicationContext, android.R.string.yes, Toast.LENGTH_SHORT).show()

                    ActualizarReceta()
                    val intento1 = Intent(this, ListaRecetas::class.java)
                    startActivity(intento1)
                    finish()
                }
                mensaje.setNegativeButton(android.R.string.no){ dialog, which ->
                    Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()

                }
                mensaje.show()
            }
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var index = parent?.getPositionForView(view).toString().toInt()
        indiceSeleccionado2 = index
    }
}

