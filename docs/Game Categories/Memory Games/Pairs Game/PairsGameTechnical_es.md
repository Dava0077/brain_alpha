# 🧠 Juego de Parejas — Documentación Técnica

## Visión General

El **Juego de Parejas** es un minijuego de **entrenamiento cognitivo visual** en el que los jugadores deben descubrir y emparejar cartas ocultas. Este desafío mejora la **memoria visual**, la **atención sostenida** y la **velocidad de procesamiento**.

El juego está desarrollado con **Jetpack Compose Multiplatform**, siguiendo una arquitectura basada en **Modelo–Vista–Controlador (MVC)** para garantizar una separación clara de responsabilidades y alta capacidad de testeo.

---

## Resumen de Arquitectura

### 1. `pairsModel.kt`
Contiene todo el **estado central del juego** y la **lógica de emparejamiento**.

- `allCards`: Lista con 20 valores únicos de cartas.
- `shuffledCards`: Lista duplicada y mezclada para la partida.
- `revealed`: Lista booleana que indica qué cartas están descubiertas.
- `matched`: Lista booleana que marca qué cartas han sido emparejadas.
- `selectedIndices`: Índices actualmente seleccionados por el jugador.
- `started`: Indica si la partida está en curso.
- `difficulty`: Enum de dificultad: `EASY`, `MEDIUM`, `HARD`.

#### Funciones Clave

| Función | Propósito |
|--------|-----------|
| `generateCards()` | Inicializa y mezcla las cartas en función de la dificultad. |
| `calculateEasyScore(seconds)` | Calcula la puntuación en dificultad fácil según el tiempo. |

---

### 2. `pairsController.kt`
Conecta la lógica del modelo con la interfaz de usuario.

#### Responsabilidades principales:

- Manejo del estado del juego y las interacciones del jugador.
- Gestión de selección de cartas, coincidencias y turnos.
- Cálculo de puntuación final.
- Control del estado de las instrucciones.
- Navegación entre pantallas.

#### Funciones Destacadas

- `startGame()` inicia una nueva partida.
- `selectCard(index)` procesa la selección del usuario.
- `processTurnEnd()` evalúa si hay coincidencia.
- `calculateScore(seconds)` devuelve el puntaje ajustado al nivel de dificultad.
- `toggleInstructions()` muestra u oculta la pantalla de instrucciones.

> Las vistas deben **observar los estados derivados** desde el controlador para mantener reactividad y lógica desacoplada.

---

### 3. `couplesScreen.kt`
Pantalla principal de juego.

- Renderiza:
  - Selector de dificultad
  - Puntuación actual y temporizador
  - Cuadrícula interactiva de cartas
  - Botón de inicio de partida
- Usa `LaunchedEffect` para:
  - Temporizador de 90 segundos
  - Delay de 1 segundo para cartas que no coinciden
- Muestra puntuación final al terminar o mensaje de derrota por tiempo.

### 4. `InstructionsCouples.kt`
Pantalla de instrucciones del juego.

- Presenta reglas del juego con diseño estilizado.
- Usa `AnnotatedString` para destacar partes del texto.
- Botón central de **"Comenzar partida"** para ocultar las instrucciones.

---

## Flujo del Juego

```mermaid
graph TD
  A[Usuario abre la pantalla] --> B[Pantalla de instrucciones o juego]
  B --> C{¿Juego iniciado?}
  C -- No --> D[Seleccionar dificultad]
  D --> E[Iniciar partida]
  C -- Sí --> F[Inicia temporizador]
  F --> G[Muestra cartas en cuadrícula]
  G --> H[Usuario elige 2 cartas]
  H --> I{¿Coinciden?}
  I -- Sí --> J[Marcar como emparejadas]
  I -- No --> K[Volver a ocultar]
  J & K --> G
  F --> L{¿Tiempo agotado o todo emparejado?}
  L --> M[Mostrar puntuación o mensaje de derrota]
