package com.foodsaver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.foodsaver.MainActivity
import com.foodsaver.R
import com.foodsaver.data.AppDatabase
import com.foodsaver.data.AppSession
import com.foodsaver.data.estadoDe
import kotlinx.coroutines.launch

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private val alimentoDao by lazy {
        AppDatabase.getDatabase(requireContext().applicationContext).alimentoDao()
    }

    private val pasos = listOf(
        "📝" to "Registra los alimentos que tienes en casa.",
        "🔍" to "FoodSaver identifica cuáles están por caducar.",
        "🍽️" to "Consulta sugerencias para aprovecharlos."
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ids = listOf(R.id.how1, R.id.how2, R.id.how3)
        ids.forEachIndexed { i, id ->
            val row = view.findViewById<View>(id)
            row.findViewById<TextView>(R.id.icon).text = pasos[i].first
            row.findViewById<TextView>(R.id.text).text = pasos[i].second
        }

        val ownerEmail = AppSession.currentUser?.email ?: return

        // Observa Room (solo los alimentos de este usuario): actualiza el
        // resumen "Próximos a caducar" y los contadores del header.
        viewLifecycleOwner.lifecycleScope.launch {
            alimentoDao.obtenerPorUsuario(ownerEmail).collect { todos ->
                val urgentes = AppSession.urgentes(todos).take(3)
                val container = view.findViewById<View>(R.id.proximosContainer)
                val list = view.findViewById<LinearLayout>(R.id.proximosList)
                list.removeAllViews()

                if (urgentes.isEmpty()) {
                    container.visibility = View.GONE
                } else {
                    container.visibility = View.VISIBLE
                    val inflater = LayoutInflater.from(requireContext())
                    urgentes.forEach { alimento ->
                        val itemView = inflater.inflate(R.layout.item_alimento, list, false)
                        val estado = estadoDe(alimento.dias)
                        itemView.findViewById<TextView>(R.id.tvNombre).text = alimento.nombre
                        itemView.findViewById<TextView>(R.id.tvSubtitulo).text = "${alimento.dias} días restantes"
                        bindBadge(itemView, estado)
                        list.addView(itemView)
                    }
                }

                (activity as? MainActivity)?.actualizarStatsInicio(
                    total = todos.size,
                    urgentes = AppSession.soloUrgentes(todos).size
                )
            }
        }
    }
}
