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
import com.foodsaver.data.Estado
import com.foodsaver.data.estadoDe

class UrgentesFragment : Fragment(R.layout.fragment_urgentes) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<FrameLayout>(R.id.container)
        val inflater = LayoutInflater.from(requireContext())
        val urgentes = AppSession.urgentes()

        if (urgentes.isEmpty()) {
            val empty = inflater.inflate(R.layout.item_empty_state, container, false)
            empty.findViewById<TextView>(R.id.tvIcon).text = "🎉"
            empty.findViewById<TextView>(R.id.tvMsg).text = "¡Todo bajo control!"
            empty.findViewById<TextView>(R.id.tvSub).text = "No tienes alimentos urgentes."
            container.addView(empty)
            return
        }

        val list = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        urgentes.forEach { alimento ->
            val itemView = inflater.inflate(R.layout.item_alimento, list, false)
            val estado = estadoDe(alimento.dias)
            val icon = if (estado == Estado.URGENTE) "⚠️" else "🕐"
            itemView.findViewById<TextView>(R.id.tvNombre).text = "$icon ${alimento.nombre}"
            itemView.findViewById<TextView>(R.id.tvSubtitulo).text =
                "Caduca en ${alimento.dias} días · ${alimento.categoria}"
            bindBadge(itemView, estado)
            list.addView(itemView)
        }
        container.addView(list)
    }
}
