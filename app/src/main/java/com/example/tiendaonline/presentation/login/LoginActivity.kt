package com.example.tiendaonline.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaonline.MainMenuActivity
import com.example.tiendaonline.R
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.databinding.ActivityLoginBinding
import com.example.tiendaonline.presentation.perfil.PerfilActivity
import com.example.tiendaonline.presentation.registro.RegistroActivity

class LoginActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        val correo  = findViewById<EditText>(R.id.etEmail)
        val contrasena  = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnIrRegistro  = findViewById<Button>(R.id.btnRegistrar)


        btnLogin.setOnClickListener {
            if (dbHelper.validarLogin(correo.text.toString(), contrasena.text.toString())) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainMenuActivity::class.java)
                intent.putExtra("EXTRA_EMAIL", correo.text.toString())
                intent.putExtra("EXTRA_PASSWORD", contrasena.text.toString())
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
        btnIrRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}