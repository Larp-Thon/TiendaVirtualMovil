package com.example.tiendaonline

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonline.adapter.ProductoAdapter
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.models.Producto
import com.example.tiendaonline.presentation.perfil.PerfilActivity
import com.example.tiendaonline.presentation.producto.CrearProductoActivity

class MainMenuActivity : ComponentActivity() {

    private lateinit var productoAdapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""

        toolbar.inflateMenu(R.menu.main_menu)
        val email = intent.getStringExtra("EXTRA_EMAIL")
        val pass = intent.getStringExtra("EXTRA_PASSWORD")

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_crear_producto -> {
                    val intent = Intent(this, CrearProductoActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_mi_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", email)
                    intent.putExtra("EXTRA_PASSWORD", pass)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        if (!email.isNullOrEmpty()) {
            Toast.makeText(this, "Bienvenido: $email", Toast.LENGTH_SHORT).show()
        }

        val dbHelper = DatabaseHelper(this)
        val listaProductos = dbHelper.obtenerProductos()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productoAdapter = ProductoAdapter(listaProductos.toMutableList())
        recyclerView.adapter = productoAdapter
    }

    override fun onResume() {
        super.onResume()
        val dbHelper = DatabaseHelper(this)
        val listaProductos = dbHelper.obtenerProductos()
        productoAdapter.updateData(listaProductos)
        }
}
