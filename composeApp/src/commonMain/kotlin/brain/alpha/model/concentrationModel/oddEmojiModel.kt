package brain.alpha.model.concentrationModel

import brain.alpha.utils.currentTimeMillis
import kotlin.random.Random

object oddEmojiModel {

    private val emojiList = listOf("😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚")
    private val similarPairs = listOf(
        Pair("😀", "😃"), Pair("😄", "😁"), Pair("😆", "😅"), Pair("😂", "🤣"), Pair("🙂", "🙃"),
        Pair("😌", "😉"), Pair("😍", "🥰"), Pair("😘", "😗"), Pair("😙", "😚")
    )

    var level = 1
        private set
    var maxLevel = 1
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

    fun startLevel() {
        if (level > maxLevel) maxLevel = level
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

    fun reset() {
        level = 1
        maxLevel = 1
        errors = 0
        totalTime = 0
    }

    fun getAverageTime(): Double {
        return if (level > 1) totalTime.toDouble() / (level - 1) else 0.0
    }
}
