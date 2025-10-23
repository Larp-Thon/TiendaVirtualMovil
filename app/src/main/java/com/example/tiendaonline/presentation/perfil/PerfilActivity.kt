package com.example.tiendaonline.presentation.perfil

import android.content.Intent
import com.example.tiendaonline.R
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.tiendaonline.presentation.login.LoginActivity

class PerfilActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val tvEmail = findViewById<TextView>(R.id.tvEmailUsuario)
        val tvPassword = findViewById<TextView>(R.id.tvPasswordUsuario)

        val email = intent.getStringExtra("EXTRA_EMAIL") ?: "No disponible"
        val password = intent.getStringExtra("EXTRA_PASSWORD") ?: ""

        tvEmail?.text = "Correo: $email"
        tvPassword?.text = "Contraseña: ${"•".repeat(password.length)}"

        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}