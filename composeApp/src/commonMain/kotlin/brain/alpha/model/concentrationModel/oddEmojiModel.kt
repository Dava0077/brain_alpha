package brain.alpha.model.concentrationModel

import brain.alpha.model.shared.Difficulty
import brain.alpha.utils.currentTimeMillis
import kotlin.random.Random

object oddEmojiModel {

    private val similarPairs = listOf(
        Pair("😀", "😃"), Pair("😄", "😁"), Pair("😆", "😅"), Pair("😂", "🤣"), Pair("🙂", "🙃"),
        Pair("😌", "😉"), Pair("😍", "🥰"), Pair("😘", "😗"), Pair("😙", "😚")
    )

    var level = 1
        private set
    var errors = 0
        private set
    var totalTime: Long = 0
        private set

    private var startTime: Long = 0

    var gridSize = 2
        private set

    var grid: List<List<String>> = emptyList()
        private set

    var oddRow = 0
        private set
    var oddCol = 0
        private set

    var currentDifficulty: Difficulty = Difficulty.MEDIUM
        private set

    fun setDifficulty(difficulty: Difficulty) {
        currentDifficulty = difficulty
    }


    fun getMaxLevel(): Int = when (currentDifficulty) {
        Difficulty.EASY -> 5
        Difficulty.MEDIUM -> 8
        Difficulty.HARD -> 12
    }

    fun startLevel() {
        gridSize = 2 + (level / 2).coerceAtMost(6) // grows with level
        val (base, odd) = similarPairs.random()
        val oddIndex = Pair(Random.nextInt(gridSize), Random.nextInt(gridSize))
        val newGrid = List(gridSize) { row ->
            List(gridSize) { col ->
                if (row == oddIndex.first && col == oddIndex.second) odd else base
            }
        }

        grid = newGrid
        oddRow = oddIndex.first
        oddCol = oddIndex.second
        startTime = currentTimeMillis()
    }

    fun select(row: Int, col: Int): Boolean {
        return if (row == oddRow && col == oddCol) {
            val timeTaken = currentTimeMillis() - startTime
            totalTime += timeTaken
            level++
            true
        } else {
            errors++
            false
        }
    }

    fun hasFinished(): Boolean = level > getMaxLevel()

    fun reset() {
        level = 1
        errors = 0
        totalTime = 0
    }

    fun getAverageTime(): Double {
        return if (level > 1) totalTime.toDouble() / (level - 1) else 0.0
    }
}
