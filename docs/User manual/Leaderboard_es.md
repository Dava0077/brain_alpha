# 🏆 Pantalla de Leaderboard – Guía de Usuario

## 🎯 Propósito

La pantalla de **Leaderboard** te permite consultar las **mejores puntuaciones globales** de cada juego disponible en Brain Alpha.

Puedes explorar rankings por:

- 🧠 Juego específico
- 📊 Dificultad (Fácil, Medio, Difícil)
- 🕒 Tiempo promedio o puntuación
- 🔼 Orden ascendente o descendente

---

## 📊 ¿Qué muestra el leaderboard?

Cada entrada incluye:

- 👤 Usuario
- 🧠 Puntuación obtenida
- ⏱ Tiempo promedio (o dificultad)
- 📅 Fecha de la partida

Los datos se agrupan por tipo de juego:  
Ej. **Memoria**, **Lógica**, **Concentración**, **Agilidad mental**.

---

## 📂 Categorías y Juegos

Los juegos se agrupan en las siguientes categorías:

| Categoría         | Juegos incluidos                          |
|-------------------|-------------------------------------------|
| 🧠 Memoria         | Pairs Game, Simon Game                    |
| 🔍 Lógica          | Sudoku, Word Search                       |
| 🎯 Concentración   | Stroop Test, Odd Emoji                    |
| ⚡ Agilidad Mental | Mental Math, Click Number                 |

---

## 🔽 Filtros y Opciones

Puedes personalizar cómo se muestran los resultados:

- **Ordenar por**:
  - 🧠 Puntuación (`score`)
  - ⏱ Tiempo promedio (`avg_time`)
- **Dirección**:
  - 🔼 Ascendente
  - 🔽 Descendente

Se puede expandir cada categoría y juego individual para ver detalles.

---

## 🎮 Tabla de Puntuaciones

Una vez expandido un juego, verás:

- 🟢 Fácil
- 🔵 Medio
- 🔴 Difícil

O una versión **combinada** por usuario (sumando puntuaciones de todas las dificultades).

Cada fila de la tabla contiene:

| Posición | Usuario | Puntos | Tiempo o Dificultad | Fecha |
|----------|---------|--------|----------------------|--------|

---

## 📦 Origen de Datos

Los datos se recuperan desde Supabase con filtros por:

- 🆔 Juego
- 🎚️ Dificultad
- 🧠 Orden (score o tiempo promedio)
- 🔼 Dirección (asc o desc)

Cada bloque expandible ejecuta una consulta y almacena resultados en memoria para evitar recargas innecesarias.

---

## 🧭 Navegación

Desde esta pantalla puedes:

- 🔙 Volver al menú principal (botón “Back”)
- 👁️ Visualizar rankings específicos por juego y dificultad
- 🔄 Cambiar criterios de orden y dirección con un clic

```kotlin
fun goHome() {
    navigateTo("home")
}
