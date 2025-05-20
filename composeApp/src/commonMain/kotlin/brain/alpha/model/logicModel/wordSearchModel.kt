// Package declaration for the logic layer of the word search game
package brain.alpha.model.logicModel

// Import Compose state handling
import androidx.compose.runtime.mutableStateOf

// Import difficulty enum
import brain.alpha.model.shared.Difficulty

// Import for Kotlin serialization
import kotlinx.serialization.Serializable

// Import random number generator
import kotlin.random.Random

// Serializable data class to represent a game session's score
@Serializable
data class GameSession(
    val score: Int
)

// Singleton object managing the word search game logic
object wordSearchModel {

    // Size of the grid (10x10)
    private const val GRID_SIZE = 10

    // 2D character array representing the word search grid
    private val grid = Array(GRID_SIZE) { CharArray(GRID_SIZE) { '-' } }

    // List of words that need to be found
    private val wordsToFind = mutableListOf<String>()

    // Current difficulty setting
    var difficulty: Difficulty = Difficulty.EASY

    // Master list of all possible words to choose from
    private val allWords = listOf(
        "KOTLIN", "JAVA", "PYTHON", "CODE", "PROGRAM", "ALPHA", "BRAIN", "MATH", "LOGIC", "GAME",
        "DATA", "STRING", "ARRAY", "LOOP", "CLASS", "METHOD", "OBJECT", "FUN", "TEST", "DEBUG"
    )

    // Mutable state to track whether the game has started
    val gameStarted = mutableStateOf(false)

    // Check if all words have been found
    fun isGameFinished(): Boolean {
        return wordsToFind.isNotEmpty() && wordsToFind.all { isWordFound(it) }
    }

    // Clear the list of words that have been found
    fun clearFoundWords() {
        foundWords.clear()
    }

    // Mark the game as not started
    fun finishGame() {
        gameStarted.value = false
    }

    // Set of words that the user has already found
    private val foundWords = mutableSetOf<String>()

    // Check if a specific word has been found
    fun isWordFound(word: String): Boolean {
        return foundWords.contains(word)
    }

    // Mark a word as found by adding it to the set
    fun markWordAsFound(word: String) {
        foundWords.add(word)
    }

    // Prepare the grid with parameters (currently placeholder)
    fun setupGrid(size: Int, words: List<String>, allowDiagonals: Boolean, allowReverse: Boolean) {
        // Crea la matriz, distribuye las palabras según los parámetros
        // Llenar el resto con letras aleatorias
    }

    // Generate a new word search game with a given list of words
    fun generateGame(customWords: List<String>) {
        wordsToFind.clear()
        wordsToFind.addAll(customWords)

        // Clear the entire grid
        for (i in 0 until grid.size) {
            for (j in 0 until grid[i].size) {
                grid[i][j] = '-'
            }
        }

        // Place words and fill remaining empty spaces
        placeWordsInGrid()
        fillEmptySpaces()
        gameStarted.value = true
    }

    // Attempt to place all selected words in the grid
    fun placeWordsInGrid() {
        for (word in wordsToFind) {
            var placed = false
            var attempts = 0
            val maxAttempts = 50 // Max attempts to avoid infinite loops

            // Try placing the word in random positions and directions
            while (!placed && attempts < maxAttempts) {
                val direction = Random.nextInt(3) // 0: horizontal, 1: vertical, 2: diagonal
                val row = Random.nextInt(GRID_SIZE)
                val col = Random.nextInt(GRID_SIZE)

                // Try placing in chosen direction
                when (direction) {
                    0 -> placed = tryPlaceHorizontal(word, row, col)
                    1 -> placed = tryPlaceVertical(word, row, col)
                    2 -> placed = tryPlaceDiagonal(word, row, col)
                }
                attempts++
            }

            // Print message if word couldn't be placed
            if (!placed) {
                println("No se pudo colocar la palabra: $word")
            }
        }
    }

    // Try placing a word horizontally at a given position
    fun tryPlaceHorizontal(word: String, row: Int, col: Int): Boolean {
        if (col + word.length > GRID_SIZE) return false
        for (i in word.indices) {
            if (grid[row][col + i] != '-' && grid[row][col + i] != word[i]) return false
        }
        for (i in word.indices) {
            grid[row][col + i] = word[i]
        }
        return true
    }

    // Try placing a word vertically at a given position
    fun tryPlaceVertical(word: String, row: Int, col: Int): Boolean {
        if (row + word.length > GRID_SIZE) return false
        for (i in word.indices) {
            if (grid[row + i][col] != '-' && grid[row + i][col] != word[i]) return false
        }
        for (i in word.indices) {
            grid[row + i][col] = word[i]
        }
        return true
    }

    // Try placing a word diagonally at a given position
    fun tryPlaceDiagonal(word: String, row: Int, col: Int): Boolean {
        if (row + word.length > GRID_SIZE || col + word.length > GRID_SIZE) return false
        for (i in word.indices) {
            if (grid[row + i][col + i] != '-' && grid[row + i][col + i] != word[i]) return false
        }
        for (i in word.indices) {
            grid[row + i][col + i] = word[i]
        }
        return true
    }

    // Fill all empty grid cells (denoted by '-') with random capital letters
    fun fillEmptySpaces() {
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                if (grid[i][j] == '-') {
                    grid[i][j] = ('A'..'Z').random()
                }
            }
        }
    }

    // Return the current grid
    fun getGrid(): Array<CharArray> = grid

    // Return the list of words to find
    fun getWordsToFind(): List<String> = wordsToFind

    // View class for printing the grid and word list to the console
    class WordSearchView {

        // Display the grid in console with indexes
        fun displayGrid(grid: Array<CharArray>) {
            println("=== Sopa de Letras ===")
            print("  ")
            for (i in grid.indices) {
                print(" $i ")
            }
            println()
            for (i in grid.indices) {
                print("$i ")
                for (j in grid[i].indices) {
                    print("${grid[i][j]}  ")
                }
                println()
            }
        }

        // Display the list of words to find
        fun displayWordsToFind(words: List<String>) {
            println("\nPalabras a encontrar:")
            words.forEach { println("- $it") }
        }

        // Print a message to the console
        fun showMessage(message: String) {
            println(message)
        }
    }
}