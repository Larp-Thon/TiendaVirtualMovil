package com.example.tiendaonline.presentation.carrito

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonline.R
import com.example.tiendaonline.data.CarritoManager
import com.example.tiendaonline.presentation.carrito.ui.theme.CarritoAdapter
import com.google.android.material.appbar.MaterialToolbar

class CarritoActivity : ComponentActivity() {

    private lateinit var recyclerCarrito: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var layoutEmpty: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerCarrito = findViewById(R.id.recyclerCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        val carrito = CarritoManager.obtenerProductos()

        recyclerCarrito.layoutManager = LinearLayoutManager(this)
        recyclerCarrito.adapter = CarritoAdapter(carrito)

        if (carrito.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            recyclerCarrito.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerCarrito.visibility = View.VISIBLE
        }

        val btnPagar = findViewById<TextView>(R.id.btnPagar)
        btnPagar.setOnClickListener {
            Toast.makeText(this, "Gracias por tu compra", Toast.LENGTH_SHORT).show()
        }
        val total = carrito.sumOf { it.precio }
        tvTotal.text = "$$total"
    }
}