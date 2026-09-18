package com.foodsaver

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.foodsaver.data.AppSession

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                mostrarError(tvError, "Completa todos los campos.")
                return@setOnClickListener
            }

            val user = AppSession.authUsers.find { it.email == email && it.password == password }
            if (user == null) {
                mostrarError(tvError, "Correo o contraseña incorrectos.")
                return@setOnClickListener
            }

            AppSession.currentUser = user
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun mostrarError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = TextView.VISIBLE
    }
}
