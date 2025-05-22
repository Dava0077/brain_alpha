# 🧠 Mental Math — Documentación Técnica

## Descripción General

**Mental Math** es un juego de **cálculo aritmético mental** donde el jugador debe resolver 10 operaciones básicas (suma, resta y multiplicación) lo más rápido posible dentro de un límite de tiempo determinado por la dificultad. Se premia tanto la **precisión** como la **rapidez**.

El juego está desarrollado con **Jetpack Compose Multiplatform**, estructurado según el patrón **Modelo–Vista–Controlador (MVC)** para una separación clara de responsabilidades, reactividad y fácil mantenibilidad.

---

## Resumen de Arquitectura

### 1. `mentalMathModel.kt`
Encapsula la lógica del juego, la puntuación y la generación de preguntas.

- `score`: Puntaje total actual del jugador.
- `currentQuestion`: Índice de la pregunta actual.
- `totalQuestions`: Total de preguntas por sesión (10).
- `difficulty`: Nivel actual (`EASY`, `MEDIUM`, `HARD`).
- `currentQuestionObj`: Objeto `Question` activo.

#### Funciones Clave

| Función | Propósito |
|--------|-----------|
| `generateQuestion()` | Crea una pregunta aleatoria con suma, resta o multiplicación. |
| `checkAnswer(answer, time)` | Verifica la respuesta, actualiza puntaje y avanza a la siguiente pregunta. |
| `calculatePoints(time)` | Calcula puntos en función del tiempo y la dificultad. |
| `resetGame()` | Reinicia el estado interno del juego. |
| `getTimeLimit()` | Devuelve el tiempo total de juego según dificultad. |
| `isGameOver()` | Devuelve `true` si se respondieron las 10 preguntas. |

---

### 2. `mentalMathController.kt`
Actúa como puente entre la vista y el modelo.

- Controla:
  - Generación de preguntas.
  - Envío y evaluación de respuestas.
  - Control de dificultad.
  - Puntuación y navegación.

#### Responsabilidades Destacadas

| Función | Propósito |
|--------|-----------|
| `startGame(callback)` | Genera una nueva pregunta y llama al callback. |
| `submitAnswer(answer, time, callback)` | Evalúa la respuesta, calcula puntaje y devuelve feedback. |
| `resetGame()` | Reinicia la sesión actual. |
| `setDifficulty(level)` | Establece el nivel de dificultad. |
| `goHome()` | Navega al menú principal. |
| `isGameOver()` | Verifica si ya terminó la partida. |
| `getScore()` | Devuelve el puntaje actual. |
| `getTotalQuestions()` | Total de preguntas del juego (10). |
| `getCurrentQuestionNumber()` | Número de pregunta actual (basado en índice +1). |

---

### 3. `mentalMathView.kt`
Pantalla principal del juego.

- Muestra:
  - Barra superior con navegación.
  - Puntaje acumulado y dificultad seleccionada.
  - Temporizador en cuenta regresiva.
  - Preguntas dinámicas con campo de entrada.
  - Retroalimentación tras cada respuesta.
  - Botones de reinicio e instrucciones.

- También incluye:
  - Selección de dificultad con niveles desbloqueables por neuronas.
  - Guardado automático de sesión en Supabase al finalizar.

---

### 4. `MentalInstructionsScreen.kt`
Pantalla de instrucciones inicial.

- Explica:
  - Objetivo del juego.
  - Cómo se calcula el puntaje según velocidad y dificultad.
  - Cómo se desbloquean niveles.
- Estilo visual claro con texto enriquecido (`AnnotatedString`).
- Botón de "Volver" para regresar al juego.

---

## Flujo del Juego

```mermaid
graph TD
    A[Usuario abre pantalla Mental Math] --> B[Ver instrucciones o comenzar juego]
    B --> C[El juego ha comenzado]
    C -->|No| D[Seleccionar nivel de dificultad]
    D --> E[Presionar boton Iniciar]
    C -->|Si| F[Generar una pregunta]
    E --> F
    F --> G[Mostrar la pregunta]
    G --> H[Jugador ingresa respuesta]
    H --> I[Validar y calcular puntaje]
    I --> J[Juego terminado]
    J -->|No| F
    J -->|Si| K[Mostrar resultado final y guardar]