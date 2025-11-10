package com.example.tiendaonline.data

import com.example.tiendaonline.models.Producto

object CarritoManager {
    private val productosEnCarrito = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productosEnCarrito.add(producto)
    }

    fun obtenerProductos(): MutableList<Producto> {
        return productosEnCarrito
    }

    fun limpiarCarrito() {
        productosEnCarrito.clear()
    }
}