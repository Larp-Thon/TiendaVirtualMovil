package com.example.tiendaonline.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.example.tiendaonline.R
import com.example.tiendaonline.presentation.bienvenida.BienvenidaActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, BienvenidaActivity::class.java))
            finish()
        }, 2000)
    }
}