package com.foodsaver.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.foodsaver.MainActivity
import com.foodsaver.R
import com.foodsaver.data.Alimento
import com.foodsaver.data.AppDatabase
import com.foodsaver.data.AppSession
import com.foodsaver.data.LimitesAlimento
import com.foodsaver.util.Validators
import kotlinx.coroutines.launch

/**
 * Formulario de alimento: sirve tanto para CREAR (Fragment sin argumentos)
 * como para EDITAR (Fragment con "alimentoId" en arguments). Ambas rutas
 * guardan en Room, asociadas al correo de la cuenta con sesión iniciada
 * (AppSession.currentUser), y respetan los límites de LimitesAlimento
 * (cantidad máxima y días máximos según la categoría elegida).
 */
class AgregarFragment : Fragment(R.layout.fragment_agregar) {

    companion object {
        private const val ARG_ALIMENTO_ID = "alimentoId"

        fun paraEditar(alimentoId: Int): AgregarFragment = AgregarFragment().apply {
            arguments = bundleOf(ARG_ALIMENTO_ID to alimentoId)
        }
    }

    private val alimentoDao by lazy {
        AppDatabase.getDatabase(requireContext().applicationContext).alimentoDao()
    }

    private var alimentoIdEnEdicion: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ownerEmail = AppSession.currentUser?.email
        if (ownerEmail == null) {
            Toast.makeText(requireContext(), "Sesión no válida.", Toast.LENGTH_SHORT).show()
            return
        }

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val spCategoria = view.findViewById<Spinner>(R.id.spCategoria)
        val etCantidad = view.findViewById<EditText>(R.id.etCantidad)
        val etDias = view.findViewById<EditText>(R.id.etDias)
        val tvLimiteCantidad = view.findViewById<TextView>(R.id.tvLimiteCantidad)
        val tvLimiteDias = view.findViewById<TextView>(R.id.tvLimiteDias)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        spCategoria.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, AppSession.CATEGORIAS
        )

        tvLimiteCantidad.text = "Máx. ${LimitesAlimento.CANTIDAD_MAXIMA} unidades"

        fun actualizarHintDias() {
            val categoria = spCategoria.selectedItem as? String ?: return
            tvLimiteDias.text = "Máx. ${LimitesAlimento.diasMaximoPara(categoria)} días para $categoria"
        }
        spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) = actualizarHintDias()
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        actualizarHintDias()

        listOf(etNombre, etCantidad, etDias).forEach { et ->
            et.addTextChangedListener(onTextChanged = { _, _, _, _ -> et.error = null })
        }

        val id = arguments?.getInt(ARG_ALIMENTO_ID, -1)?.takeIf { it != -1 }
        alimentoIdEnEdicion = id

        if (id != null) {
            lifecycleScope.launch {
                val alimento = alimentoDao.obtenerPorId(id)
                if (alimento != null) {
                    etNombre.setText(alimento.nombre)
                    etCantidad.setText(alimento.cantidad.toString())
                    etDias.setText(alimento.dias.toString())
                    val posicion = AppSession.CATEGORIAS.indexOf(alimento.categoria)
                    if (posicion >= 0) spCategoria.setSelection(posicion)
                    actualizarHintDias()
                    btnGuardar.text = "Actualizar alimento"
                }
            }
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidadTexto = etCantidad.text.toString().trim()
            val diasTexto = etDias.text.toString().trim()
            val categoria = spCategoria.selectedItem as String

            // ── Validación: formato + límites (cantidad y días según categoría) ──
            var valido = true
            if (!Validators.esNombreValido(nombre)) {
                etNombre.error = "Escribe un nombre válido (solo letras)"
                valido = false
            }
            if (!Validators.esEnteroPositivo(cantidadTexto)) {
                etCantidad.error = "Debe ser un número entero mayor a 0"
                valido = false
            } else if (!Validators.esCantidadValida(cantidadTexto)) {
                etCantidad.error = "Máximo ${LimitesAlimento.CANTIDAD_MAXIMA} unidades"
                valido = false
            }
            if (!Validators.esEnteroNoNegativo(diasTexto)) {
                etDias.error = "Debe ser un número entero (0 o más)"
                valido = false
            } else if (!Validators.esDiasValidoParaCategoria(diasTexto, categoria)) {
                etDias.error = "Máximo ${LimitesAlimento.diasMaximoPara(categoria)} días para $categoria"
                valido = false
            }
            if (!valido) return@setOnClickListener

            val cantidad = cantidadTexto.toInt()
            val dias = diasTexto.toInt()
            val idActual = alimentoIdEnEdicion

            // ── Inserción/actualización en segundo plano con corrutinas ──
            lifecycleScope.launch {
                if (idActual == null) {
                    alimentoDao.insertar(
                        Alimento(ownerEmail = ownerEmail, nombre = nombre, categoria = categoria, cantidad = cantidad, dias = dias)
                    )
                    Toast.makeText(requireContext(), "✓ Alimento agregado.", Toast.LENGTH_SHORT).show()
                    etNombre.setText("")
                    etCantidad.setText("")
                    etDias.setText("")
                    spCategoria.setSelection(0)
                } else {
                    alimentoDao.actualizar(
                        Alimento(id = idActual, ownerEmail = ownerEmail, nombre = nombre, categoria = categoria, cantidad = cantidad, dias = dias)
                    )
                    Toast.makeText(requireContext(), "✓ Alimento actualizado.", Toast.LENGTH_SHORT).show()
                }
                (activity as? MainActivity)?.volverADespensaSiEditando(idActual)
            }
        }
    }
}
