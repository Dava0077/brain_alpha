# Sudoku Game — Technical Documentation

## Overview

The Sudoku Game is a **logic-based number puzzle** that challenges players to complete a 9x9 grid using deduction and reasoning. Each row, column, and 3x3 subgrid must contain all digits from 1 to 9 with no repetitions.

This module is implemented using **Jetpack Compose Multiplatform**, following a **Model–View–Controller (MVC)** architecture to separate logic, UI, and interaction handling cleanly.

---

## Architecture Summary

### 1. `sudokuModel.kt`
Manages the entire board state and puzzle generation.

- `grid`: 9x9 matrix representing the current Sudoku board.
- `initialCells`: Flags marking cells that can't be edited (given clues).
- `difficulty`: Enum — `EASY`, `MEDIUM`, `HARD`.
- `started`: Boolean flag to track game status.
- `clueCount`: Calculated based on difficulty.

#### Key Functions

| Function | Purpose |
|---------|---------|
| `generateValidBoard()` | Creates and configures a solvable Sudoku board. |
| `solveBoard()` | Solves the current grid via backtracking. |
| `removeCells(count)` | Removes values to set puzzle difficulty. |
| `setCell(row, col, value)` | Places a value if cell is not an initial. |
| `getGrid()` | Returns a snapshot of the board. |
| `isInitial(row, col)` | Checks if a cell is fixed. |
| `clearBoard()` | Resets all cells to 0 and clears states. |
| `fillDiagonalBlocks()` | Seeds the diagonal subgrids randomly. |
| `isSafe(row, col, num)` | Validates if a number is allowed in a cell. |

---

### 2. `sudokuController.kt`
Acts as the bridge between the UI and the game logic.

- Tracks difficulty and instruction visibility.
- Initializes game session and manages navigation.
- Updates cell values.
- Performs board validation and scoring.

#### Notable Responsibilities

- `startGame()` initializes a game session and board.
- `setCellValue(row, col, value)` writes a value into the grid.
- `isCellInitial(row, col)` returns editability of a cell.
- `isBoardComplete()` checks if all cells are filled.
- `isBoardValid()` validates solution correctness.
- `calculateScore(seconds)` computes score based on difficulty and speed.
- `toggleInstructions(show)` toggles help UI.
- `goHomeLogic()` returns to logic game selection.

---

### 3. `sudokuScreen.kt`
Main game interface implementation.

- Renders:
    - 9x9 grid with editable and fixed cells.
    - Timer based on difficulty.
    - Number selection buttons.
    - Score, instructions, and difficulty controls.
- Handles win/loss feedback, UI coloring, and cell selection.
- Supports responsive layouts and theme styling.

### 4. `InstructionsSudoku.kt`
- Renders onboarding content for new players.
- Uses `AnnotatedString` to highlight game mechanics.
- Includes start button and navigation header.
- Styled consistently with game layout.

---

## Game Flow

```mermaid
graph TD
  A[User opens Sudoku screen] --> B[Instructions screen or Game]
  B --> C{Game started?}
  C -- Yes --> D[Board generation]
  C -- No --> E[Difficulty selection]
  E --> F[Start Game]
  D --> G[Player fills cells]
  G --> H{Board complete?}
  H -- No --> G
  H -- Yes --> I[Validate board]
  I --> J{Valid?}
  J -- Yes --> K[Score + Save game]
  J -- No --> L[Show error]
  D --> M{Time runs out}
  M --> N[Game lost dialog]
