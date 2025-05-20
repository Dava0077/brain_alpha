package brain.alpha.controller.logicController

import androidx.compose.runtime.mutableStateOf
import brain.alpha.model.logicModel.wordSearchModel
import brain.alpha.model.shared.Difficulty

// Controller for managing the Word Search game logic and interaction with the model
object wordSearchController {
    // Reference to the model that handles the actual game data
    private val model = wordSearchModel
    // Callback function to navigate between screens
    lateinit var navigateTo: (String) -> Unit
    // List of currently selected cells on the grid
    private val selectedCells = mutableListOf<Pair<Int, Int>>()
    // Direction of selection (e.g., horizontal, vertical, diagonal)
    var selectionDirection: Pair<Int, Int>? = null
    // State controlling whether the instructions should be shown
    val showInstructions = mutableStateOf(false)
    // Words selected for the current game session
    private var currentWords: List<String> = emptyList()


    // Add a cell to the current selection
    fun selectCell(row: Int, col: Int) {
        selectedCells.add(Pair(row, col))
    }
    // Toggle the instruction screen visibility
    fun toggleInstructions(show: Boolean) {
        showInstructions.value = show
    }
    // Check if the game has started
    fun hasStarted(): Boolean {
        return model.gameStarted.value
    }
    // Set the game difficulty level
    fun setDifficulty(level: Difficulty) {
        model.difficulty = level
    }

    // Check if all target words have been found
    fun isGameFinished(): Boolean {
        return model.getWordsToFind().all { word -> model.isWordFound(word) }
    }

    // Clear the current word selection and direction
    fun resetSelection() {
        selectedCells.clear()
        selectionDirection = null
    }

    // Determine if a cell can be selected (only adjacent to last selected)
    fun canSelect(row: Int, col: Int): Boolean {
        if (selectedCells.isEmpty()) return true
        val (lastRow, lastCol) = selectedCells.last()
        val rowDiff = kotlin.math.abs(row - lastRow)
        val colDiff = kotlin.math.abs(col - lastCol)
        return rowDiff <= 1 && colDiff <= 1 && (row != lastRow || col != lastCol)
    }
    // Begin the game from instructions screen
    fun beginGame() {
        showInstructions.value = false
        startGame()
    }

    // Clear selected cells (used when canceling a word selection)
    fun clearSelection() {
        selectedCells.clear()
    }

    // Set up the currentWords list based on the selected difficulty
    fun configureGame(difficulty: Difficulty) {
        currentWords = when (difficulty) {
            Difficulty.EASY -> getRandomWords(5..8)
            Difficulty.MEDIUM -> getRandomWords(10..15)
            Difficulty.HARD -> getRandomWords(20..25)
        }
    }
    // Pick a random set of words from a dictionary based on a given range
    private fun getRandomWords(range: IntRange): List<String> {
        val dictionary = listOf("GATO", "PERRO", "CASA", "SOL", "LUNA", "FUEGO", "TIERRA", "AGUA", "FLOR", "PARAISO", "BOSQUE", "RIO", "CIELO", "NUBE", "CAMINO")
        return dictionary.shuffled().take(range.random())
    }

    // Helper to initialize the game grid with specified configuration
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

    // Build the selected word string based on the selected cells
    fun getSelectedWord(): String {
        return selectedCells.map { (r, c) -> model.getGrid()[r][c] }.joinToString("")
    }

    // Check if a given word has already been found
    fun isWordFound(word: String): Boolean {
        return wordSearchModel.isWordFound(word)
    }

    // Mark a word as found in the model
    fun markWordAsFound(word: String) {
        wordSearchModel.markWordAsFound(word)
    }

    // Validate if the selected word is correct (exists in list)
    fun isWordCorrect(): Boolean {
        val word = getSelectedWord()
        return model.getWordsToFind().contains(word)
    }

    // Return currently selected cells
    fun getSelectedCells(): List<Pair<Int, Int>> = selectedCells
    // Return the current letter grid
    fun getGrid() = model.getGrid()
    // Return the list of words to find
    fun getWordsToFind() = model.getWordsToFind()

    // Navigate to logic menu (does not reset game)
    fun goHomeLogic() {
        navigateTo("logic") // Solo navegamos, sin reiniciar el modelo
        showInstructions.value = false
    }

    // Start a new game and reset game state
    fun startGame() {
        model.generateGame(currentWords)
        model.gameStarted.value = true
        model.clearFoundWords()
    }

    // Calculate score based on difficulty and completion time
    fun calculateScore(seconds: Int): Int{
        val baseScore = when (model.difficulty){
            Difficulty.EASY ->{
                when{
                    seconds <= 30 -> 10
                    seconds <= 60 -> 7
                    seconds <= 90 -> 5
                    else -> 3
                }
            }Difficulty.MEDIUM ->{
                when{
                    seconds <= 60 -> 10
                    seconds <= 90 -> 7
                    seconds <= 120 -> 5
                    else ->3
                }
            }Difficulty.HARD ->{
                when{
                    seconds <= 90 -> 10
                    seconds <= 120 -> 7
                    seconds <= 150 -> 5
                    else ->3
                }
            }
        }
        val multiplier = when (model.difficulty){
            Difficulty.EASY -> 1
            Difficulty.MEDIUM -> 2
            Difficulty.HARD -> 3
        }
        return baseScore * multiplier
    }
}