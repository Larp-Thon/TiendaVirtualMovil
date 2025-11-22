package com.example.tiendaonline.presentation.producto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tiendaonline.R
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.models.Producto
import com.example.tiendaonline.presentation.producto.ui.theme.TiendaOnlineTheme
import java.io.File
import java.io.FileOutputStream

class CrearProductoActivity : ComponentActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnElegirImagen: Button
    private lateinit var btnGuardar: Button

    private var imagenUri: Uri? = null
    private var productoId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_producto)

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        imgPreview = findViewById(R.id.imgPreview)
        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        btnGuardar = findViewById(R.id.btnGuardarProducto)

        // Verificar si estamos en modo edición
        val idRecibido = intent.getIntExtra("producto_id", -1)
        if (idRecibido != -1) {
            productoId = idRecibido
            cargarDatosProducto(idRecibido)
            btnGuardar.text = "Actualizar Producto"
        }

        val seleccionarImagen =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    imagenUri = uri
                    imgPreview.setImageURI(uri)
                }
            }

        btnElegirImagen.setOnClickListener {
            seleccionarImagen.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            guardarProducto()
        }
    }

    private fun cargarDatosProducto(id: Int) {
        val dbHelper = DatabaseHelper(this)
        val producto = dbHelper.obtenerProductoPorId(id)

        if (producto != null) {
            etNombre.setText(producto.nombre)
            etDescripcion.setText(producto.descripcion)
            etPrecio.setText(producto.precio.toString())

            if (!producto.imagenUri.isNullOrEmpty()) {
                val file = File(producto.imagenUri)
                if (file.exists()) {
                    imagenUri = Uri.fromFile(file)
                    imgPreview.setImageURI(imagenUri)
                }
            }
        }
    }

    private fun guardarProducto() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precioTexto = etPrecio.text.toString().trim()

        if (nombre.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        if (precio == null) {
            Toast.makeText(this, "El precio no es válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Si seleccionó una nueva imagen, la copiamos. Si no, mantenemos la anterior (si es edición)
        // OJO: Aquí hay un detalle. Si estamos editando y NO cambiamos la imagen, imagenUri podría ser null si no la seteamos al cargar.
        // Pero en cargarDatosProducto ya hacemos imagenUri = Uri.fromFile(...), así que si tiene imagen, imagenUri no será null.
        // Sin embargo, si imagenUri apunta a un archivo local ya existente (porque lo cargamos al editar),
        // copiarImagenLocal volvería a copiarlo sobre sí mismo o crearía una copia nueva.
        // Para simplificar: si la URI es "file://...", significa que ya es local y no necesitamos copiarla de nuevo.
        
        var rutaFinal: String? = null
        
        if (imagenUri != null) {
             if (imagenUri!!.scheme == "content") {
                 // Es una nueva imagen de la galería, hay que copiarla
                 rutaFinal = copiarImagenLocal(imagenUri!!)
             } else {
                 // Ya es un archivo local (file://), mantenemos la ruta
                 rutaFinal = imagenUri!!.path
             }
        }

        val nuevoProducto = Producto(
            id = productoId, // Si es null, es nuevo. Si tiene valor, es edición.
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            imagenUri = rutaFinal
        )

        val dbHelper = DatabaseHelper(this)
        
        if (productoId == null) {
            // Crear nuevo
            val exito = dbHelper.insertarProducto(nuevoProducto)
            if (exito) {
                Toast.makeText(this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al crear el producto", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Actualizar existente
            val exito = dbHelper.actualizarProducto(nuevoProducto)
            if (exito) {
                Toast.makeText(this, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copiarImagenLocal(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            val nombreArchivo = "producto_${System.currentTimeMillis()}.jpg"
            val archivoDestino = File(filesDir, nombreArchivo)
            val outputStream = FileOutputStream(archivoDestino)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            archivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}