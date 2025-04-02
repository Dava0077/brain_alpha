package brain.alpha.model.logicModel

import kotlin.random.Random

object wordSearchModel {
    private const val GRID_SIZE = 10
    private val grid = Array(GRID_SIZE) { CharArray(GRID_SIZE) { '-' } }
    private val wordsToFind = mutableListOf<String>()
    private val allWords = listOf(
        "KOTLIN", "JAVA", "PYTHON", "CODE", "PROGRAM", "ALPHA", "BRAIN", "MATH", "LOGIC", "GAME",
        "DATA", "STRING", "ARRAY", "LOOP", "CLASS", "METHOD", "OBJECT", "FUN", "TEST", "DEBUG"
    )


    init {
        generateGame()
    }


    fun generateGame() {
        // Seleccionar 5 palabras aleatorias de la lista
        wordsToFind.clear()
        val shuffledWords = allWords.shuffled()
        wordsToFind.addAll(shuffledWords.take(5))


        // Colocar las palabras en la cuadrícula
        placeWordsInGrid()


        // Rellenar los espacios vacíos con letras aleatorias
        fillEmptySpaces()
    }


    fun placeWordsInGrid() {
        for (word in wordsToFind) {
            var placed = false
            while (!placed) {
                val direction = Random.nextInt(3) // 0: horizontal, 1: vertical, 2: diagonal
                val row = Random.nextInt(GRID_SIZE)
                val col = Random.nextInt(GRID_SIZE)


                when (direction) {
                    0 -> placed = tryPlaceHorizontal(word, row, col)
                    1 -> placed = tryPlaceVertical(word, row, col)
                    2 -> placed = tryPlaceDiagonal(word, row, col)
                }
            }
        }
    }


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


    fun fillEmptySpaces() {
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                if (grid[i][j] == '-') {
                    grid[i][j] = ('A'..'Z').random()
                }
            }
        }
    }


    fun getGrid(): Array<CharArray> = grid
    fun getWordsToFind(): List<String> = wordsToFind




    class WordSearchView {
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


        fun displayWordsToFind(words: List<String>) {
            println("\nPalabras a encontrar:")
            words.forEach { println("- $it") }
        }


        fun showMessage(message: String) {
            println(message)
        }
    }
}
