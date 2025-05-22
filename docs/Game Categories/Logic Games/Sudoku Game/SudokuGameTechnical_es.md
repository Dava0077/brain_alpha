# Juego de Sudoku — Documentación Técnica

## Visión General

El juego de Sudoku es un **rompecabezas numérico basado en lógica** que desafía a los jugadores a completar una cuadrícula de 9x9 usando deducción y razonamiento. Cada fila, columna y subcuadro 3x3 debe contener todos los números del 1 al 9 sin repetir.

Este módulo está implementado utilizando **Jetpack Compose Multiplataforma**, con una arquitectura clara de **Modelo–Vista–Controlador (MVC)** que separa la lógica del juego, la interfaz y el manejo de eventos.

---

## Resumen de la Arquitectura

### 1. `sudokuModel.kt`
Gestiona todo el estado del tablero y la generación de puzzles.

- `grid`: Matriz 9x9 que representa el tablero actual.
- `initialCells`: Marca qué celdas son fijas (dadas desde el inicio).
- `difficulty`: Enum — `EASY`, `MEDIUM`, `HARD`.
- `started`: Bandera que indica si el juego ha comenzado.
- `clueCount`: Número de pistas iniciales, determinado por la dificultad.

#### Funciones Clave

| Función | Propósito |
|--------|-----------|
| `generateValidBoard()` | Genera un tablero válido y jugable. |
| `solveBoard()` | Resuelve el tablero actual usando backtracking. |
| `removeCells(count)` | Elimina celdas para ajustar la dificultad. |
| `setCell(row, col, value)` | Coloca un número si la celda no es fija. |
| `getGrid()` | Devuelve el tablero actual. |
| `isInitial(row, col)` | Verifica si una celda es editable. |
| `clearBoard()` | Reinicia el tablero por completo. |
| `fillDiagonalBlocks()` | Llena las diagonales con valores aleatorios. |
| `isSafe(row, col, num)` | Valida si un número puede colocarse en una celda. |

---

### 2. `sudokuController.kt`
Actúa como puente entre la vista y el modelo.

- Controla dificultad, visibilidad de instrucciones y navegación.
- Llama a la lógica del modelo para iniciar, validar y puntuar.
- Gestiona el estado del juego desde la UI.

#### Responsabilidades Notables

- `startGame()` inicia una partida y genera el tablero.
- `setCellValue(row, col, value)` coloca un valor en la celda.
- `isCellInitial(row, col)` verifica si la celda es fija.
- `isBoardComplete()` detecta si todas las celdas están llenas.
- `isBoardValid()` valida que la solución sea correcta.
- `calculateScore(seconds)` calcula la puntuación según el tiempo.
- `toggleInstructions(show)` muestra/oculta instrucciones.
- `goHomeLogic()` redirige al menú de juegos de lógica.

---

### 3. `sudokuScreen.kt`
Implementa la interfaz principal del juego.

- Renderiza:
    - Cuadrícula de 9x9 con celdas editables y fijas.
    - Cronómetro adaptado a la dificultad.
    - Teclado numérico (botones del 1 al 9).
    - Botones para instrucciones, puntuación y reinicio.
- Muestra mensajes de éxito o error.
- Diseño adaptable a móviles/tabletas.

### 4. `InstructionsSudoku.kt`
- Presenta las reglas del juego con formato visual.
- Utiliza `AnnotatedString` para resaltar contenido clave.
- Incluye botón de iniciar y barra de navegación.
- Estética coherente con la vista del juego.

---

## Flujo del Juego

```mermaid
graph TD
  A[Usuario abre Sudoku] --> B[Pantalla de instrucciones o juego]
  B --> C{¿Juego iniciado?}
  C -- Sí --> D[Generar tablero]
  C -- No --> E[Seleccionar dificultad]
  E --> F[Iniciar juego]
  D --> G[Jugador llena celdas]
  G --> H{¿Tablero completo?}
  H -- No --> G
  H -- Sí --> I[Validar solución]
  I --> J{¿Correcta?}
  J -- Sí --> K[Guardar y mostrar puntos]
  J -- No --> L[Mostrar error]
  D --> M{¿Tiempo agotado?}
  M --> N[Fin del juego]
