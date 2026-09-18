package com.foodsaver.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foodsaver.R
import com.foodsaver.data.User

class UsuariosAdapter : RecyclerView.Adapter<UsuariosAdapter.UserViewHolder>() {

    private var items: List<User> = emptyList()

    fun submitList(nuevos: List<User>) {
        items = nuevos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombreCompleto: TextView = itemView.findViewById(R.id.tvNombreCompleto)
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)

        fun bind(user: User) {
            tvNombreCompleto.text = "${user.nombre} ${user.apellidos}"
            tvId.text = "ID: ${user.id}"
            tvDireccion.text = "📍 ${user.direccion}"
            tvTelefono.text = "📞 ${user.telefono}"
        }
    }
}
