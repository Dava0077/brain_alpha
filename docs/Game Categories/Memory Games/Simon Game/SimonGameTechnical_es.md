# 🧠 Juego de Simón Dice — Documentación Técnica

## Visión General

El **Juego de Simón Dice** es un desafío clásico de **memoria secuencial** en el que el jugador debe repetir una serie de colores en el orden exacto en que se mostraron. Con cada ronda, se agrega un nuevo color, lo que pone a prueba la **memoria de trabajo**, la **atención** y la **retención de patrones** del jugador.

Este módulo está implementado con **Jetpack Compose Multiplatform**, con separación clara entre las capas **Modelo**, **Controlador** y **Vista**.

---

## Resumen de Arquitectura

### 1. `simonModel.kt`
Contiene toda la **lógica central** y el **estado del juego**.

- `difficulty`: Dificultad actual.
- `availableColors`: Lista de 4 colores disponibles.
- `machineSequence`: Secuencia generada por el sistema.
- `playerSequence`: Secuencia ingresada por el jugador.
- `state`: Enum `GameState` — `START`, `MACHINE`, `PLAYER`.
- `score`: Puntuación actual del jugador.

#### Funciones Clave

| Función | Propósito |
|--------|-----------|
| `startGame()` | Inicializa el juego y comienza la primera secuencia. |
| `addRandomColor()` | Agrega un nuevo color aleatorio a la secuencia. |
| `addPlayerColor(color)` | Registra el color presionado por el jugador. |
| `validatePlayerInput()` | Verifica si el último color ingresado es correcto. |
| `isPlayerSequenceComplete()` | Comprueba si la secuencia ha sido completada. |
| `getFinalScore()` | Devuelve el puntaje final multiplicado por la dificultad. |
| `getDelayBetweenColors()` | Define el tiempo entre colores según la dificultad. |
| `getAnimationSpeed()` | Define la velocidad de animación de colores. |
| `resetGame()` | Reinicia completamente el juego. |

---

### 2. `simonController.kt`
Sirve de intermediario entre `simonModel.kt` y la interfaz.

#### Responsabilidades

- Expone `uiState`: mensaje actual, turno, puntaje y secuencia.
- Inicia el juego (`onStartPressed()`).
- Procesa entradas del jugador (`onColorPressed()`).
- Coordina la lógica del modelo: validación, avance de ronda, puntuación.
- Maneja la selección de dificultad y visualización de instrucciones.
- Gestiona el estado de "game over" para mostrar el resultado.

> El controlador mantiene la lógica desacoplada y permite que la vista reaccione únicamente a estados observables.

---

### 3. `simonScreen.kt`
Pantalla principal del juego, construida con Compose.

- Renderiza:
  - Barra superior (perfil, puntuación, ajustes)
  - Selector de dificultad (si el juego no ha empezado)
  - Caja central que muestra el color activo
  - 4 botones de colores (Rojo, Verde, Azul, Amarillo)
  - Mensajes del estado del juego y puntuación
  - Botón para jugar o reiniciar

- Reactivo al estado de juego (`isUserTurn`, `gameOver`, `sequence`).

### 4. `InstructionsSimon.kt`
- Muestra la guía del juego con estilo visual personalizado.
- Utiliza `AnnotatedString` para resaltar texto.
- Incluye un único botón para comenzar la partida.

---

## Flujo del Juego

```mermaid
graph TD
  A[Usuario abre la pantalla] --> B[Pantalla de instrucciones o dificultad]
  B --> C[Presiona Iniciar]
  C --> D[El sistema muestra la secuencia]
  D --> E[Jugador repite la secuencia]
  E --> F{¿Coincide?}
  F -- Sí --> G[Sumar punto y nueva ronda]
  F -- No --> H[Fin del juego]
  G --> D
