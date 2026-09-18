# FoodSaver

App de Android (Kotlin) para el control de alimentos en casa: registra lo que
tienes, avisa qué está por caducar, sugiere recetas y lleva un registro de
usuarios en una base de datos local con **Room**.

Proyecto Gradle único (no está dividido en actividades/ramas): se abre
directo en Android Studio desde la raíz de esta carpeta.

## Requisitos que cumple (Room)

| Requisito | Archivo |
|---|---|
| **Entidad (Model)** `User` — `id` autoincrementable, `nombre`, `apellidos`, `direccion`, `telefono` | `app/src/main/java/com/foodsaver/data/User.kt` |
| **DAO** — insertar y consultas | `app/src/main/java/com/foodsaver/data/UserDao.kt` |
| **Database Class** — `RoomDatabase` + patrón Singleton | `app/src/main/java/com/foodsaver/data/AppDatabase.kt` |
| **UI** — formulario con `TextInputLayout` / `TextInputEditText` + botón guardar | `app/src/main/res/layout/fragment_usuarios.xml` |
| **Controlador** — valida campos vacíos + inserta en segundo plano con `lifecycleScope` | `app/src/main/java/com/foodsaver/ui/UsuariosFragment.kt` |

> Nota: la pestaña "Usuarios" está oculta del menú inferior por ahora, pero
> el código de arriba sigue completo y funcional dentro del proyecto.

## Cómo abrir el proyecto

En Android Studio: **File → Open** y selecciona la carpeta `FoodSaver` (la
raíz de este repositorio, no una subcarpeta).

```
cd FoodSaver
./gradlew assembleDebug
```

## Estructura del proyecto

| Carpeta | Contenido |
|---|---|
| `app/src/main/java/com/foodsaver/data/` | Entidad `User`, `UserDao`, `AppDatabase` (Room) + modelos en memoria (`Alimento`, `AppSession`) |
| `app/src/main/java/com/foodsaver/ui/` | Fragments de cada sección (Inicio, Agregar, Despensa, Urgentes, Recetas, Usuarios) |
| `app/src/main/java/com/foodsaver/` | `LoginActivity`, `RegisterActivity`, `MainActivity` (nav inferior + header dinámico) |
| `app/src/main/res/layout/` | Layouts XML de cada pantalla |
| `app/src/main/res/drawable/` | Fondos redondeados, botones, badges, degradados |
| `app/src/main/res/values/` | Colores, estilos y tema |

