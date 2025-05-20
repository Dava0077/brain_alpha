# 🧠 Memory Match Game — Technical Documentation

## Overview

The Memory Match Game is a **visual memory-based minigame** where players must uncover and match pairs of hidden cards. It challenges players' **concentration**, **attention span**, and **pattern recognition** skills.

The game is implemented using **Jetpack Compose Multiplatform** with a clean **Model–View–Controller (MVC)** architecture for separation of concerns and testability.

---

## Architecture Summary

### 1. `pairsModel.kt`
Contains all core game data and logic.

- `allCards`: Pool of 20 unique card values.
- `shuffledCards`: Duplicated and shuffled card list.
- `revealed`: Boolean list tracking which cards are face-up.
- `matched`: Boolean list tracking which cards are matched.
- `selectedIndices`: Tracks current pair of selected cards.
- `started`: Indicates if a game is in progress.
- `difficulty`: Enum value — `EASY`, `MEDIUM`, or `HARD`.

#### Key Functions

| Function | Purpose |
|---------|---------|
| `generateCards()` | Initializes and shuffles the card pairs based on difficulty. |
| `calculateEasyScore(seconds)` | Computes score for EASY mode based on elapsed time. |

---

### 2. `pairsController.kt`
Acts as the bridge between UI and model logic. Handles:

- Game state flow and user interactions.
- Card selection, match checking, and turn resets.
- Score calculation and difficulty management.
- UI logic for showing/hiding instructions.
- Navigation to home screen.

#### Notable Responsibilities

- `startGame()` sets up a new session.
- `selectCard(index)` handles user tap logic.
- `processTurnEnd()` evaluates pair correctness.
- `calculateScore(seconds)` returns score adjusted by difficulty multiplier.
- `toggleInstructions()` controls visibility of the instruction screen.

> View layers bind to controller state to maintain reactive updates and logic encapsulation.

---

### 3. `couplesScreen.kt`
The main game UI screen.

- Renders:
  - Difficulty selection
  - Current score and timer
  - Grid of cards
  - Play/start button
- Uses `LaunchedEffect` for:
  - Timer countdown (max 90 seconds)
  - Turn delays (1 second before flipping non-matching cards)
- Displays end-of-game result and score on completion or timeout.

### 4. `InstructionsCouples.kt`
- Provides an instructional screen with stylized layout.
- Uses `AnnotatedString` for bold highlights in rules.
- Shows a single "Start Game" CTA that hides instructions and launches the game.

---

## Game Flow

```mermaid
graph TD
  A[User opens screen] --> B[Instructions screen or Game]
  B --> C{Game started?}
  C -- No --> D[Select difficulty]
  D --> E[Start game]
  C -- Yes --> F[Timer begins]
  F --> G[Show cards grid]
  G --> H[User selects 2 cards]
  H --> I{Match?}
  I -- Yes --> J[Mark matched]
  I -- No --> K[Flip cards back]
  J & K --> G
  F --> L{Time >= 90s OR All matched?}
  L --> M[Show score or defeat screen]
