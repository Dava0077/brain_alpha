# Click Number — Technical Documentation

## Overview

Click Number is a **fast-paced mental agility minigame** where players must find and tap a randomly selected target number within a 4x4 grid of integers. Each correct tap regenerates the grid with a new target. The game runs under a countdown based on difficulty level, challenging players’ **focus**, **speed**, and **short-term memory**.

The module is implemented using **Jetpack Compose Multiplatform**, following a **Model–View–Controller (MVC)** architecture to separate state, UI, and interaction logic cleanly.

---

## Architecture Summary

### 1. `clickNumberModel.kt`
Manages grid data generation and correctness checking.

- `gridSize`: Grid is fixed at 4x4.
- `numbers`: List of 16 random integers (1–99).
- `targetNumber`: One randomly chosen number the player must tap.

#### Key Functions

| Function | Purpose |
|---------|---------|
| `startGame()` | Generates a new 4x4 grid and selects one target number. |
| `checkNumber(selected)` | Returns `true` if selected number matches the current target. |
| `getNumbers()` | Exposes the current shuffled grid to the view. |

---

### 2. `clickNumberController.kt`
Acts as the game logic manager and UI state holder.

- Manages:
    - Time countdown
    - Score
    - Difficulty
    - Instructions and end-of-game dialog
    - Navigation and game restarts

#### Notable Responsibilities

- `startGame()` initializes the game and grid.
- `handleButtonClick(number)` checks if the tap is correct, updates state and regenerates.
- `startTimer()` controls the countdown and ends the game when it hits zero.
- `resetGame()` clears all reactive state.
- `restartGame()` resets and starts a new session instantly.
- `setDifficulty(diff)` assigns timer based on difficulty.
- `goHome()` and `goHomeLogic()` handle view transitions.
- `beginClickGame()` dismisses instructions and starts session.

---

### 3. `clickNumberScreen.kt`
Main game UI implementation using Jetpack Compose.

- Displays:
    - Logo, profile, settings bar
    - Score display and difficulty selector
    - Instruction text and feedback
    - Timer and dynamic target number
    - 4x4 button grid for number selection
    - End-of-game dialog with score summary

- Supports real-time reactivity using `mutableStateOf` and `LaunchedEffect`.

---

### 4. `InstructionsClickNumber.kt`
Instruction screen shown before gameplay starts.

- Styled with `AnnotatedString` for highlighted phrases.
- Describes:
    - Objective
    - How difficulty levels affect time
    - Feedback mechanics
- Ends with a large start button to transition into gameplay.

---

## Game Flow

```mermaid
graph TD
  A[User opens Click Number screen] --> B[Instructions screen or Game]
  B --> C{Game started?}
  C -- No --> D[Select difficulty]
  D --> E[Start Game]
  C -- Yes --> F[Initialize 4x4 Grid + Target]
  F --> G[Display grid]
  G --> H[User taps number]
  H --> I{Correct tap?}
  I -- Yes --> J[+1 Score, regenerate grid and target]
  I -- No --> K[Show incorrect message]
  J & K --> G
  F --> L[Countdown timer runs]
  L --> M{Time = 0?}
  M -- Yes --> N[Show end-game dialog + Score]