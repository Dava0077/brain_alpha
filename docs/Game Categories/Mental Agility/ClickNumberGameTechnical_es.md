# 🔢 Click Number — Documentación Técnica

## Descripción General

Click Number es un **minijuego de agilidad mental acelerado** donde los jugadores deben encontrar y pulsar un número objetivo seleccionado aleatoriamente dentro de una cuadrícula 4x4 de enteros. Cada acierto genera una nueva cuadrícula con un nuevo objetivo. El juego se desarrolla bajo una cuenta regresiva basada en el nivel de dificultad, desafiando la **concentración**, la **velocidad** y la **memoria a corto plazo** del jugador.

Este módulo está implementado utilizando **Jetpack Compose Multiplatform**, siguiendo una arquitectura **Modelo–Vista–Controlador (MVC)** para separar de forma clara el estado, la interfaz y la lógica de interacción.

---

## Resumen de Arquitectura

### 1. `clickNumberModel.kt`
Gestiona la generación de datos de la cuadrícula y la verificación de respuestas.

- `gridSize`: La cuadrícula está fijada en 4x4.
- `numbers`: Lista de 16 enteros aleatorios (1–99).
- `targetNumber`: Un número elegido aleatoriamente que el jugador debe pulsar.

#### Funciones Clave

| Función | Propósito |
|---------|----------|
| `startGame()` | Genera una nueva cuadrícula 4x4 y selecciona un número objetivo. |
| `checkNumber(selected)` | Devuelve `true` si el número pulsado coincide con el objetivo. |
| `getNumbers()` | Expone la cuadrícula generada a la vista. |

---

### 2. `clickNumberController.kt`
Actúa como gestor de lógica del juego y mantiene el estado de la interfaz.

- Administra:
    - Cuenta regresiva de tiempo
    - Puntaje
    - Dificultad
    - Instrucciones y diálogo de fin de juego
    - Navegación y reinicio de partidas

#### Responsabilidades Destacadas

- `startGame()` inicializa la partida y la cuadrícula.
- `handleButtonClick(number)` verifica si la pulsación es correcta, actualiza estado y regenera.
- `startTimer()` controla la cuenta regresiva y finaliza el juego cuando llega a cero.
- `resetGame()` limpia todos los estados reactivos.
- `restartGame()` reinicia y comienza una nueva sesión de inmediato.
- `setDifficulty(diff)` asigna el temporizador según la dificultad.
- `goHome()` y `goHomeLogic()` gestionan las transiciones de pantalla.
- `beginClickGame()` oculta las instrucciones e inicia la sesión.

---

### 3. `clickNumberScreen.kt`
Implementación principal de la interfaz de usuario utilizando Jetpack Compose.

- Muestra:
    - Barra con logo, perfil y ajustes
    - Puntaje y selector de dificultad
    - Texto de instrucción y retroalimentación
    - Temporizador y número objetivo dinámico
    - Cuadrícula 4x4 de botones con números
    - Diálogo final con resumen de puntaje

- Soporta reactividad en tiempo real mediante `mutableStateOf` y `LaunchedEffect`.

---

### 4. `InstructionsClickNumber.kt`
Pantalla de instrucciones mostrada antes de comenzar el juego.

- Estilizada con `AnnotatedString` para resaltar frases clave.
- Describe:
    - Objetivo del juego
    - Cómo afecta el nivel de dificultad al tiempo
    - Mecanismos de retroalimentación
- Finaliza con un botón grande para comenzar el juego.

---

## Flujo del Juego

```mermaid
graph TD
  A[El usuario abre la pantalla de Click Number] --> B[Pantalla de instrucciones o Juego]
  B --> C{¿El juego ha comenzado?}
  C -- No --> D[Seleccionar dificultad]
  D --> E[Iniciar juego]
  C -- Sí --> F[Inicializar cuadrícula 4x4 + objetivo]
  F --> G[Mostrar cuadrícula]
  G --> H[El usuario pulsa un número]
  H --> I{¿Es correcto?}
  I -- Sí --> J[+1 punto, regenerar cuadrícula y objetivo]
  I -- No --> K[Mostrar mensaje incorrecto]
  J & K --> G
  F --> L[Temporizador descendente en ejecución]
  L --> M{¿Tiempo = 0?}
  M -- Sí --> N[Mostrar diálogo de fin de partida + Puntaje]