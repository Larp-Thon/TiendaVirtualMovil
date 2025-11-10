package com.example.tiendaonline.presentation.carrito.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonline.R
import com.example.tiendaonline.models.Producto

class CarritoAdapter(private val productos: MutableList<Producto>) :
    RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "Precio: $${producto.precio}"

        if (producto.imagenUri != null) {
            holder.imgProducto.setImageURI(android.net.Uri.parse(producto.imagenUri))
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_image_placeholder)
        }
    }

    override fun getItemCount(): Int = productos.size
}