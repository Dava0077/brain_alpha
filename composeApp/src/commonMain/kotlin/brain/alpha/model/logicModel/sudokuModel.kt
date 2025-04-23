package brain.alpha.model.logicModel

import brain.alpha.model.shared.Difficulty

object sudokuModel {
    private val grid = Array(9) { IntArray(9) }
    private val initialCells = Array(9) { BooleanArray(9) }
    var difficulty: Difficulty = Difficulty.EASY
    var started: Boolean = false


    val clueCount: Int
        get() = when (difficulty) {
            Difficulty.EASY -> 54
            Difficulty.MEDIUM -> 42
            Difficulty.HARD -> 30
        }

    fun generateValidBoard() {
        clearBoard()
        fillDiagonalBlocks()
        solveBoard()
        removeCells(40)
    }
    fun setCell(row: Int, col: Int, value: Int) {
        if (!initialCells[row][col]) {
            grid[row][col] = value
        }
    }

    fun getGrid(): Array<IntArray> = grid.map { it.copyOf() }.toTypedArray()
    fun isInitial(row: Int, col: Int): Boolean = initialCells[row][col]

    fun clearBoard() {
        for (row in 0..8) {
            for (col in 0..8) {
                grid[row][col] = 0
                initialCells[row][col] = false
            }
        }
    }

    fun fillDiagonalBlocks() {
        for (i in 0..6 step 3) {
            fillBlock(i, i)
        }
    }

    fun fillBlock(row: Int, col: Int) {
        val nums = (1..9).shuffled()
        var idx = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                grid[row + i][col + j] = nums[idx++]
            }
        }
    }

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