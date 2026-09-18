package com.foodsaver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foodsaver.data.AppSession
import com.foodsaver.data.AuthUser
import android.util.Patterns

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

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmar = etConfirmar.text.toString()

            when {
                nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmar.isEmpty() ->
                    error(tvError, "Completa todos los campos.")
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    error(tvError, "Ingresa un correo válido.")
                password.length < 6 ->
                    error(tvError, "La contraseña debe tener al menos 6 caracteres.")
                password != confirmar ->
                    error(tvError, "Las contraseñas no coinciden.")
                AppSession.authUsers.any { it.email == email } ->
                    error(tvError, "Ya existe una cuenta con ese correo.")
                else -> {
                    val nuevo = AuthUser(nombre, email, password)
                    AppSession.authUsers.add(nuevo)
                    AppSession.currentUser = nuevo
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun error(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = TextView.VISIBLE
    }
}
