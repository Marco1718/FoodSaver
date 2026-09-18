package com.foodsaver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.foodsaver.R
import com.foodsaver.data.AppSession

class RecetasFragment : Fragment(R.layout.fragment_recetas) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<FrameLayout>(R.id.container)
        val inflater = LayoutInflater.from(requireContext())
        val recetas = AppSession.recetasSugeridas()

        if (recetas.isEmpty()) {
            val empty = inflater.inflate(R.layout.item_empty_state, container, false)
            empty.findViewById<TextView>(R.id.tvIcon).text = "🍳"
            empty.findViewById<TextView>(R.id.tvMsg).text = "Sin sugerencias aún."
            empty.findViewById<TextView>(R.id.tvSub).text = "Agrega leche, tomate,\nhuevo, queso o tortilla."
            container.addView(empty)
            return
        }

        val list = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        recetas.forEach { (nombre, descripcion, tag) ->
            val itemView = inflater.inflate(R.layout.item_receta, list, false)
            itemView.findViewById<TextView>(R.id.tvNombre).text = "🍳 $nombre"
            itemView.findViewById<TextView>(R.id.tvDescripcion).text = descripcion
            itemView.findViewById<TextView>(R.id.tvTag).text = tag
            list.addView(itemView)
        }
        container.addView(list)
    }
}
