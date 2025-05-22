# 👤 Pantalla de Usuario – Guía de Uso

## 🎯 Objetivo

La pantalla de **perfil de usuario** te permite:

- Ver tu nombre de usuario y correo electrónico actual.
- Consultar tu progreso en los distintos juegos.
- Cambiar nombre, email o contraseña.
- Cerrar sesión o eliminar tu cuenta.

---

## 🖼️ Elementos de la Interfaz

### 🔷 Cabecera

- **Título:** "Perfil"
- Barra de navegación superior con fondo oscuro.

### 👤 Información del Usuario

- Imagen de usuario (avatar circular).
- Nombre de usuario y correo electrónico.
- Botones superiores:
  - 🔴 **Cerrar sesión**
  - 🗑️ **Eliminar cuenta**
  - 🔙 **Volver**

### ✏️ Opciones de Edición

- Cambiar nombre
- Cambiar email (requiere contraseña actual)
- Cambiar contraseña (requiere contraseña actual)

Cada acción abre un diálogo para introducir los nuevos datos.

---

## 🧠 Tu Progreso

Se muestra una lista de tus puntuaciones acumuladas por minijuego.

| Juego              | Neuronas Acumuladas | Dificultad Actual |
|--------------------|---------------------|-------------------|
| Stroop Test        | 240                 | Difícil           |
| Sudoku             | 85                  | Medio             |
| Odd Emoji          | 30                  | Fácil             |

Cada entrada incluye:

- Icono del juego
- Puntos totales (neurons)
- Nivel de dificultad asociado (fácil, medio, difícil)

También se muestra una leyenda visual con los íconos de dificultad.

---

## 🔐 Seguridad y Confirmaciones

- Si el correo no está verificado, se muestra un botón para reenviar el correo de verificación.
- Confirmación modal al cerrar sesión.
- Confirmación al eliminar cuenta (con feedback de éxito o error).

---

## ⚙️ Funcionalidad Técnica

- Los datos se recuperan desde Supabase y se actualizan localmente en `UserSession`.
- Cambios de nombre/email/contraseña afectan tanto a Supabase como a la sesión actual.
- Las acciones están protegidas con validación de contraseña si es necesario.
- El progreso se carga desde `GameSessionController.getAllTotalScores()`.

---

## 📌 Notas Adicionales

- Algunos botones pueden estar deshabilitados para usuarios invitados.
- Se recomienda verificar el correo para mantener la cuenta segura.
- El diseño es responsivo y optimizado para dispositivos móviles.

---

¿Listo para ver tu rendimiento?  
📈 Revisa tus puntos y sigue mejorando.
