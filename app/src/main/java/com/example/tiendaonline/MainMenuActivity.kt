package com.example.tiendaonline

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonline.adapter.ProductoAdapter
import com.example.tiendaonline.data.CarritoManager
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.presentation.carrito.CarritoActivity
import com.example.tiendaonline.presentation.contacto.Contacto
import com.example.tiendaonline.presentation.perfil.PerfilActivity
import com.example.tiendaonline.presentation.producto.CrearProductoActivity

class MainMenuActivity : ComponentActivity() {

    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        
        dbHelper = DatabaseHelper(this)

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
                R.id.menu_ver_carrito -> {
                    startActivity(Intent(this, CarritoActivity::class.java))
                    true
                }
                R.id.menu_mi_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    intent.putExtra("EXTRA_EMAIL", email)
                    intent.putExtra("EXTRA_PASSWORD", pass)
                    startActivity(intent)
                    true
                }
                R.id.menu_contacto -> {
                    startActivity(Intent(this, Contacto::class.java))
                    true
                }
                else -> false
            }
        }

        val listaProductos = dbHelper.obtenerProductos()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        productoAdapter = ProductoAdapter(
            listaProductos.toMutableList(),
            onAgregarCarrito = { producto ->
                CarritoManager.agregarProducto(producto)
                Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show()
            },
            onEditar = { producto ->
                val intent = Intent(this, CrearProductoActivity::class.java)
                intent.putExtra("producto_id", producto.id)
                startActivity(intent)
            },
            onEliminar = { producto ->
                val eliminado = producto.id?.let { dbHelper.eliminarProducto(it) } ?: false
                if (eliminado) {
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    val nuevaLista = dbHelper.obtenerProductos()
                    productoAdapter.updateData(nuevaLista)
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
        )
        
        recyclerView.adapter = productoAdapter
    }

    override fun onResume() {
        super.onResume()
        val listaProductos = dbHelper.obtenerProductos()
        productoAdapter.updateData(listaProductos)
    }
}