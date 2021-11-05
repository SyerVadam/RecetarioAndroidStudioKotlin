package com.desapps.recetario

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase.CursorFactory

class SQLite (context:Context, name:String, factory: SQLiteDatabase.CursorFactory?, version: Int): SQLiteOpenHelper(context, name, factory, version){

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL("create table receta (idReceta Integer PRIMARY KEY autoincrement, nombre text, descripcion text, personas Integer, categoria text) ")
        db.execSQL("create table ingrediente (idIngrediente Integer PRIMARY KEY autoincrement, nombre text, porcion real, medida text, idReceta Integer, FOREIGN KEY(idReceta) REFERENCES receta(idReceta))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}