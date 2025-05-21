# ⚙️ Pantalla de Ajustes – Guía de Usuario

## 🎯 Propósito

La **pantalla de Ajustes** permite a los usuarios personalizar su experiencia dentro de la app y acceder a opciones relacionadas con su cuenta.

Desde aquí puedes:

- 🔁 Cambiar el **idioma** de la interfaz
- 👤 Acceder a tu **perfil** para modificar nombre, correo o contraseña
- 🔙 Volver a la **pantalla principal**

---

## 🧩 Vista General de la Interfaz

- 🧠 **Barra Superior**: Muestra el título “Ajustes” centrado sobre un fondo oscuro.
- 🏠 **Botón Volver**: Te permite regresar al menú principal.
- 🌐 **Selector de Idioma**: Alterna entre **Español** y **English**.
- 👤 **Sección de Cuenta y Perfil**:
  - Cambiar nombre de usuario
  - Cambiar correo electrónico
  - Cambiar contraseña

Todas estas acciones redirigen a la **pantalla de Perfil**.

---

## 🌍 Selección de Idioma

Para cambiar el idioma:

1. Pulsa sobre el idioma actual (“Español” o “English”).
2. Selecciona el idioma deseado desde el menú desplegable.
3. La interfaz se actualizará inmediatamente al nuevo idioma.

---

## 👤 Acceso a Cuenta y Perfil

Cada una de las siguientes opciones:

- 🔤 Cambiar Nombre de Usuario
- 📧 Cambiar Correo Electrónico
- 🔐 Cambiar Contraseña

...redirige a la misma **pantalla de Perfil**, donde se realizan las modificaciones de forma segura.

---

## 🔙 Navegación

Utiliza el botón **Volver** para regresar a la pantalla principal en cualquier momento:

```kotlin
fun goHome() {
    navigateTo("home")
}
