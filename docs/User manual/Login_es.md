# 🔐 Pantalla de Inicio de Sesión - Guía del Usuario

## 🎯 Objetivo

La **Pantalla de Inicio de Sesión** es el primer paso para acceder a la experiencia completa de la aplicación. Permite a los usuarios registrarse, iniciar sesión o continuar como invitados.  
Esencial para guardar tu progreso, acumular puntuaciones (_neuronas_) y desbloquear niveles.

---

## 🕹️ Cómo Funciona

1. Al abrir la app, se muestra una pantalla de bienvenida con opciones:
   - Iniciar sesión con Google
   - Crear una cuenta con correo y contraseña
   - Iniciar sesión si ya tienes cuenta
   - Continuar como invitado (modo sin registro)

2. Cuando eliges iniciar sesión:
   - Se abre un formulario para ingresar tus credenciales.
   - Si son correctas, accedes al perfil y datos almacenados.
   - Si usas Google, se abre el flujo de autenticación de Google.

3. Si decides continuar como invitado:
   - No se guardarán tus partidas ni tu progreso.
   - Aún podrás jugar, pero con funciones limitadas.

---

## 🧾 Opciones Disponibles

| Botón                  | Función                                  |
|------------------------|------------------------------------------|
| ✨ Continuar como invitado | Entra al menú principal sin registro      |
| 🧾 Iniciar sesión         | Abre formulario para usuarios registrados |
| 🆕 Crear cuenta           | Abre formulario de registro              |
| 🔐 Iniciar con Google     | Usa tu cuenta de Google para ingresar    |
| ⚙️ Ajustes                | Cambia idioma, cierra sesión, etc.       |

---

## 👥 Tipos de Usuario

- 🧑‍💼 **Registrado:** Acceso a puntuaciones, progresos y dificultad avanzada.
- 🧪 **Invitado:** Juega libremente, pero no guarda estadísticas.

---

## 🎨 Interfaz

- 🖼️ **Encabezado:** Logo y acceso a ajustes
- 📋 **Botones centrales:** Acciones de sesión
- 🧾 **Mensajes:** Notificaciones de éxito o error

---

## 🔐 Seguridad

- Las contraseñas se manejan de forma segura mediante Supabase.
- Se utiliza OAuth 2.0 en el login con Google.
- La validación incluye control de errores: campos vacíos, email inválido, etc.

---

## 💡 Consejos

- Regístrate para acumular neuronas y desbloquear niveles.
- Usa Google si quieres una experiencia rápida y sin recordar contraseñas.
- Puedes cambiar de cuenta desde el menú de ajustes.

---

## 🏁 ¿Qué sigue?

Una vez dentro, se te redirige al **menú principal**, donde puedes:
- Elegir juegos de lógica, memoria, atención...
- Ver tu progreso y estadísticas
- Cambiar configuraciones del perfil

---

🔁 ¿Listo para comenzar?  
**Elige una opción de inicio de sesión y explora tu potencial.**
