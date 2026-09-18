package com.foodsaver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.foodsaver.MainActivity
import com.foodsaver.R
import com.foodsaver.data.AppSession
import com.foodsaver.data.estadoDe

class DespensaFragment : Fragment(R.layout.fragment_despensa) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render(view)
    }

    private fun render(root: View) {
        val container = root.findViewById<FrameLayout>(R.id.container)
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        if (AppSession.alimentos.isEmpty()) {
            val empty = inflater.inflate(R.layout.item_empty_state, container, false)
            empty.findViewById<TextView>(R.id.tvIcon).text = "🥕"
            empty.findViewById<TextView>(R.id.tvMsg).text = "Tu despensa está vacía."
            empty.findViewById<TextView>(R.id.tvSub).text = "Agrega tu primer alimento."
            container.addView(empty)
            return
        }

        val list = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        AppSession.alimentos.toList().forEach { alimento ->
            val itemView = inflater.inflate(R.layout.item_alimento, list, false)
            val estado = estadoDe(alimento.dias)
            itemView.findViewById<TextView>(R.id.tvNombre).text = alimento.nombre
            itemView.findViewById<TextView>(R.id.tvSubtitulo).text =
                "${alimento.categoria} · ${alimento.cantidad} unidad${if (alimento.cantidad != 1) "es" else ""}"
            bindBadge(itemView, estado)

            val actionsRow = itemView.findViewById<View>(R.id.actionsRow)
            actionsRow.visibility = View.VISIBLE
            itemView.findViewById<TextView>(R.id.btnComido).setOnClickListener {
                AppSession.alimentos.removeAll { it.id == alimento.id }
                Toast.makeText(requireContext(), "¡Consumiste ${alimento.nombre}!", Toast.LENGTH_SHORT).show()
                (activity as? MainActivity)?.actualizarHeader()
                render(root)
            }
            itemView.findViewById<TextView>(R.id.btnEliminar).setOnClickListener {
                AppSession.alimentos.removeAll { it.id == alimento.id }
                Toast.makeText(requireContext(), "Alimento eliminado.", Toast.LENGTH_SHORT).show()
                (activity as? MainActivity)?.actualizarHeader()
                render(root)
            }
            list.addView(itemView)
        }
        container.addView(list)
    }
}
