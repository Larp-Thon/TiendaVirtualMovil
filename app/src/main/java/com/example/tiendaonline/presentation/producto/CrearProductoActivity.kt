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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_producto)

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        imgPreview = findViewById(R.id.imgPreview)
        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        btnGuardar = findViewById(R.id.btnGuardarProducto)

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

        val rutaLocal = imagenUri?.let { copiarImagenLocal(it) }

        val nuevoProducto = Producto(
            id = null,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            imagenUri = rutaLocal
        )

        val dbHelper = DatabaseHelper(this)
        val exito = dbHelper.insertarProducto(nuevoProducto)

        if (exito) {
            Toast.makeText(this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al crear el producto", Toast.LENGTH_SHORT).show()
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