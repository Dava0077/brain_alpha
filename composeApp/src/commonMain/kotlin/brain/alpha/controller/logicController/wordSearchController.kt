package brain.alpha.controller.logicController

import brain.alpha.model.logicModel.wordSearchModel

object wordSearchController {
    private val model = wordSearchModel
    lateinit var navigateTo: (String) -> Unit
    private val selectedCells = mutableListOf<Pair<Int, Int>>()
    var selectionDirection: Pair<Int, Int>? = null

    fun selectCell(row: Int, col: Int) {
        selectedCells.add(Pair(row, col))
    }

    fun resetSelection() {
        selectedCells.clear()
        selectionDirection = null
    }

    fun canSelect(row: Int, col: Int): Boolean {
        if (selectedCells.isEmpty()) return true
        val (lastRow, lastCol) = selectedCells.last()
        val rowDiff = kotlin.math.abs(row - lastRow)
        val colDiff = kotlin.math.abs(col - lastCol)
        return rowDiff <= 1 && colDiff <= 1 && (row != lastRow || col != lastCol)
    }

    fun clearSelection() {
        selectedCells.clear()
    }

    fun getSelectedWord(): String {
        return selectedCells.map { (r, c) -> model.getGrid()[r][c] }.joinToString("")
    }

    fun isWordCorrect(): Boolean {
        val word = getSelectedWord()
        return model.getWordsToFind().contains(word)
    }

    fun getSelectedCells(): List<Pair<Int, Int>> = selectedCells
    fun getGrid() = model.getGrid()
    fun getWordsToFind() = model.getWordsToFind()

    fun goHomeLogic() {
        navigateTo("logic") // Solo navegamos, sin reiniciar el modelo
    }

    fun startGame() {
        model.generateGame() // Genera un nuevo juego con nuevas palabras
    }
}