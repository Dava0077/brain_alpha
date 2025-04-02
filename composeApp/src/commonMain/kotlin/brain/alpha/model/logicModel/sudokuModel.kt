package brain.alpha.model.logicModel

object sudokuModel {
    data class Cell(var value: Int, val isInitial: Boolean)

    private val board: Array<Array<Cell>> = Array(9) {
        Array(9) { Cell(0, false) }
    }

    fun initBoard() {
        // Carga simple: Fijamos algunos valores como ejemplo
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val isFixed = (0..1).random() == 0 && (row + col) % 4 == 0
                val value = if (isFixed) ((1..9).random()) else 0
                board[row][col] = Cell(value, isFixed)
            }
        }
    }

    fun getValue(row: Int, col: Int): Int = board[row][col].value

    fun isInitial(row: Int, col: Int): Boolean = board[row][col].isInitial

    fun setValue(row: Int, col: Int, value: Int) {
        if (!board[row][col].isInitial) {
            board[row][col].value = value
        }
    }
}