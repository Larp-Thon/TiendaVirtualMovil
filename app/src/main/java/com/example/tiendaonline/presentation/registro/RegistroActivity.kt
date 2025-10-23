package com.example.tiendaonline.presentation.registro

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.tiendaonline.databinding.ActivityRegistroBinding


class RegistroActivity : ComponentActivity() {
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener {
            finish()
        }
    }
}