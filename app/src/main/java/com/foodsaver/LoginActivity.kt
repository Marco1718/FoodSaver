package com.foodsaver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.foodsaver.data.AppSession
import com.foodsaver.util.Validators

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)

        listOf(etEmail, etPassword).forEach { et ->
            et.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                et.error = null
                tvError.visibility = TextView.GONE
            })
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // ── Validación de campos ──
            var valido = true
            if (!Validators.noEsVacio(email)) {
                etEmail.error = "Ingresa tu correo"; valido = false
            } else if (!Validators.esEmailValido(email)) {
                etEmail.error = "Correo con formato inválido"; valido = false
            }
            if (!Validators.noEsVacio(password)) {
                etPassword.error = "Ingresa tu contraseña"; valido = false
            }
            if (!valido) return@setOnClickListener

            val user = AppSession.authUsers.find { it.email == email && it.password == password }
            if (user == null) {
                mostrarError(tvError, "Correo o contraseña incorrectos.")
                return@setOnClickListener
            }

            AppSession.currentUser = user
            irAMainLimpiandoPila()
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun mostrarError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = TextView.VISIBLE
    }

    /**
     * Va a MainActivity limpiando TODA la pila (Login incluido). Sin esto,
     * si el usuario venía de Registro, LoginActivity queda viva debajo y
     * presionar "Atrás" desde Main regresaría al Login en vez de salir.
     */
    private fun irAMainLimpiandoPila() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
