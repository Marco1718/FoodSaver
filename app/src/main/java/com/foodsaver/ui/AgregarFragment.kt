package com.foodsaver.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.foodsaver.MainActivity
import com.foodsaver.R
import com.foodsaver.data.Alimento
import com.foodsaver.data.AppSession

class AgregarFragment : Fragment(R.layout.fragment_agregar) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val spCategoria = view.findViewById<Spinner>(R.id.spCategoria)
        val etCantidad = view.findViewById<EditText>(R.id.etCantidad)
        val etDias = view.findViewById<EditText>(R.id.etDias)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        spCategoria.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, AppSession.CATEGORIAS
        )

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidad = etCantidad.text.toString()
            val dias = etDias.text.toString()

            if (nombre.isEmpty() || cantidad.isEmpty() || dias.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AppSession.alimentos.add(
                Alimento(
                    id = AppSession.nextFoodId++,
                    nombre = nombre,
                    categoria = spCategoria.selectedItem as String,
                    cantidad = cantidad.toIntOrNull() ?: 1,
                    dias = dias.toIntOrNull() ?: 0
                )
            )

            etNombre.setText("")
            etCantidad.setText("")
            etDias.setText("")
            spCategoria.setSelection(0)

            Toast.makeText(requireContext(), "✓ Alimento agregado.", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.actualizarHeader()
        }
    }
}
