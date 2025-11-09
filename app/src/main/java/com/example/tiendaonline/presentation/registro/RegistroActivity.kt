package com.example.tiendaonline.presentation.registro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.R
import com.example.tiendaonline.databinding.ActivityRegistroBinding
import com.example.tiendaonline.models.Cliente


class RegistroActivity : ComponentActivity() {
    private lateinit var binding: ActivityRegistroBinding

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        val nombre = findViewById<EditText>(R.id.etNombre)
        val correo = findViewById<EditText>(R.id.etCorreo)
        val contrasena = findViewById<EditText>(R.id.etContraseña)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {
            val cliente = Cliente(
                nombre = nombre.text.toString(),
                correo = correo.text.toString(),
                contrasena = contrasena.text.toString()
            )

            when {
                cliente.nombre.isBlank() || cliente.correo.isBlank() || cliente.contrasena.isBlank() -> {
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT)
                        .show()
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(cliente.correo).matches() -> {
                    Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                }

                dbHelper.existeCorreo(cliente.correo) -> {
                    Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    if (dbHelper.registrarCliente(cliente)) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}