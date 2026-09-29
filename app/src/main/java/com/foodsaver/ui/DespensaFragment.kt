package com.foodsaver.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.foodsaver.MainActivity
import com.foodsaver.R
import com.foodsaver.data.Alimento
import com.foodsaver.data.AppDatabase
import com.foodsaver.data.AppSession
import com.foodsaver.data.estadoDe
import com.foodsaver.util.DialogUtils
import kotlinx.coroutines.launch

/**
 * Despensa: lista + buscador en tiempo real + chips de categoría.
 *
 * La lista completa del usuario se observa una sola vez desde Room (Flow);
 * el texto de búsqueda y la categoría elegida se aplican como un filtro en
 * memoria sobre esa lista (ya está acotada a "mis alimentos", así que es
 * instantáneo) para no tener que re-consultar la base de datos en cada
 * letra que el usuario escribe.
 */
class DespensaFragment : Fragment(R.layout.fragment_despensa) {

    private val alimentoDao by lazy {
        AppDatabase.getDatabase(requireContext().applicationContext).alimentoDao()
    }

    private var todosLosAlimentos: List<Alimento> = emptyList()
    private var textoBusqueda: String = ""
    private var categoriaSeleccionada: String? = null // null = "Todas"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ownerEmail = AppSession.currentUser?.email ?: return

        armarChips(view)

        view.findViewById<EditText>(R.id.etBuscar).addTextChangedListener(onTextChanged = { texto, _, _, _ ->
            textoBusqueda = texto?.toString().orEmpty()
            render(view)
        })

        // Observa Room UNA vez; el filtrado (texto + categoría) es local.
        viewLifecycleOwner.lifecycleScope.launch {
            alimentoDao.obtenerPorUsuario(ownerEmail).collect { alimentos ->
                todosLosAlimentos = alimentos
                render(view)
            }
        }
    }

    private fun armarChips(root: View) {
        val chipsContainer = root.findViewById<LinearLayout>(R.id.chipsContainer)
        chipsContainer.removeAllViews()
        val categorias = listOf("Todas") + AppSession.CATEGORIAS

        categorias.forEach { categoria ->
            val chip = TextView(requireContext()).apply {
                text = categoria
                textSize = 12f
                setPadding(28, 14, 28, 14)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 20
                layoutParams = params
                setOnClickListener {
                    categoriaSeleccionada = if (categoria == "Todas") null else categoria
                    armarChips(root) // repinta estados seleccionado/no seleccionado
                    render(root)
                }
            }
            val estaSeleccionado = (categoria == "Todas" && categoriaSeleccionada == null) || categoria == categoriaSeleccionada
            if (estaSeleccionado) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(resources.getColor(R.color.white, requireContext().theme))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(resources.getColor(R.color.text_body, requireContext().theme))
            }
            chipsContainer.addView(chip)
        }
    }

    private fun render(root: View) {
        val filtrados = todosLosAlimentos.filter { alimento ->
            val coincideNombre = alimento.nombre.contains(textoBusqueda, ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == null || alimento.categoria == categoriaSeleccionada
            coincideNombre && coincideCategoria
        }

        val container = root.findViewById<FrameLayout>(R.id.container)
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        if (filtrados.isEmpty()) {
            val empty = inflater.inflate(R.layout.item_empty_state, container, false)
            if (todosLosAlimentos.isEmpty()) {
                empty.findViewById<TextView>(R.id.tvIcon).text = "🥕"
                empty.findViewById<TextView>(R.id.tvMsg).text = "Tu despensa está vacía."
                empty.findViewById<TextView>(R.id.tvSub).text = "Agrega tu primer alimento."
            } else {
                empty.findViewById<TextView>(R.id.tvIcon).text = "🔎"
                empty.findViewById<TextView>(R.id.tvMsg).text = "Sin resultados."
                empty.findViewById<TextView>(R.id.tvSub).text = "Prueba con otro nombre o categoría."
            }
            container.addView(empty)
            return
        }

        val list = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        filtrados.forEach { alimento ->
            val itemView = inflater.inflate(R.layout.item_alimento, list, false)
            val estado = estadoDe(alimento.dias)
            itemView.findViewById<TextView>(R.id.tvNombre).text = alimento.nombre
            itemView.findViewById<TextView>(R.id.tvSubtitulo).text =
                "${alimento.categoria} · ${alimento.cantidad} unidad${if (alimento.cantidad != 1) "es" else ""}"
            bindBadge(itemView, estado)

            val actionsRow = itemView.findViewById<View>(R.id.actionsRow)
            actionsRow.visibility = View.VISIBLE

            itemView.findViewById<TextView>(R.id.btnEditar).setOnClickListener {
                (activity as? MainActivity)?.editarAlimento(alimento.id)
            }

            itemView.findViewById<TextView>(R.id.btnComido).setOnClickListener {
                DialogUtils.confirmar(
                    requireContext(),
                    titulo = "¿Marcar como comido?",
                    mensaje = "\"${alimento.nombre}\" se quitará de tu despensa.",
                    textoConfirmar = "Sí, ya lo comí"
                ) {
                    lifecycleScope.launch {
                        alimentoDao.eliminar(alimento)
                        Toast.makeText(requireContext(), "¡Consumiste ${alimento.nombre}!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            itemView.findViewById<TextView>(R.id.btnEliminar).setOnClickListener {
                DialogUtils.confirmar(
                    requireContext(),
                    titulo = "¿Eliminar alimento?",
                    mensaje = "Se eliminará \"${alimento.nombre}\" de tu despensa. Esta acción no se puede deshacer.",
                    textoConfirmar = "Sí, eliminar"
                ) {
                    lifecycleScope.launch {
                        alimentoDao.eliminar(alimento)
                        Toast.makeText(requireContext(), "Alimento eliminado.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            list.addView(itemView)
        }
        container.addView(list)
    }
}
