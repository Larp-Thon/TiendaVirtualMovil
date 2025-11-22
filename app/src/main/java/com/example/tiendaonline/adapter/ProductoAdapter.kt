package com.example.tiendaonline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tiendaonline.R
import com.example.tiendaonline.models.Producto
import java.io.File

class ProductoAdapter(
    private var listaProductos: MutableList<Producto>,
    private val onAgregarCarrito: (Producto) -> Unit,
    private val onEditar: (Producto) -> Unit,
    private val onEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val imgProducto: ImageView = view.findViewById(R.id.ivProducto)
        val btnMenu: ImageView = itemView.findViewById(R.id.btnMenuProducto)
        val btnAgregarCarrito: Button = itemView.findViewById(R.id.btnAgregarCarrito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvDescripcion.text = producto.descripcion
        holder.tvPrecio.text = "$${producto.precio}"

        // Uso de Coil para imagenes
        if (!producto.imagenUri.isNullOrEmpty()) {
            holder.imgProducto.load(File(producto.imagenUri)) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.btnAgregarCarrito.setOnClickListener {
            onAgregarCarrito(producto)
        }

        holder.btnMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.btnMenu)
            popup.menuInflater.inflate(R.menu.menu_item_producto, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_editar -> {
                        onEditar(producto)
                        true
                    }
                    R.id.menu_eliminar -> {
                        onEliminar(producto)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = listaProductos.size

    fun updateData(newList: List<Producto>) {
        listaProductos.clear()
        listaProductos.addAll(newList)
        notifyDataSetChanged()
    }
}