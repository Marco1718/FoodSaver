package com.foodsaver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.foodsaver.R
import com.foodsaver.data.AppDatabase
import com.foodsaver.data.AppSession
import kotlinx.coroutines.launch

class RecetasFragment : Fragment(R.layout.fragment_recetas) {

    private val alimentoDao by lazy {
        AppDatabase.getDatabase(requireContext().applicationContext).alimentoDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ownerEmail = AppSession.currentUser?.email ?: return
        val container = view.findViewById<FrameLayout>(R.id.container)
        val inflater = LayoutInflater.from(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            alimentoDao.obtenerPorUsuario(ownerEmail).collect { todos ->
                val recetas = AppSession.recetasSugeridas(todos)
                container.removeAllViews()

                if (recetas.isEmpty()) {
                    val empty = inflater.inflate(R.layout.item_empty_state, container, false)
                    empty.findViewById<TextView>(R.id.tvIcon).text = "🍳"
                    empty.findViewById<TextView>(R.id.tvMsg).text = "Sin sugerencias aún."
                    empty.findViewById<TextView>(R.id.tvSub).text = "Agrega leche, tomate,\nhuevo, queso o tortilla."
                    container.addView(empty)
                    return@collect
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
    }
}
