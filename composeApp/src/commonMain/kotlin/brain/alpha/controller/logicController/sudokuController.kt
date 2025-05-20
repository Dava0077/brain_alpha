// Package declaration for logic-level controller of the Sudoku game
package brain.alpha.controller.logicController

// Import state handling for UI
import androidx.compose.runtime.mutableStateOf

// Import other required controllers and model layers
import brain.alpha.controller.memoryController.pairsController
import brain.alpha.model.logicModel.sudokuModel
import brain.alpha.model.shared.Difficulty

// Singleton object controlling Sudoku game logic and state
object sudokuController {

    // Reference to the underlying game model
    private val model = sudokuModel

    // Lambda function used for navigation
    lateinit var navigateTo: (String) -> Unit

    // Mutable state to control the visibility of the instructions screen
    val showInstructions = mutableStateOf(false)

    // Check whether the game has already started
    fun hasStarted(): Boolean = model.started

    // Mark the game as started and generate a new board
    fun startGame() {
        model.started = true
        generateNewBoard() // tu lógica actual de iniciar juego
    }

    // Reset the game state and generate a new board
    fun resetGame() {
        model.started = false
        generateNewBoard()
    }

    // Generates a new Sudoku board by solving a complete one and removing cells
    fun generateNewBoard() {
        val cluesToRemove = 81 - sudokuModel.clueCount
        model.clearBoard()
        model.fillDiagonalBlocks()
        model.solveBoard()
        model.removeCells(cluesToRemove)
    }

    // Begin the game by hiding instructions and marking as started
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    // Prepare for game screen navigation, reset game state without starting
    fun goToGameScreen() {
        showInstructions.value = false
        model.started = false // asegura que se quede en la pantalla previa al inicio
    }

    // Toggle whether the instructions are shown
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }

    // Returns true if the game screen should be shown (instructions hidden)
    fun isGameStarted(): Boolean {
        return !showInstructions.value
    }

    // Set a specific cell's value in the model grid
    fun setCellValue(row: Int, col: Int, value: Int) {
        model.setCell(row, col, value)
    }

    // Set the current difficulty level in the model
    fun setDifficulty(level: Difficulty) {
        model.difficulty = level
    }

    // Get the current difficulty level from the model
    fun getDifficulty(): Difficulty {
        return model.difficulty
    }

    // Retrieve the value from a specific cell
    fun getCellValue(row: Int, col: Int): Int {
        return model.getGrid()[row][col]
    }

    // Check if a specific cell is an initial (non-editable) value
    fun isCellInitial(row: Int, col: Int): Boolean {
        return model.isInitial(row, col)
    }

    // Check if the board is fully filled with values from 1 to 9
    fun isBoardComplete(): Boolean {
        return model.getGrid().all { row -> row.all { it in 1..9 } }
    }

    // Validate that the board follows Sudoku rules (no duplicates in rows, cols, boxes)
    fun isBoardValid(): Boolean {
        val grid = model.getGrid()

        // Helper function to check if a group contains unique non-zero values
        fun isValidGroup(group: List<Int>) =
            group.filter { it != 0 }.let { it.size == it.toSet().size }

        // Validate all rows
        for (row in 0..8) {
            if (!isValidGroup(grid[row].toList())) return false
        }

        // Validate all columns
        for (col in 0..8) {
            val column = List(9) { row -> grid[row][col] }
            if (!isValidGroup(column)) return false
        }


        // Validate all 3x3 subgrids
        for (boxRow in 0..2) {
            for (boxCol in 0..2) {
                val box = mutableListOf<Int>()
                for (i in 0..2) {
                    for (j in 0..2) {
                        box += grid[boxRow * 3 + i][boxCol * 3 + j]
                    }
                }
                if (!isValidGroup(box)) return false
            }
        }

        return true
    }

    // Return to the logic home screen and reset game state
    fun goHomeLogic(){
        navigateTo("logic")
        showInstructions.value = false
        model.started = false
    }

    // Calculate score based on difficulty level and time taken
    fun calculateScore(seconds: Int): Int {
        // Determine base score based on time thresholds for each difficulty
        val baseScore = when (model.difficulty) {
            Difficulty.EASY -> {
                when {
                    seconds <= 150 -> 10
                    seconds <= 210 -> 7
                    seconds <= 300 -> 5
                    else -> 3
                }
            }
            Difficulty.MEDIUM -> {
                when {
                    seconds <= 240 -> 10
                    seconds <= 330 -> 7
                    seconds <= 450 -> 5
                    else -> 3
                }
            }
            Difficulty.HARD -> {
                when {
                    seconds <= 360 -> 10
                    seconds <= 480 -> 7
                    seconds <= 600 -> 5
                    else -> 3
                }
            }
        }

        // Score multiplier based on difficulty
        val multiplier = when (model.difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 2
            Difficulty.HARD -> 3
        }

        // Final score = base score × multiplier
        return baseScore * multiplier
    }
}