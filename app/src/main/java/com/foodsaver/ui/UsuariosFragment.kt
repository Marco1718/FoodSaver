package com.foodsaver.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodsaver.MainActivity
import com.foodsaver.R
import com.foodsaver.data.AppDatabase
import com.foodsaver.data.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

/**
 * Controlador (Activity/Fragment) de la sección "Usuarios".
 *
 * - Valida que ningún campo del formulario esté vacío.
 * - Ejecuta la inserción en la base de datos Room en segundo plano,
 *   mediante corrutinas lanzadas con lifecycleScope.
 * - Observa el Flow del DAO para refrescar automáticamente la lista
 *   (RecyclerView) cada vez que cambian los datos.
 */
class UsuariosFragment : Fragment(R.layout.fragment_usuarios) {

    // Obtenemos el DAO a partir de la instancia Singleton de la base de datos.
    private val userDao by lazy {
        AppDatabase.getDatabase(requireContext().applicationContext).userDao()
    }

    private val adapter = UsuariosAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilNombre = view.findViewById<TextInputLayout>(R.id.tilNombre)
        val tilApellidos = view.findViewById<TextInputLayout>(R.id.tilApellidos)
        val tilDireccion = view.findViewById<TextInputLayout>(R.id.tilDireccion)
        val tilTelefono = view.findViewById<TextInputLayout>(R.id.tilTelefono)

        val etNombre = view.findViewById<TextInputEditText>(R.id.etNombre)
        val etApellidos = view.findViewById<TextInputEditText>(R.id.etApellidos)
        val etDireccion = view.findViewById<TextInputEditText>(R.id.etDireccion)
        val etTelefono = view.findViewById<TextInputEditText>(R.id.etTelefono)

        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val successBanner = view.findViewById<View>(R.id.successBanner)
        val tvContador = view.findViewById<TextView>(R.id.tvContador)
        val rvUsuarios = view.findViewById<RecyclerView>(R.id.rvUsuarios)

        rvUsuarios.layoutManager = LinearLayoutManager(requireContext())
        rvUsuarios.adapter = adapter

        // Quita el error de un campo en cuanto el usuario empieza a escribir en él.
        listOf(etNombre to tilNombre, etApellidos to tilApellidos,
            etDireccion to tilDireccion, etTelefono to tilTelefono).forEach { (et, til) ->
            et.doOnTextChanged { _, _, _, _ -> til.error = null }
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            val apellidos = etApellidos.text?.toString()?.trim().orEmpty()
            val direccion = etDireccion.text?.toString()?.trim().orEmpty()
            val telefono = etTelefono.text?.toString()?.trim().orEmpty()

            // ── Validación: ningún campo puede estar vacío ──
            var valido = true
            if (nombre.isEmpty()) { tilNombre.error = "Este campo es obligatorio"; valido = false }
            if (apellidos.isEmpty()) { tilApellidos.error = "Este campo es obligatorio"; valido = false }
            if (direccion.isEmpty()) { tilDireccion.error = "Este campo es obligatorio"; valido = false }
            if (telefono.isEmpty()) { tilTelefono.error = "Este campo es obligatorio"; valido = false }

            if (!valido) {
                android.widget.Toast.makeText(requireContext(), "⚠️ Ningún campo puede estar vacío.", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = User(
                nombre = nombre,
                apellidos = apellidos,
                direccion = direccion,
                telefono = telefono
            )

            // ── Inserción en segundo plano con corrutinas (lifecycleScope) ──
            lifecycleScope.launch {
                userDao.insertar(nuevoUsuario)

                // De vuelta en el hilo principal (lifecycleScope.launch por
                // defecto reanuda en Dispatchers.Main) actualizamos la UI.
                etNombre.setText("")
                etApellidos.setText("")
                etDireccion.setText("")
                etTelefono.setText("")

                successBanner.visibility = View.VISIBLE
                successBanner.postDelayed({
                    if (isAdded) successBanner.visibility = View.GONE
                }, 3000)

                (activity as? MainActivity)?.let { main ->
                    val total = userDao.contar()
                    main.refrescarContadorUsuarios(total)
                }
            }
        }

        // Observamos el Flow del DAO: cada INSERT dispara automáticamente
        // una nueva emisión y el RecyclerView + el contador se refrescan solos.
        viewLifecycleOwner.lifecycleScope.launch {
            userDao.obtenerTodos().collect { usuarios ->
                adapter.submitList(usuarios)
                if (usuarios.isEmpty()) {
                    tvContador.visibility = View.GONE
                } else {
                    tvContador.visibility = View.VISIBLE
                    tvContador.text = "${usuarios.size} usuario${if (usuarios.size != 1) "s" else ""} registrado${if (usuarios.size != 1) "s" else ""}"
                }
                (activity as? MainActivity)?.refrescarContadorUsuarios(usuarios.size)
            }
        }
    }
}
