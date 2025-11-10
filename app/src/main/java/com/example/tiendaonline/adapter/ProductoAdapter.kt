package com.example.tiendaonline.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonline.R
import com.example.tiendaonline.models.Producto
import android.net.Uri
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import com.example.tiendaonline.data.CarritoManager
import com.example.tiendaonline.data.DatabaseHelper
import com.example.tiendaonline.presentation.producto.CrearProductoActivity
import java.io.File

class ProductoAdapter(
    private var listaProductos: MutableList<Producto>
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

        val imagenPath = producto.imagenUri

        if (!imagenPath.isNullOrEmpty()) {
            val archivo = File(imagenPath)
            if (archivo.exists()) {
                holder.imgProducto.setImageURI(Uri.fromFile(archivo))
            } else {
                holder.imgProducto.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.btnAgregarCarrito.setOnClickListener {
            CarritoManager.agregarProducto(producto)
            Toast.makeText(holder.itemView.context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        holder.btnMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.btnMenu)
            popup.menuInflater.inflate(R.menu.menu_item_producto, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_editar -> {
                        val context = holder.itemView.context
                        val intent = Intent(context, CrearProductoActivity::class.java)
                        intent.putExtra("producto_id", producto.id)
                        context.startActivity(intent)
                        true
                    }
                    R.id.menu_eliminar -> {
                        val context = holder.itemView.context
                        val db = DatabaseHelper(context)
                        val eliminado = db.eliminarProducto(producto.id!!)
                        if (eliminado) {
                            Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show()
                            listaProductos.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
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