# FoodSaver

App de Android (Kotlin) para el control de alimentos en casa: registra lo que
tienes, avisa qué está por caducar, sugiere recetas y lleva un registro de
usuarios en una base de datos local con **Room**.

Proyecto Gradle único (no está dividido en actividades/ramas): se abre
directo en Android Studio desde la raíz de esta carpeta.

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

> Nota: existe una pestaña "Usuarios" en la cual se pueden registrar más usuarios
> ésta está oculta del menú inferior por ahora, pero el código sigue completo y funcional dentro del proyecto.

