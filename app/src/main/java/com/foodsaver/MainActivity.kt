package com.foodsaver

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.foodsaver.data.AppSession
import com.foodsaver.ui.AgregarFragment
import com.foodsaver.ui.DespensaFragment
import com.foodsaver.ui.InicioFragment
import com.foodsaver.ui.RecetasFragment
import com.foodsaver.ui.UrgentesFragment
import com.foodsaver.ui.UsuariosFragment
import com.foodsaver.util.DialogUtils

enum class Seccion(val titulo: String) {
    INICIO("FoodSaver"), AGREGAR("Agregar alimento"), DESPENSA("Mi despensa"),
    URGENTES("Urgentes"), RECETAS("Sugerencias"), USUARIOS("Usuarios")
}

class MainActivity : AppCompatActivity() {

    private lateinit var tvSectionTitle: TextView
    private lateinit var tvGreeting: TextView
    private lateinit var statsRow: View
    private lateinit var tvStatAlimentos: TextView
    private lateinit var tvStatUrgentes: TextView
    private lateinit var chipUrgentes: View

    private var seccionActual: Seccion = Seccion.INICIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (AppSession.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        tvSectionTitle = findViewById(R.id.tvSectionTitle)
        tvGreeting = findViewById(R.id.tvGreeting)
        statsRow = findViewById(R.id.statsRow)
        tvStatAlimentos = findViewById(R.id.tvStatAlimentos)
        tvStatUrgentes = findViewById(R.id.tvStatUrgentes)
        chipUrgentes = findViewById(R.id.chipUrgentes)

        findViewById<TextView>(R.id.btnLogout).setOnClickListener {
            DialogUtils.confirmar(
                this,
                titulo = "¿Cerrar sesión?",
                mensaje = "Tendrás que volver a iniciar sesión para entrar de nuevo.",
                textoConfirmar = "Sí, salir"
            ) {
                AppSession.cerrarSesion()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        setupNavItem(R.id.navInicio, "🏠", "Inicio", Seccion.INICIO)
        setupNavItem(R.id.navAgregar, "➕", "Agregar", Seccion.AGREGAR)
        setupNavItem(R.id.navDespensa, "🥕", "Despensa", Seccion.DESPENSA)
        setupNavItem(R.id.navUrgentes, "⚠️", "Urgentes", Seccion.URGENTES)
        setupNavItem(R.id.navRecetas, "🍳", "Recetas", Seccion.RECETAS)
        // Pestaña "Usuarios" oculta a petición (ver activity_main.xml).
        // El código de Room (UsuariosFragment, DAO, Database) sigue intacto,
        // solo no hay botón de nav que lo abra.

        mostrarSeccion(Seccion.INICIO)
    }

    override fun onResume() {
        super.onResume()
        actualizarHeader()
    }

    private fun setupNavItem(containerId: Int, icon: String, label: String, seccion: Seccion) {
        val container = findViewById<View>(containerId)
        container.findViewById<TextView>(R.id.navIcon).text = icon
        container.findViewById<TextView>(R.id.navLabel).text = label
        container.setOnClickListener { mostrarSeccion(seccion) }
    }

    private fun mostrarSeccion(seccion: Seccion, fragmentOverride: Fragment? = null) {
        seccionActual = seccion
        val fragment: Fragment = fragmentOverride ?: when (seccion) {
            Seccion.INICIO -> InicioFragment()
            Seccion.AGREGAR -> AgregarFragment()
            Seccion.DESPENSA -> DespensaFragment()
            Seccion.URGENTES -> UrgentesFragment()
            Seccion.RECETAS -> RecetasFragment()
            Seccion.USUARIOS -> UsuariosFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        actualizarHeader()
        actualizarNavActivo(seccion)
    }

    /** Abre el formulario de "Agregar alimento" ya en modo edición, precargado con ese alimento. */
    fun editarAlimento(alimentoId: Int) {
        mostrarSeccion(Seccion.AGREGAR, AgregarFragment.paraEditar(alimentoId))
    }

    /** Tras guardar una edición (Update), regresa a Despensa para ver el cambio reflejado. */
    fun volverADespensaSiEditando(idEnEdicion: Int?) {
        if (idEnEdicion != null) mostrarSeccion(Seccion.DESPENSA)
    }

    /** Título/saludo del header. Los NÚMEROS (alimentos/urgentes) los actualiza
     *  InicioFragment vía actualizarStatsInicio() cuando observa Room, ya que
     *  ahora viven en la base de datos (consulta asíncrona), no en memoria. */
    fun actualizarHeader() {
        tvSectionTitle.text = seccionActual.titulo
        if (seccionActual == Seccion.INICIO) {
            tvGreeting.visibility = View.VISIBLE
            tvGreeting.text = "Hola, ${AppSession.currentUser?.nombre?.split(" ")?.firstOrNull() ?: ""} 👋"
            statsRow.visibility = View.VISIBLE
        } else {
            tvGreeting.visibility = View.GONE
            statsRow.visibility = View.GONE
        }
    }

    /** Llamado por InicioFragment cada vez que Room emite la lista de alimentos del usuario actual. */
    fun actualizarStatsInicio(total: Int, urgentes: Int) {
        tvStatAlimentos.text = total.toString()
        tvStatUrgentes.text = urgentes.toString()
        chipUrgentes.setBackgroundResource(
            if (urgentes > 0) R.drawable.bg_stat_chip_alert else R.drawable.bg_stat_chip
        )
    }

    private fun actualizarNavActivo(seccion: Seccion) {
        val map = mapOf(
            Seccion.INICIO to R.id.navInicio,
            Seccion.AGREGAR to R.id.navAgregar,
            Seccion.DESPENSA to R.id.navDespensa,
            Seccion.URGENTES to R.id.navUrgentes,
            Seccion.RECETAS to R.id.navRecetas
            // Seccion.USUARIOS ya no tiene botón de nav (pestaña oculta).
        )
        map.forEach { (sec, id) ->
            val container = findViewById<View>(id)
            val active = sec == seccion
            container.findViewById<View>(R.id.navIndicator).visibility = if (active) View.VISIBLE else View.INVISIBLE
            val label = container.findViewById<TextView>(R.id.navLabel)
            label.setTextColor(resources.getColor(if (active) R.color.green_primary else R.color.text_muted, theme))
        }
    }
}
