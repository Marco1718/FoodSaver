package com.foodsaver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.foodsaver.data.AppSession
import com.foodsaver.data.AuthUser
import com.foodsaver.util.Validators

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmar = findViewById<EditText>(R.id.etConfirmar)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnBack = findViewById<TextView>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        listOf(etNombre, etEmail, etPassword, etConfirmar).forEach { et ->
            et.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                et.error = null
                tvError.visibility = TextView.GONE
            })
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmar = etConfirmar.text.toString()

            // ── Validación de cada campo ──
            var valido = true
            if (!Validators.esNombreValido(nombre)) {
                etNombre.error = "Escribe tu nombre completo (solo letras)"; valido = false
            }
            if (!Validators.noEsVacio(email)) {
                etEmail.error = "Ingresa tu correo"; valido = false
            } else if (!Validators.esEmailValido(email)) {
                etEmail.error = "Correo con formato inválido"; valido = false
            }
            if (!Validators.esPasswordValida(password)) {
                etPassword.error = "Mínimo 6 caracteres"; valido = false
            }
            if (confirmar != password) {
                etConfirmar.error = "Las contraseñas no coinciden"; valido = false
            }
            if (!valido) return@setOnClickListener

            if (AppSession.authUsers.any { it.email == email }) {
                mostrarError(tvError, "Ya existe una cuenta con ese correo.")
                return@setOnClickListener
            }

            val nuevo = AuthUser(nombre, email, password)
            AppSession.authUsers.add(nuevo)
            AppSession.currentUser = nuevo
            irAMainLimpiandoPila()
        }
    }

    private fun mostrarError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = TextView.VISIBLE
    }

    /** Ver LoginActivity.irAMainLimpiandoPila(): evita que Login quede vivo debajo en la pila. */
    private fun irAMainLimpiandoPila() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
