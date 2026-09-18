#!/bin/sh
# Wrapper estandar de Gradle.
# NOTA: este proyecto no incluye gradle/wrapper/gradle-wrapper.jar (binario)
# porque se generó sin conexión a internet. Simplemente abre la carpeta del
# proyecto en Android Studio: al hacer "Sync", Android Studio detecta el
# wrapper incompleto y lo regenera automáticamente con su propio Gradle
# integrado. Ver README.md -> "Primer paso al abrir el proyecto".
DIR="$(cd "$(dirname "$0")" && pwd)"
exec java -cp "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
