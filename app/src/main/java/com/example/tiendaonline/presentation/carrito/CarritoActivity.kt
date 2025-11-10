package com.example.tiendaonline.presentation.carrito

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonline.R
import com.example.tiendaonline.adapter.ProductoAdapter
import com.example.tiendaonline.data.CarritoManager
import com.example.tiendaonline.presentation.carrito.ui.theme.CarritoAdapter


class CarritoActivity : ComponentActivity() {

    private lateinit var recyclerCarrito: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var layoutEmpty: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val btnBack = toolbar.findViewById<ImageView>(R.id.logoImage)
        recyclerCarrito = findViewById(R.id.recyclerCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        btnBack.setOnClickListener {
            finish()
        }

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

        val total = carrito.sumOf { it.precio }
        tvTotal.text = "Total: $${total}"
    }

}