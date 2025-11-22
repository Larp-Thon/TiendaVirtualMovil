package com.example.tiendaonline.presentation.producto

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.tiendaonline.R
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.models.Producto
import com.google.android.material.appbar.MaterialToolbar // Importar Toolbar
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
    private var tempImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_producto)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        imgPreview = findViewById(R.id.imgPreview)
        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        btnGuardar = findViewById(R.id.btnGuardarProducto)

        val idRecibido = intent.getIntExtra("producto_id", -1)
        if (idRecibido != -1) {
            productoId = idRecibido
            cargarDatosProducto(idRecibido)
            btnGuardar.text = "Actualizar Producto"
            toolbar.title = "Editar Producto"
        }

        btnElegirImagen.setOnClickListener {
            mostrarDialogoSeleccionImagen()
        }

        btnGuardar.setOnClickListener {
            guardarProducto()
        }
    }

    private fun mostrarDialogoSeleccionImagen() {
        val opciones = arrayOf("Tomar Foto", "Elegir de Galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccionar Imagen")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> verificarPermisoCamara()
                1 -> seleccionarDeGaleria.launch("image/*")
            }
        }
        builder.show()
    }

    private val seleccionarDeGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imagenUri = uri
                imgPreview.setImageURI(uri)
            }
        }

    private val tomarFoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success && tempImageUri != null) {
                imagenUri = tempImageUri
                imgPreview.setImageURI(imagenUri)
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                abrirCamara()
            } else {
                Toast.makeText(this, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
            }
        }

    private fun verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            abrirCamara()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        val archivoFoto = File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            getExternalFilesDir(null)
        )
        
        tempImageUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            archivoFoto
        )
        
        tomarFoto.launch(tempImageUri)
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

        var rutaFinal: String? = null
        
        if (imagenUri != null) {
             if (imagenUri!!.scheme == "content") {
                 rutaFinal = copiarImagenLocal(imagenUri!!)
             } else if (imagenUri!!.scheme == "file") {
                 rutaFinal = imagenUri!!.path
             }
        }

        val nuevoProducto = Producto(
            id = productoId,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            imagenUri = rutaFinal
        )

        val dbHelper = DatabaseHelper(this)
        
        if (productoId == null) {
            val exito = dbHelper.insertarProducto(nuevoProducto)
            if (exito) {
                Toast.makeText(this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al crear el producto", Toast.LENGTH_SHORT).show()
            }
        } else {
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