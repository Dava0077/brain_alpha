package brain.alpha.controller.logicController

import androidx.compose.runtime.mutableStateOf
import brain.alpha.controller.memoryController.pairsController
import brain.alpha.model.logicModel.sudokuModel
import brain.alpha.model.shared.Difficulty

object sudokuController {
    private val model = sudokuModel
    lateinit var navigateTo: (String) -> Unit
    val showInstructions = mutableStateOf(false)

    fun hasStarted(): Boolean = model.started
    fun startGame() {
        model.started = true
        generateNewBoard() // tu lógica actual de iniciar juego
    }
    fun resetGame() {
        model.started = false
        generateNewBoard()
    }
    fun generateNewBoard() {
        val cluesToRemove = 81 - sudokuModel.clueCount
        model.clearBoard()
        model.fillDiagonalBlocks()
        model.solveBoard()
        model.removeCells(cluesToRemove)
    }
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }
    fun goToGameScreen() {
        showInstructions.value = false
        model.started = false // asegura que se quede en la pantalla previa al inicio
    }
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }
    fun isGameStarted(): Boolean {
        return !showInstructions.value
    }

    fun setCellValue(row: Int, col: Int, value: Int) {
        model.setCell(row, col, value)
    }
    fun setDifficulty(level: Difficulty) {
        model.difficulty = level
    }

    fun getDifficulty(): Difficulty {
        return model.difficulty
    }

    fun getCellValue(row: Int, col: Int): Int {
        return model.getGrid()[row][col]
    }

    fun isCellInitial(row: Int, col: Int): Boolean {
        return model.isInitial(row, col)
    }

    fun isBoardComplete(): Boolean {
        return model.getGrid().all { row -> row.all { it in 1..9 } }
    }


    fun isBoardValid(): Boolean {
        val grid = model.getGrid()

        fun isValidGroup(group: List<Int>) =
            group.filter { it != 0 }.let { it.size == it.toSet().size }

        // Filas
        for (row in 0..8) {
            if (!isValidGroup(grid[row].toList())) return false
        }

        // Columnas
        for (col in 0..8) {
            val column = List(9) { row -> grid[row][col] }
            if (!isValidGroup(column)) return false
        }


        // Regiones 3x3
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

    fun goHomeLogic(){
        navigateTo("logic")
        showInstructions.value = false
        model.started = false
    }
    fun calculateScore(seconds: Int): Int {
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

        val multiplier = when (model.difficulty) {
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 2
            Difficulty.HARD -> 3
        }

        return baseScore * multiplier
    }
}