package com.example.tiendaonline.presentation.perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.tiendaonline.R
import com.example.tiendaonline.presentation.login.LoginActivity
import com.google.android.material.appbar.MaterialToolbar

class PerfilActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val tvEmail = findViewById<TextView>(R.id.tvEmailUsuario)
        val tvPassword = findViewById<TextView>(R.id.tvPasswordUsuario)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        val email = intent.getStringExtra("EXTRA_EMAIL") ?: "usuario@tienda.com"
        val pass = intent.getStringExtra("EXTRA_PASSWORD") ?: "123456"

        tvEmail.text = email
        tvPassword.text = "•".repeat(pass.length)

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}