// Package declaration for logic-level Sudoku model
package brain.alpha.model.logicModel

// Import the Difficulty enum used to determine the game level
import brain.alpha.model.shared.Difficulty

// Singleton object to manage Sudoku board state and logic
object sudokuModel {

    // 9x9 grid representing the Sudoku board values
    private val grid = Array(9) { IntArray(9) }

    // 9x9 grid to track which cells are initial (not editable)
    private val initialCells = Array(9) { BooleanArray(9) }

    // Current difficulty level of the game
    var difficulty: Difficulty = Difficulty.EASY

    // Flag to indicate whether the game has started
    var started: Boolean = false

    // Computed property that returns the number of clues based on the difficulty
    val clueCount: Int
        get() = when (difficulty) {
            Difficulty.EASY -> 54
            Difficulty.MEDIUM -> 42
            Difficulty.HARD -> 30
        }

    // Generates a complete and valid Sudoku board and removes cells to create a puzzle
    fun generateValidBoard() {
        clearBoard()            // Reset the board
        fillDiagonalBlocks()    // Fill diagonal 3x3 blocks
        solveBoard()            // Solve the full board
        removeCells(40)   // Remove cells to make it a playable puzzle
    }

    // Set a value in a cell if it's not marked as initial
    fun setCell(row: Int, col: Int, value: Int) {
        if (!initialCells[row][col]) {
            grid[row][col] = value
        }
    }

    // Return a deep copy of the current board state
    fun getGrid(): Array<IntArray> = grid.map { it.copyOf() }.toTypedArray()

    // Check if a cell was part of the original puzzle (initial)
    fun isInitial(row: Int, col: Int): Boolean = initialCells[row][col]

    // Clear the board and reset initial cells
    fun clearBoard() {
        for (row in 0..8) {
            for (col in 0..8) {
                grid[row][col] = 0
                initialCells[row][col] = false
            }
        }
    }

    // Fill the 3 diagonal 3x3 blocks with random values
    fun fillDiagonalBlocks() {
        for (i in 0..6 step 3) {
            fillBlock(i, i)
        }
    }

    // Fill a 3x3 block starting at the given row and column with shuffled numbers
    fun fillBlock(row: Int, col: Int) {
        val nums = (1..9).shuffled()
        var idx = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                grid[row + i][col + j] = nums[idx++]
            }
        }
    }

    // Check whether a number can be safely placed at the given position
    fun isSafe(row: Int, col: Int, num: Int): Boolean {
        for (x in 0..8) if (grid[row][x] == num || grid[x][col] == num) return false
        val startRow = row / 3 * 3
        val startCol = col / 3 * 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (grid[startRow + i][startCol + j] == num) return false
            }
        }
        return true
    }

    // Recursive backtracking algorithm to solve the Sudoku board
    fun solveBoard(): Boolean {
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] == 0) {
                    for (num in 1..9) {
                        if (isSafe(row, col, num)) {
                            grid[row][col] = num
                            if (solveBoard()) return true
                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    // Randomly remove cells from the board to create the puzzle
    fun removeCells(count: Int) {
        var removed = 0
        while (removed < count) {
            val row = (0..8).random()
            val col = (0..8).random()
            if (grid[row][col] != 0) {
                grid[row][col] = 0
                removed++
            }
        }
        // Marcar las celdas restantes como iniciales
        for (row in 0..8) {
            for (col in 0..8) {
                if (grid[row][col] != 0) {
                    initialCells[row][col] = true
                }
            }
        }
    }
}