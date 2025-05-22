# 🏠 Pantalla de Inicio – Manual de Usuario

## 🎯 Propósito

La pantalla de **Inicio** es el primer punto de entrada para los usuarios después de iniciar sesión exitosamente. Aquí puedes:

- Navegar por las diferentes categorías de juegos cognitivos.
- Acceder a tu perfil.
- Ver la tabla de clasificación (Leaderboard).
- Ir a los ajustes de la cuenta.
- Cerrar sesión de manera segura.

---

## 🖼️ Vista General de la Interfaz

### 🧑‍🎓 Barra Superior

- 📷 **Avatar del usuario** (acceso al perfil)
- ⚙️ **Botón de ajustes**
- 🚪 **Botón de cerrar sesión**
- 👋 **Saludo personalizado** con el nombre del usuario

### 🏆 Botón de Tabla de Clasificación

- Acceso directo a la **Leaderboard global**
- Botón ubicado debajo de la barra superior

### 🧠 Categorías Cognitivas

Accesibles desde tarjetas interactivas, divididas en dos filas:

| Categoría         | Ícono                    | Color de Borde |
|-------------------|--------------------------|----------------|
| 🧠 Memoria         | `iconoMem`               | Azul cian      |
| 🧩 Lógica          | `iconoLog`               | Azul cian      |
| 🎯 Concentración   | `icono_concentracion`    | Azul cian      |
| ⚡ Agilidad Mental | `iconoAgi`               | Azul cian      |

Cada tarjeta contiene:

- Imagen temática
- Título de la categoría
- Acción al hacer clic: redirige al menú de juegos correspondiente

### 🖼️ Fondo Visual

- Imagen de fondo (`Login2`)
- Logo flotante centrado arriba (`logo`)
- Línea decorativa en la parte inferior

---

## 🔒 Cierre de Sesión Seguro

Cuando el usuario hace clic en el ícono de logout:

1. Se muestra un **diálogo de confirmación**.
2. Si el usuario acepta:
   - Se ejecuta `AuthController.signOut()`
   - Se borra la sesión (`LoginController.clear()`)
   - Se redirige al login
   - Se muestra un snackbar con un mensaje de éxito

---

## ⚙️ Funcionalidades Técnicas

- **Estado persistente de sesión**: recupera usuario desde Supabase al abrir la pantalla.
- **Scaffold con Snackbar**: muestra mensajes de éxito o error.
- **Scroll vertical habilitado**: la UI es navegable en pantallas pequeñas.
- **Responsive Layout**: elementos distribuidos correctamente sin importar el tamaño del dispositivo.

---

## 🔁 Navegación Disponibles

| Acción                             | Destino              |
|------------------------------------|-----------------------|
| Hacer clic en avatar               | Pantalla de perfil    |
| Clic en ajustes                    | Pantalla de ajustes   |
| Botón "Leaderboard"               | Tabla de clasificación global |
| Tarjetas de categorías             | Menús de juegos por categoría |

---

📌 **Consejo:** Usa esta pantalla como tu centro de control para mejorar tus habilidades cognitivas a través de juegos organizados por categoría.

