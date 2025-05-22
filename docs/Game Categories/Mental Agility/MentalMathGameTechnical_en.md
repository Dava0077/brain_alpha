# 🧠 Mental Math — Technical Documentation

## Overview

**Mental Math** is a **mental arithmetic game** where the player must solve 10 basic math operations (addition, subtraction, multiplication) as quickly as possible within a time limit determined by difficulty. The game rewards both **accuracy** and **speed**.

The game is built with **Jetpack Compose Multiplatform**, structured according to the **Model–View–Controller (MVC)** pattern for clear separation of responsibilities, reactivity, and easy maintainability.

---

## Architecture Summary

### 1. `mentalMathModel.kt`
Encapsulates game logic, scoring, and question generation.

- `score`: Player’s current total score.
- `currentQuestion`: Index of the current question.
- `totalQuestions`: Total questions per session (10).
- `difficulty`: Current difficulty (`EASY`, `MEDIUM`, `HARD`).
- `currentQuestionObj`: The active `Question` object.

#### Key Functions

| Function | Purpose |
|---------|---------|
| `generateQuestion()` | Creates a random question using +, −, or ×. |
| `checkAnswer(answer, time)` | Verifies the answer, updates score, and advances to the next question. |
| `calculatePoints(time)` | Calculates score based on time taken and difficulty. |
| `resetGame()` | Resets all internal game state. |
| `getTimeLimit()` | Returns the total game time based on difficulty. |
| `isGameOver()` | Returns `true` if all 10 questions have been answered. |

---

### 2. `mentalMathController.kt`
Acts as a bridge between the view and model.

- Controls:
  - Question generation.
  - Answer submission and evaluation.
  - Difficulty control.
  - Score and navigation.

#### Notable Responsibilities

| Function | Purpose |
|---------|---------|
| `startGame(callback)` | Generates a new question and calls the callback. |
| `submitAnswer(answer, time, callback)` | Evaluates the answer, calculates score, and returns feedback. |
| `resetGame()` | Resets the current session. |
| `setDifficulty(level)` | Sets the difficulty level. |
| `goHome()` | Navigates back to the main menu. |
| `isGameOver()` | Checks if the game is over. |
| `getScore()` | Returns the current score. |
| `getTotalQuestions()` | Total number of questions in the game (10). |
| `getCurrentQuestionNumber()` | Returns the current question number (1-based). |

---

### 3. `mentalMathView.kt`
Main game screen.

- Displays:
  - Top bar with navigation buttons.
  - Accumulated score and selected difficulty.
  - Countdown timer.
  - Dynamic question with input field.
  - Feedback after each answer.
  - Reset and instructions buttons.

- Also includes:
  - Difficulty selector with unlock logic based on neurons.
  - Auto-saving session to Supabase on game end.

---

### 4. `MentalInstructionsScreen.kt`
Initial instructions screen.

- Explains:
  - Game goal.
  - How scoring works based on speed and difficulty.
  - How levels unlock.
- Clear visual layout with rich text (`AnnotatedString`).
- "Back" button returns to the game.

---

## Game Flow

```mermaid
graph TD
  A[User opens the Mental Math screen] --> B[Instructions screen or game]
  B --> C{Has the game started?}
  C -- No --> D[Select difficulty]
  D --> E[Press "Start"]
  C -- Yes --> F[Generate random question]
  F --> G[Display current question]
  G --> H[Player enters answer]
  H --> I[Validate answer and calculate score]
  I --> J{Game over? (10 questions or time = 0)}
  J -- No --> F
  J -- Yes --> K[Show final score and save session]
  ```