package com.example.tiendaonline.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tiendaonline.models.Cliente
import com.example.tiendaonline.models.Producto

class DatabaseHelper (context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "tienda.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CLIENTES = "clientes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_CORREO = "correo"
        private const val COLUMN_CONTRASENA = "contraseña"

        private const val TABLE_PRODUCTOS = "productos"
        private const val COLUMN_IDPRODUCTO = "idProducto"
        private const val COLUMN_NOMBREPRODUCTO = "nombreProducto"
        private const val COLUMN_DESCRIPCION = "descripcion"
        private const val COLUMN_PRECIO = "precio"
        private const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CLIENTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT UNIQUE NOT NULL,
                $COLUMN_CONTRASENA TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTable)
        val createTableProduct = """
            CREATE TABLE $TABLE_PRODUCTOS (
                $COLUMN_IDPRODUCTO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBREPRODUCTO TEXT NOT NULL,
                $COLUMN_DESCRIPCION TEXT UNIQUE NOT NULL,
                $COLUMN_PRECIO TEXT NOT NULL,
                $COLUMN_IMAGEN TEXT
            );
        """.trimIndent()
        db.execSQL(createTableProduct)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun registrarCliente(cliente: Cliente): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, cliente.nombre)
            put(COLUMN_CORREO, cliente.correo)
            put(COLUMN_CONTRASENA, cliente.contrasena)
        }
        val result = db.insert(TABLE_CLIENTES, null, values)
        db.close()
        return result != -1L
    }

    fun existeCorreo(correo: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_CLIENTES WHERE $COLUMN_CORREO=?"
        val cursor = db.rawQuery(query, arrayOf(correo))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun validarLogin(correo: String, contrasena: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_CLIENTES WHERE $COLUMN_CORREO=? AND $COLUMN_CONTRASENA=?"
        val cursor = db.rawQuery(query, arrayOf(correo, contrasena))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun insertarProducto(producto: Producto): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBREPRODUCTO, producto.nombre)
            put(COLUMN_DESCRIPCION, producto.descripcion)
            put(COLUMN_PRECIO, producto.precio)
            put(COLUMN_IMAGEN, producto.imagenUri ?: "")
        }

        val result = db.insert(TABLE_PRODUCTOS, null, values)
        db.close()
        return result != -1L
    }

    fun obtenerProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUCTOS", null)

        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IDPRODUCTO)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBREPRODUCTO)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO)),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN))
                )
                lista.add(producto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminarProducto(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_PRODUCTOS, "$COLUMN_IDPRODUCTO = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }


}