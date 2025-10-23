package com.example.tiendaonline.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.tiendaonline.R
import com.example.tiendaonline.databinding.ActivityLoginBinding
import com.example.tiendaonline.presentation.perfil.PerfilActivity
import com.example.tiendaonline.presentation.registro.RegistroActivity

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Completa correo y contraseña", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PerfilActivity::class.java).apply {
                putExtra("EXTRA_EMAIL", email)
                putExtra("EXTRA_PASSWORD", password)
            }
            startActivity(intent)
        }
        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}