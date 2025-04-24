package brain.alpha.controller.logicController

import androidx.compose.runtime.mutableStateOf
import brain.alpha.model.logicModel.wordSearchModel
import brain.alpha.model.shared.Difficulty

object wordSearchController {
    private val model = wordSearchModel
    lateinit var navigateTo: (String) -> Unit
    private val selectedCells = mutableListOf<Pair<Int, Int>>()
    var selectionDirection: Pair<Int, Int>? = null
    val showInstructions = mutableStateOf(false)
    private var currentWords: List<String> = emptyList()


    fun selectCell(row: Int, col: Int) {
        selectedCells.add(Pair(row, col))
    }
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }
    fun hasStarted(): Boolean {
        return model.gameStarted.value
    }

    fun isGameFinished(): Boolean {
        return model.getWordsToFind().all { word -> model.isWordFound(word) }
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
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    fun clearSelection() {
        selectedCells.clear()
    }
    fun configureGame(difficulty: Difficulty) {
        currentWords = when (difficulty) {
            Difficulty.EASY -> getRandomWords(5..8)
            Difficulty.MEDIUM -> getRandomWords(10..15)
            Difficulty.HARD -> getRandomWords(20..25)
        }
    }
    private fun getRandomWords(range: IntRange): List<String> {
        val dictionary = listOf("GATO", "PERRO", "CASA", "SOL", "LUNA", "FUEGO", "TIERRA", "AGUA", "FLOR", "MONTAÑA", "BOSQUE", "RÍO", "CIELO", "NUBE", "CAMINO")
        return dictionary.shuffled().take(range.random())
    }
    private fun setupGrid(
        size: Int,
        words: List<String>,
        allowDiagonals: Boolean,
        allowReverse: Boolean
    ) {
        model.setupGrid(
            size = size,
            words = words,
            allowDiagonals = allowDiagonals,
            allowReverse = allowReverse
        )
    }

    fun getSelectedWord(): String {
        return selectedCells.map { (r, c) -> model.getGrid()[r][c] }.joinToString("")
    }
    fun isWordFound(word: String): Boolean {
        return wordSearchModel.isWordFound(word)
    }

    fun markWordAsFound(word: String) {
        wordSearchModel.markWordAsFound(word)
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
        showInstructions.value = false
    }

    fun startGame() {
        model.generateGame(currentWords)
        model.gameStarted.value = true
        model.clearFoundWords()
    }
}